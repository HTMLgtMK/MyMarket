package com.gthncz.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gthncz.beans.Params;
import com.gthncz.beans.SimpleGoodsBean;
import com.gthncz.helper.ClientDBHelper;
import com.gthncz.helper.INIHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 服务
 * @author GT
 *
 */
public class GoodsMarkerService {
	private static final String TAG = GoodsMarkerService.class.getSimpleName();
	
	/**服务器服务 
	 * GoodsMarkerService
	 * 1. open Socket on ip:8686
	 * 2. while(inventionFlag)
	 * 3. 	listening on port
	 * 4. 	get new Request, add to TaskQueue, release semaphore
	 * 
	 * HandlerTask ThreadPool(10) 
	 * 1. start threadPool, aquire task semaphore	
	 * 2. receive Request, aquire thread semaphore, new Thread handle Request
	 * 3. release thread semaphore on finish handle Request
	 * 
	 * */
	private ExecutorService executorService;
	private int nThreads;
	private static final int DEAFULT_THREAD_COUNT = 5;
	
	private ServerThread listenerThread;
	private Semaphore startServerSemaphore;
	// 服务停止的信号量
	private Semaphore stopedServerSemaphore;
	
	public GoodsMarkerService() {
		this(DEAFULT_THREAD_COUNT);
	}
	
	public GoodsMarkerService(int nThreads) {
		this.nThreads = nThreads;
	}
	
	/** 开始业务逻辑 */
	public boolean startService() {
		startServerSemaphore = new Semaphore(0);
		try { // 初始化数据库
			ClientDBHelper.getInstance().init();
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.getLogger(TAG).log(Level.INFO, String.format("%s %d %s", e.getMessage(), e.getErrorCode(), e.getSQLState()));
			return false;
		}
		executorService = Executors.newFixedThreadPool(nThreads);
		openListener();
		stopedServerSemaphore = new Semaphore(0);
		try {
			startServerSemaphore.acquire(); // block thread
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return listenerThread.isListening();
	}
	
	public boolean isRunning() {
		if(listenerThread == null) return false;
		return listenerThread.isListening();
	}
	
	public void stopService() {
		ClientDBHelper.getInstance().close();
		listenerThread.stopListener();
		executorService.shutdown();
		try {
			stopedServerSemaphore.acquire(); // 阻塞
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** 开启监听 */
	private void openListener() {
		HashMap<String, String> ini = INIHelper.getIniSet(Params.INI_NAME);
		int port = 0;
		if(ini != null) {
			String portS = ini.get("server_port") ;
			if(portS != null) {
				port = Integer.valueOf(portS);
			}else {
				port = ServerThread.DEFAULT_PORT;
			}
		}
		listenerThread = new ServerThread(port);
		listenerThread.start();
	}
	
	/**服务器监听线程 */
	private class ServerThread extends Thread{
		
		private boolean listenerFlag;
		private int port; // 监听端口
		public static final int DEFAULT_PORT = 8686;
		
		public ServerThread(int port) {
			listenerFlag = false;
			this.port = port;
		}
		
		public boolean isListening() {
			return listenerFlag;
		}

		public void stopListener() { // 再没有请求时，还是会阻塞
			listenerFlag = false;
		}
		
		@Override
		public void run() {
			listenerFlag = true;
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(port);
				serverSocket.setSoTimeout(10000); // 10s自动退出阻塞向后运行
			} catch (IOException e) {
				e.printStackTrace();
				// return
				listenerFlag = false;
			}
			startServerSemaphore.release();
			while(listenerFlag) { // 不断接收请求
				Socket socket = null;
				try {
					socket = serverSocket.accept();
				} catch (IOException e) {
//					e.printStackTrace(); // TODO 不输出异常
					continue; // 跳过
				}
				// 接收数据, 生成任务, 发送到处理线程
				if(socket == null) continue;
				handleSocket(socket);
			}
			try {
				if(serverSocket != null) {
					serverSocket.close(); // 关闭监听
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			stopedServerSemaphore.release(); // 表示这个服务已经停止
		}
		
		private void handleSocket(Socket socket) {
			if(executorService != null 
					&& !executorService.isShutdown() && !executorService.isTerminated()) {
				GoodsMarkerTask task = new GoodsMarkerTask(socket);
				executorService.execute(task); // 添加到线程池处理
			}
		}
		
	}
	
	/**
	 * 标记商品状态任务
	 * @author GT
	 *
	 */
	private class GoodsMarkerTask implements Runnable{
		
		private Socket  socket;
		
		public GoodsMarkerTask(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			Logger.getLogger(TAG).log(Level.INFO, "获取到新 Request !");
			InputStream is = null;
			InputStreamReader reader = null;
			try {
				is = socket.getInputStream();
				reader = new InputStreamReader(is, "UTF-8");
				 // 获取提交的所有商品
				StringBuilder builder = new StringBuilder();
				int len = 0;
				char[] buffer = new char[1024];
				while((len = reader.read(buffer)) != -1) {
					builder.append(buffer, 0, len);
				}
				Logger.getLogger(TAG).log(Level.INFO, " Request :" + builder.toString());
				// 解析JSON数据
				ArrayList<SimpleGoodsBean> list = parseJSON(builder.toString());
				// 写入数据库
				markGoods(list);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if(is != null) {
						is.close();
					}
					if(reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * 标记商品状态
		 * @param list
		 */
		private void markGoods(ArrayList<SimpleGoodsBean> list) {
			if(list == null || list.size() == 0) return;
			for(SimpleGoodsBean bean : list) {
				try {
					if(ClientDBHelper.getInstance()
							.isGoodsExists(bean.getEpc())) {
						ClientDBHelper.getInstance().updateSimpleGoodsBean(bean);
					}else {
						ClientDBHelper.getInstance().insertSimpleGoods(bean);
					}
				} catch (SQLException e) {
//					e.printStackTrace();
					Logger.getLogger(TAG).log(Level.INFO, String.format("%s %d %s", e.getMessage(), e.getErrorCode(), e.getSQLState()));
				} 
			}
		}
		
		private ArrayList<SimpleGoodsBean> parseJSON(String json){
			JSONArray jsonArr = JSONArray.fromObject(json);
			int size = jsonArr.size();
			ArrayList<SimpleGoodsBean> list = new ArrayList<>();
			for(int i=0 ; i <size ; ++i) {
				JSONObject obj = jsonArr.getJSONObject(i);
				SimpleGoodsBean bean = SimpleGoodsBean.newInstanceFromLocalStoreJSON(obj);
				list.add(bean);
			}
			return list;
		}
		
	}
	
}
