package com.gthncz.safeguard.login;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;

import com.gthncz.beans.AdminstratorInfo;
import com.gthncz.beans.Params;
import com.gthncz.helper.DialogHelper;
import com.gthncz.helper.NetworkHelper;
import com.gthncz.safeguard.main.SafeGuardController;

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
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import net.sf.json.JSONObject;

/**
 * 登陆界面类
 * 
 * @author GT
 *
 */
public class LoginController implements Initializable {
	private Stage stage;

	@FXML private TextField textfield_login_account;
	@FXML private PasswordField passwordfield_login_password;
	@FXML private Label label_login_msg;
	@FXML private Button button_login_login;

	private Parent parent;

	public LoginController() {
		// !important 不使用这种方式创建实例
	}

	public static LoginController getInstance() {
		URL location = LoginController.class.getResource("login.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
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
	public void start(Stage primaryStage) {
		this.stage = primaryStage;
		Image logo32 = new Image("file:resource/drawable/logo32.png");
		stage.getIcons().add(logo32); // TODO  java.lang.NullPointerException: Root cannot be null ?
		Scene scene = new Scene(parent, 400, 400);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
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
	}

	private void login() {
		if (!checkAccount())
			return;
		// 显示一个等待对话框
		Stage dialog = DialogHelper.getLoadingDialog(null);
		dialog.show();
		String account = textfield_login_account.getText().trim();
		String pwd = passwordfield_login_password.getText();
		// 提交给服务器
		(new Thread(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("username", account);
				map.put("password", pwd);
				map.put("device_type", "pc");
				String jsonStr = NetworkHelper.downloadString(Params.URL_ADMIN_LOGIN, map, "POST");
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
					AdminstratorInfo adminInfo = AdminstratorInfo.newInstanceFromJSONObject(adminObj);
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							dialog.hide();
							Main.setToken(token);
							Main.setAdminInfo(adminInfo);
							Logger.getLogger(LoginController.class.getSimpleName()).log(Level.INFO, adminInfo.toString());
							jump2SafeGuardControl();
						}
					});
				}
			}
		})).start();
	}
	
	private void jump2SafeGuardControl() {
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1*1000); // 延时一秒跳转
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						SafeGuardController controller = SafeGuardController.getInstance();
						controller.start();
						LoginController.this.stage.close();
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
