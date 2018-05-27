package com.gthncz.safeguard.main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.gthncz.helper.ClientDBHelper;
import com.gthncz.helper.DialogHelper;
import com.gthncz.helper.UHFHelper;
import com.gthncz.service.GoodsMarkerService;

import application.Main;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SafeGuardControlController implements Initializable {
	
	@FXML protected Label label_uhf_status;
	@FXML protected Button btn_uhf_connect;
	
	@FXML protected Label label_server_status;
	@FXML protected Button btn_server_connect;
	
	@FXML protected ScrollPane scrollpane_info;
	protected VBox vbox_info;
	
	private Parent parent;
	
	public static SafeGuardControlController getInstance() {
		URL location = SafeGuardControlController.class.getResource("goods_add.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SafeGuardControlController instance = loader.getController();
		instance.parent = parent;
		return instance;
	}

	public Parent getRoot() {
		return parent;
	}
	
	public void start() {
		initUHFReader();
		initServerConnection();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initViews();
	}
	
	private void initViews() {
		// 为scrollPane 添加content
		vbox_info = new VBox();
		vbox_info.setSpacing(10);
		vbox_info.setPadding(new Insets(10));
		scrollpane_info.setContent(vbox_info);
		
		/* 设置面板滚动 */
		vbox_info.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				scrollpane_info.setVvalue(1);
			}
		});
	}
	
	private class MyServerConnectionButtonClicked implements EventHandler<Event>{

		@Override
		public void handle(Event event) {
			if(ClientDBHelper.getInstance().isConnected()) {
				disconnectServer();
			}else {
				connectServer();
			}
		}
	}
	
	private MyServerConnectionButtonClicked mServerConnectionButtonClicked = new MyServerConnectionButtonClicked();
	private GoodsMarkerService goodsMarkerService;
	
	private void initServerConnection() {
		if(goodsMarkerService == null) {
			goodsMarkerService = new GoodsMarkerService();
		}
		if(goodsMarkerService.isRunning()) {
			btn_server_connect.setText("断开");
			label_server_status.setText("已开启");
		}else {
			btn_server_connect.setText("开启");
			label_server_status.setText("已关闭");
		}
		btn_server_connect.setOnMouseClicked(mServerConnectionButtonClicked);
	}
	
	private void connectServer() {
		showMessage(LEVEL_MSG_INFO, "正在开启服务...");
		final Stage dialog = DialogHelper.getLoadingDialog(null);
		dialog.show();
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean res = goodsMarkerService.startService();
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						if(res) {
							showMessage(LEVEL_MSG_INFO, "服务已开启!");
						}else {
							showMessage(LEVEL_MSG_INFO, "服务开启失败!");
						}
						dialog.close();
						initServerConnection();
					}
				});
			}
		})).start();
	}
	
	private void disconnectServer() {
		showMessage(LEVEL_MSG_INFO, "正在关闭服务...");
		Stage dialog = DialogHelper.getLoadingDialog(null);
		dialog.show();
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				goodsMarkerService.stopService();
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						dialog.close();
						showMessage(LEVEL_MSG_INFO, "服务已关闭!");
						initServerConnection();
					}
				});
			}
		})).start();
	}
	
	/* UHFReader 连接按钮点击事件 */
	private class MyUHFConnectionButtonClicked implements EventHandler<Event> {
		@Override
		public void handle(Event event) {
			if (Main.isUhfConnected()) { // intent to 断开连接
				disconnectUHFReader();
			} else { // intent to 连接读写器
				connectUHFReader();
			}
		}
	}

	private MyUHFConnectionButtonClicked myUHFConnectionButtonClicked = new MyUHFConnectionButtonClicked();

	private void initUHFReader() {
		if (Main.isUhfConnected()) {
			label_uhf_status.setText("已连接");
			btn_uhf_connect.setText("断开");
		} else {
			label_uhf_status.setText("未连接");
			btn_uhf_connect.setText("连接");
		}
		btn_uhf_connect.setOnMouseClicked(myUHFConnectionButtonClicked);
	}

	/** 连接UHF读写器 */
	protected void connectUHFReader() {
		showMessage(LEVEL_MSG_INFO, "正在连接读写器...");
		Stage dialog = DialogHelper.getLoadingDialog(null);
		dialog.show();
		(new Thread(new Runnable() {

			@Override
			public void run() {
				final int ret = UHFHelper.init();
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						dialog.close();
						if (ret == 0x00) {
							showMessage(LEVEL_MSG_INFO, "连接读写器成功!");
							Main.setUhfConnected(true);
						} else {
							showMessage(LEVEL_MSG_ERROR,
									String.format("连接失败: %s(0x%02X)", com.gthncz.helper.UHFHelper.CODE_MSG_MAP.get(ret), ret));
						}
						// 重新设置状态
						initUHFReader();
					}
				});
			}
		})).start();
	}

	/** 断开连接UHF读写器 */
	protected void disconnectUHFReader() {
		showMessage(LEVEL_MSG_INFO, "正在断开连接读写器...");
		Stage dialog = DialogHelper.getLoadingDialog(null);
		dialog.show();
		(new Thread(new Runnable() {

			@Override
			public void run() {
				final int ret = UHFHelper.closeComPort();
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						dialog.close();
						if (ret == 0x00) {
							showMessage(LEVEL_MSG_INFO, "断开连接读写器成功!");
							Main.setUhfConnected(false);
						} else {
							showMessage(LEVEL_MSG_ERROR,
									String.format("断开连接失败: %s(0x%02X)", UHFHelper.CODE_MSG_MAP.get(ret), ret));
						}
						// 重新设置状态
						initUHFReader();
					}
				});
			}
		})).start();
	}
	
	
	private final int LEVEL_MSG_INFO = 0x01;
	private final int LEVEL_MSG_WARNING = 0x02;
	private final int LEVEL_MSG_ERROR = 0x03;

	private Paint mPaint = null;

	private void showMessage(int level, String msg) {
		this.showMessage(level, msg, null);
	}
	/** 在消息面板上显示信息 */
	private void showMessage(int level, String msg, Text text) {
		if(text == null) {
			text = new Text(msg);
			vbox_info.getChildren().add(text);
		}
		text.setText(msg);
		switch (level) {
		case LEVEL_MSG_INFO: {
			mPaint = Paint.valueOf("#000000");
			break;
		}
		case LEVEL_MSG_WARNING: {
			mPaint = Paint.valueOf("#FF7F24");
			break;
		}
		case LEVEL_MSG_ERROR: {
			mPaint = Paint.valueOf("#FF0000");
			break;
		}
		default: {
			mPaint = Paint.valueOf("#000000");
			break;
		}
		}
		text.setFill(mPaint);
	}
}
