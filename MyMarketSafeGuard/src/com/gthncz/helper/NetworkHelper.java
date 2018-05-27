package com.gthncz.helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 网络帮助类
 * @author GT
 *
 */
public class NetworkHelper {
	
	
	public static String downloadString(String spec, HashMap<String, String> map, String method) {
		return downloadString(spec,map,method,false);
	}
	
	/**
	 * 获取字符串
	 * @param spec 资源地址
	 * @param map 需要上传的参数
	 * @param method 请求方式,GET or POST
	 * @param debug  为true则输出请求url
	 * @return 若出错，则按照JSON格式返回错误字符串
	 * {
	 * 'code': status_code
	 * 'msg': 'msgs'
	 * 'data':{ obj }
	 * }
	 */
	public static String downloadString(String spec, HashMap<String, String> map, String method, boolean debug) {
		StringBuilder stringBuilder = new StringBuilder();
		HttpURLConnection connection = null;
		try {
			URL url = new URL(spec);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod(method);
			connection.setConnectTimeout(60*1000);
			connection.setReadTimeout(60*1000);
			connection.connect();
			//提交数据
			if(map!=null) {//只有当map不为空时认为有数据提交
				DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
				Iterator<String> it = map.keySet().iterator();
				while(it.hasNext()) {
					StringBuilder builder = new StringBuilder();
					String key = it.next();
					String value = map.get(key);
					builder.append(key);
					builder.append('=');
					builder.append(value);
					builder.append('&');
					dos.write(builder.toString().getBytes()); // 不能使用writeBytes(String)方法, 会出现中文不能传输的问题。
					if(debug) {
						Logger.getLogger(NetworkHelper.class.getSimpleName()).log(Level.INFO , key+"="+value);
					}
				}
				dos.flush();
				dos.close();
			}
			if(connection.getResponseCode() != 200) {//状态码错误
				//按照JSON格式返回数据
				stringBuilder.append("{\"code\":-1, \"msg\":\""
						+connection.getResponseMessage()+"\", \"data\":\"\"}");
			}else {
				//获取数据
				InputStreamReader is = new InputStreamReader(connection.getInputStream(), "UTF-8");// 必须指定编码, 否则接收的数据乱码
				BufferedReader reader = new BufferedReader(is);
				char[] buffer = new char[512];
				int len = 0;
				while((len=reader.read(buffer))!=-1) {
					stringBuilder.append(buffer, 0, len);
				}
				reader.close();
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			connection.disconnect();
			return "{\"code\":-2, \"msg\":\""+e.getMessage()+"\", \"data\":\"\"}";
		} finally {
			if(connection!=null) {
				connection.disconnect();
			}
		}
		return stringBuilder.toString();
	}
	
}
