package com.gthncz.safeguard.main;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gthncz.beans.InventoryBean;
import com.gthncz.beans.Params;
import com.gthncz.beans.SimpleGoodsBean;
import com.gthncz.helper.ClientDBHelper;
import com.gthncz.helper.NetworkHelper;
import com.gthncz.helper.UHFHelper;

import application.Main;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;
import javafx.util.Duration;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SafeGuardTabPageController implements Initializable {
	private static final String TAG = SafeGuardTabPageController.class.getSimpleName();
	
	@FXML protected Label label_msg;
	@FXML protected TableView<SimpleGoodsBean> tableView_safeguard_unchecked;
	protected TableColumn<SimpleGoodsBean, String> unCheckedNameCloumn;
	protected TableColumn<SimpleGoodsBean, String> unCheckedTimeColumn;
	protected TableColumn<SimpleGoodsBean, String> unCheckedStatusColumn;
	
	@FXML protected Label label_all_msg;
	@FXML protected TableView<SimpleGoodsBean> tableView_saefeguard_all;
	protected TableColumn<SimpleGoodsBean, String> allTimeColumn;
	
	/** 当前扫描到的EPC集合*/
	protected ObservableList<SimpleGoodsBean> allGoods;
	/** 当前未付款的商品集合 */
	protected ObservableList<SimpleGoodsBean> unCheckedGoods;
	
	private SimpleDateFormat simpleDateFormat;
	private Date date;
	
	private MediaPlayer mediaPlayer;
	private boolean isPlaying;
	
	private Parent parent;
	public static SafeGuardTabPageController getInstance() {
		URL location = SafeGuardTabPageController.class.getResource("safeguard_safeguard.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SafeGuardTabPageController instance = loader.getController();
		instance.parent = parent;
		return instance;
	} 
	
	public Parent getRoot() {
		return parent;
	}
	
	public SafeGuardTabPageController() {
		simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		date = new Date();
		allGoods = FXCollections.observableArrayList();
		unCheckedGoods = FXCollections.observableArrayList();
	}
	
	private InventionTask inventionTask;
	private CheckEPCTask checkEPCTask;
	private Semaphore checkSemaphore;
	
	public void start() {
		checkSemaphore = new Semaphore(0);
		checkEPCTask = new CheckEPCTask();
		checkEPCTask.start();
		inventionTask = new InventionTask();
		inventionTask.start();
		
		File welcomeFile = new File("./resource/audio/alarm.mp3");
		URI uri = welcomeFile.toURI();
		Media media = new Media(uri.toString());
		mediaPlayer = new MediaPlayer(media);
		isPlaying = false;
		mediaPlayer.setOnEndOfMedia(new Runnable() {
			
			@Override
			public void run() {
				isPlaying = false;
			}
		});
	}
	
	public void forceStopTimer() {
		if(inventionTask!=null) {
			inventionTask.stopInvention();
			inventionTask = null;
		}
		if(checkEPCTask != null) {
			checkEPCTask.stopCheck();
			checkSemaphore.release();
		}
		if(mediaPlayer != null) {
			try{
				mediaPlayer.stop();
				mediaPlayer.dispose();
			}catch(Exception e) {
				// nothing to do 
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<TableColumn<SimpleGoodsBean, ?>> columnList = tableView_safeguard_unchecked.getColumns();
		unCheckedTimeColumn = (TableColumn<SimpleGoodsBean, String>) columnList.get(2);
		unCheckedStatusColumn = (TableColumn<SimpleGoodsBean, String>) columnList.get(3);
		
		ObservableList<TableColumn<SimpleGoodsBean, ?>> columnList2 = tableView_saefeguard_all.getColumns();
		allTimeColumn = (TableColumn<SimpleGoodsBean, String>) columnList2.get(1);

		unCheckedTimeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SimpleGoodsBean,String>, ObservableValue<String>>() {
			
			@Override
			public ObservableValue<String> call(CellDataFeatures<SimpleGoodsBean, String> param) {
				date.setTime(param.getValue().getTime() * 1000);
				String time = simpleDateFormat.format(date);
				return new SimpleStringProperty(time);
			}
		});
		unCheckedStatusColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SimpleGoodsBean,String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<SimpleGoodsBean, String> param) {
				String status = null;
				switch(param.getValue().getStatus()) {
				case 1:{
					status=  "待售";
					break;
				}
				case 2:{
					status = "已售";
					break;
				}
				case 3:{
					status = "被锁定";
					break;
				}
				default:{
					status = "未知";
				}
				}
				return new SimpleStringProperty(status);
			}
		});
		allTimeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SimpleGoodsBean,String>, ObservableValue<String>>() {
			
			@Override
			public ObservableValue<String> call(CellDataFeatures<SimpleGoodsBean, String> param) {
				date.setTime(param.getValue().getTime()*1000); // ?? 这里为什么要*1000
				String time = simpleDateFormat.format(date);
				return new SimpleStringProperty(time);
			}
		});
		tableView_saefeguard_all.setItems(allGoods);
		tableView_safeguard_unchecked.setItems(unCheckedGoods);
		
		tableView_safeguard_unchecked.getItems().addListener(new ListChangeListener<SimpleGoodsBean>() {

			@Override
			public void onChanged(Change<? extends SimpleGoodsBean> c) {
				int count = tableView_safeguard_unchecked.getItems().size();
				tableView_safeguard_unchecked.scrollTo(count); // 滚动到最底部
				if(mediaPlayer != null && !isPlaying) {
					mediaPlayer.seek(Duration.ZERO); // 重新定位, 否则不能播放
					mediaPlayer.play();
					isPlaying = true;
				}
			}
		});
	}
	
	/**
	 * 检查商品是否已售的任务
	 * 
	 * @author GT
	 *
	 */
	private class CheckEPCTask extends Thread{
		/**任务队列*/
		private Queue<SimpleGoodsBean> queue;
		/**可检查标识*/
		private boolean checkFlag;
		/**队列使用信号量*/
		private Semaphore queueSemaphore;
		/**提交检查的EPC列表(防止重复)*/
		private HashMap<String, String> epcList;
		
		/**添加任务*/
		public synchronized void addNewGoods(SimpleGoodsBean bean) {
			synchronized (queue) { // 同步队列
				try {
					queueSemaphore.acquire(); // 阻塞线程
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				queue.add(bean);
				queueSemaphore.release(); // 用完释放
			}
		}
		
		public void stopCheck() {
			this.checkFlag = false; // 等待任务全部结束后终止检查
		}
		
		public CheckEPCTask() {
			queue = new LinkedList<>();
			checkFlag = true;
			epcList = new HashMap<>();
			queueSemaphore = new Semaphore(1);
		}
		
		@Override
		public void run() {
			while(checkFlag) {
				try {
					checkSemaphore.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(queue.isEmpty()) continue;
				epcList.clear();
				int index = 0;
				Set<String> epcSet = new HashSet<>(); // 用于过滤同一个标签
				for(SimpleGoodsBean bean : queue) {
					if(!epcSet.contains(bean.getEpc())) {
						epcSet.add(bean.getEpc());
						if(isGoodsMarked(bean.getEpc())) continue; // 已经标记, 跳过
						
						bean.setTime(bean.getTime()); 
//						String beanJSON = StringEscapeUtils.escapeHtml(bean.toJSON());
						epcList.put(String.valueOf(index++), bean.toJSON() );
					}
				}
				queue.clear();
				if(epcList.isEmpty()) continue;
				// 向服务器发起请求
				String json = NetworkHelper.downloadString(Params.URL_GET_SIMPLE_GOODS_INFO, epcList, "POST");
				Logger.getLogger(TAG).log(Level.INFO, "check json : " + json);
				ArrayList<SimpleGoodsBean> list = parseJSON(json);
				// 过滤商品， 添加到表格
				for(SimpleGoodsBean bean : list) {
					if(bean.getStatus() != 2) {
						unCheckedGoods.add(bean);
					}
				}
				updateUncheckedTable();
			}
		}
		
		
		private boolean isGoodsMarked(String epc) {
			boolean flag = false;
			try {
				flag = ClientDBHelper.getInstance().isGoodsExists(epc);
				Logger.getLogger(TAG).log(Level.INFO, "** 信息  >> client Db info epc:" + epc + " is exists :" + flag);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return flag ;
		}
		
		/**更新表格*/
		private void updateUncheckedTable() {
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					tableView_safeguard_unchecked.setItems(unCheckedGoods);
				}
			});
		}

		private ArrayList<SimpleGoodsBean> parseJSON(String json) {
			JSONObject jsonObj = JSONObject.fromObject(json);
			int code = jsonObj.getInt("code");
			ArrayList<SimpleGoodsBean> list = null;
			if(code == 1) {
				JSONObject data = jsonObj.getJSONObject("data");
				if(data.containsKey("goods")) {
					list = new ArrayList<>();
					JSONArray goodsArr = data.getJSONArray("goods");
					int length = goodsArr.size();
					for(int i=0;i<length;++i) {
						JSONObject obj = goodsArr.getJSONObject(i);
						SimpleGoodsBean bean = SimpleGoodsBean.newInstanceFromJSONObject(obj);
						list.add(bean);
					}
				}
			}
			return list; 
		}
	}
	
	
	/**
	 * 询查标签任务
	 * 
	 * @author GT
	 *
	 */
	private class InventionTask extends Thread {
		// 可轮询参数
		private boolean inventionFlag;
		// 轮询参数
		private InventoryBean inventoryBean;
		private final int INVENTION_DURATION = 100;
		// 上一次更新表格时间
		private long lastValidateTable;
		private final int VALIDATE_DURATION = 5000;
		// 某个时间段内的标签集合
		private Set<String> epcSet;
		
		public InventionTask() {
			inventionFlag = true;
			inventoryBean = new InventoryBean();
			lastValidateTable = 0;
			epcSet = new HashSet<>();
		}

		/* 外部事件停止询查 */
		public void stopInvention() {
			inventionFlag = false;
		}

		// 更新UI
		private void updateMSG(int ret) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					date.setTime(System.currentTimeMillis());
					String time =   simpleDateFormat.format(date);
					label_all_msg.setText("时间:" + time
							+ String.format(" 扫描结果: %s (0x%02x) ", UHFHelper.CODE_MSG_MAP.get(ret), ret));
					label_msg.setText(String.format("当前时间: %s", time));
				}
			});
		}

		@Override
		public void run() {
			while (inventionFlag) { // 为了可以手动退出轮询
				if(!Main.isUhfConnected()) {
					inventionFlag = false;
					continue; // 跳出循环
				}
				if(System.currentTimeMillis() -  lastValidateTable > VALIDATE_DURATION ) {
					allGoods.clear(); // 时间过长清空表格显示
					epcSet.clear();
					lastValidateTable = System.currentTimeMillis();
				}
				inventoryBean.setTotallen(0);// 置0
				inventoryBean.setCardNum(0);// 标签数置0
				int ret = UHFHelper.inventory_G2(inventoryBean);
//				Logger.getLogger(TAG).log(Level.INFO, "** 信息 >> invention return : "
//						+ String.format("%s (0x%02x) ", UHFHelper.CODE_MSG_MAP.get(ret), ret));
				/**
				 * 0x01 询查时间结束前返回 0x02 询查时间结束使得询查退出 0x03
				 * 如果读到的标签数量无法在一条消息内传送完，将分多次发送。如果Status为0x0D，则表示这条数据结束后，还有数据。 0x04
				 * 还有电子标签未读取，电子标签数量太多，MCU存储不了 0xFB 无电子标签可操作
				 */
				if ((ret == 0x01) || (ret == 0x02) || (ret == 0x03) || (ret == 0x04)) {// || (ret == 0xFB)
					int m = 1;
					for (int CardIndex = 0; CardIndex < inventoryBean.getCardNum(); CardIndex++) {
						int EPClen = inventoryBean.getEPClenandEPC()[m - 1];
						StringBuilder builder = new StringBuilder();
						for (int i = 0; i < EPClen; ++i) {
							builder.append(
									String.format("%02X", Integer.valueOf(inventoryBean.getEPClenandEPC()[m + i])));
						}
						m = m + EPClen + 1;
						String epc = builder.toString();
						Logger.getLogger(TAG).log(Level.INFO, String.format("%d epc :%s", CardIndex, epc));
						if(!epcSet.contains(epc)) { // 目前还不存在, 更新集合
							SimpleGoodsBean goodsBean = new SimpleGoodsBean();
							goodsBean.setEpc(epc);
							goodsBean.setTime(System.currentTimeMillis() / 1000); // 提交服务器的时间除1000
							allGoods.add(goodsBean);
							epcSet.add(epc);
							if(checkEPCTask != null) {
								checkEPCTask.addNewGoods(goodsBean);
							}
						}
						checkSemaphore.release(); // 允许询查
					}
					// 更新UI
					updateMSG(ret);
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							tableView_saefeguard_all.setItems(allGoods);
						}
					});
				} else if (ret == 0xFB) {
					// NONE
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							updateMSG(ret);
						}
					});
				} else {
					// 出错
					Logger.getLogger(TAG).log(Level.INFO,
							String.format("出错: %s (0x%02x)", UHFHelper.CODE_MSG_MAP.get(ret), ret));
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							label_all_msg.setText(String.format("出错: %s (0x%02x)", UHFHelper.CODE_MSG_MAP.get(ret), ret));
						}
					});
					inventionFlag = false; // 退出轮询
					checkEPCTask.stopCheck();
					checkSemaphore.release();
				}
				try {
					Thread.sleep(INVENTION_DURATION); // 不要询查过快
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
