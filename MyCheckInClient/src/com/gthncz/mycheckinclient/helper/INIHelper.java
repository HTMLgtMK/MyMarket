package com.gthncz.mycheckinclient.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 配置文件读写帮助
 * @author GT
 *
 */
public class INIHelper {

	private static final String DIR = "ini";
	/*配置文件名*/
	private static final String INI_NAME = "checinclient.ini";
	
	/**
	 * 写入配置
	 * @param map
	 */
	public static boolean writeIni(HashMap<String, String> map) {
		File dir = new File(DIR);
		if(!dir.exists()) {
			if(!dir.mkdir()) {
				return false;
			}
		}
		File file = new File(dir, INI_NAME);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		FileOutputStream fos = null;
		BufferedWriter writer = null;
		try {
			fos = new FileOutputStream(file);
			writer = new BufferedWriter(new OutputStreamWriter(fos));
			
			Iterator<String> it = map.keySet().iterator();
			while(it.hasNext()) {
				String key =  it.next();
				String value = map.get(key);
				writer.write(key+"="+value);
				writer.newLine();
			}
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(writer != null) {
					writer.close();
				}
				if(fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * 读取配置信息
	 * @return
	 */
	public static HashMap<String, String> getIniSet(){
		File dir = new File(DIR);
		if(!dir.exists()) {
			return null;
		}
		File file = new File(dir, INI_NAME);
		if(!file.exists()) {
			return null;
		}
		HashMap<String, String> map = new HashMap<>();
		FileInputStream fis = null;
		BufferedReader reader = null;
		try {
			fis = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while((line = reader.readLine())!=null) {
				if(line.length() == 0) continue;
				String[] ini = line.split("=");
				map.put(ini[0], ini[1]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader != null) {
					reader.close();
				}
				if(fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
}
