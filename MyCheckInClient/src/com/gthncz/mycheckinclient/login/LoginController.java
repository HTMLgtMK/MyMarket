package com.gthncz.mycheckinclient.login;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gthncz.mycheckinclient.beans.AdminstratorInfo;
import com.gthncz.mycheckinclient.beans.Params;
import com.gthncz.mycheckinclient.checkin.CheckInControl;
import com.gthncz.mycheckinclient.helper.DialogHelper;
import com.gthncz.mycheckinclient.helper.NetworkHelper;
import com.gthncz.mycheckinclient.helper.UHFHelper;

import application.Main;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.json.JSONObject;

public class LoginController implements Initializable {
	private static final String TAG = LoginController.class.getSimpleName();

	@FXML
	protected TextField textfield_login_account;
	@FXML
	protected PasswordField passwordfield_login_password;
	@FXML
	protected Label label_login_msg;
	@FXML
	protected Button button_login_login;
	@FXML
	protected Button btn_setting;
	@FXML protected Button btn_exit;
	@FXML protected VBox vbox_form_wrapper;
	@FXML protected VBox vbox_connection_wrapper;
	@FXML protected Label label_connection_msg;
	@FXML protected Button button_reconnect;
	
	protected Parent parent;
	protected Stage stage;

	public LoginController() {
		// !important Do Not use this construct to create instance
	}

	public static LoginController getInstance() {
		URL location = LoginController.class.getResource("login.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			// continue ?
		}
		LoginController instance = loader.getController();
		instance.parent = parent;
		return instance;
	}

	/**
	 * 开始登陆界面逻辑
	 * 
	 * @param stage
	 *            需要传入Primary Stage
	 * @throws IOException
	 */
	public void start(Stage primaryStage) throws IOException {
		stage = primaryStage;
		
		Scene scene = new Scene(parent, 400, 400);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.UNDECORATED); // 无标题栏
		primaryStage.show();
		
		vbox_connection_wrapper.setVisible(false);
		// 将焦点移到账号输入框
		textfield_login_account.requestFocus();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 注册监听器等
		loginControl();
	}

	private void loginControl() {
		EventHandler<Event> typedHandler = new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				label_login_msg.setText("");
			}
		};
		textfield_login_account.setOnKeyTyped(typedHandler);// 当有账号密码输入时，清除提示信息
		passwordfield_login_password.setOnKeyTyped(typedHandler);
		EventHandler<KeyEvent> eventHandler = new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER)) {
					login();// 触发登陆
				}
			}
		};
		passwordfield_login_password.setOnKeyPressed(eventHandler);
		button_login_login.setOnKeyPressed(eventHandler);
		button_login_login.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				// 向后台接口提交数据，检查账户密码正确性，获取登陆token
				login();
			};
		});
		btn_exit.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				LoginController.this.stage.close();
			}
		});
		button_reconnect.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				connectUHFReader();
			}
		});
	}

	private void login() {
		if (!checkAccount())
			return;
		String account = textfield_login_account.getText().trim();
		String pwd = passwordfield_login_password.getText();
		// 显示一个等待对话框
		Stage dialog = DialogHelper.getDialog("登陆中...");
		dialog.show();
		// 提交给服务器
		(new Thread(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("username", account);
				map.put("password", pwd);
				map.put("device_type", "pc");
				String jsonStr = NetworkHelper.downloadString(Params.URL_LOGIN, map, "POST");
				//// 解析JSON数据
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				int code = jsonObj.getInt("code");
				final String msg = jsonObj.getString("msg");
				if (code != 1) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							dialog.hide();
							label_login_msg.setText(msg);
						}
					});
				} else {
					JSONObject data = jsonObj.getJSONObject("data");
					final String token = data.getString("token");
					JSONObject adminObj = data.getJSONObject("adminstrator");
					final AdminstratorInfo adminInfo = AdminstratorInfo.newInstanceFromJSONObject(adminObj);
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							dialog.hide();
							Main.setToken(token);
							Main.setAdminInfo(adminInfo);
							Logger.getLogger(TAG).log(Level.INFO, adminInfo.toString());
							vbox_connection_wrapper.setVisible(true);
							vbox_form_wrapper.setVisible(false);
							connectUHFReader();
						}
					});
				}
			}
		})).start();
	}
	
	/** 连接UHF读写器 */
	private void connectUHFReader() {
		label_connection_msg.setText("连接读写器中...");
		button_reconnect.setVisible(false);
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				final int ret = UHFHelper.init();
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						if(ret == 0x00) {
							Logger.getLogger(TAG).log(Level.INFO, "连接UHFReader成功!");
							label_connection_msg.setText("连接UHFReader成功!");
							jump2checkin();
						}else {
							Logger.getLogger(TAG).log(Level.INFO, String.format("连接失败: %s(0x%02X)", UHFHelper.CODE_MSG_MAP.get(ret), ret));
							label_connection_msg.setText(String.format("连接失败: %s(0x%02X)", UHFHelper.CODE_MSG_MAP.get(ret), ret));
							button_reconnect.setVisible(true);
							// TODO DELETE THIS
							jump2checkin();
						}
					}
				});
			}
		})).start();
	}
	
	/* 跳转到收银界面 */
	private void jump2checkin() {
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(2*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						LoginController.this.stage.close();
						CheckInControl checkInControl = CheckInControl.getInstance();
						checkInControl.start();
					}
				});
			}
		})).start();
	}

	/**
	 * 检查账户是否正确
	 * 
	 * @param account
	 * @param pwd
	 */
	private boolean checkAccount() {
		String account = textfield_login_account.getText().trim();
		String pwd = passwordfield_login_password.getText();
		if ("".equals(account) || "".equals(pwd)) {
			label_login_msg.setText("请输入账户和密码!");
			return false;
		}
		return true;
	}

}
