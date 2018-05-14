package login;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import beans.AdminstratorInfo;
import goods.GoodsControl;
import helper.NetworkHelper;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.json.JSONObject;

/**
 * 登陆界面类
 * @author GT
 *
 */
public class LoginControl {
	private Stage stage;
	private TextField tf_account;
	private PasswordField pf_pwd;
	private Label lb_msg;
	private Button btn_login;
	
	/**
	 * 开始登陆界面逻辑
	 * @param stage 需要传入Primary Stage
	 * @throws IOException
	 */
	public void start(Stage primaryStage) throws IOException {
		this.stage = primaryStage;
		Image logo16 = new Image("file:resource/drawable/logo16.png");
		Image logo32 = new Image("file:resource/drawable/logo32.png");
		stage.getIcons().addAll(logo16, logo32);
		Pane pane = FXMLLoader.load(getClass().getResource("login.fxml"));
		Scene scene = new Scene(pane,400,400);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
		
		tf_account = (TextField) pane.lookup("#textfield_login_account");
		pf_pwd = (PasswordField) pane.lookup("#passwordfield_login_password");
		lb_msg = (Label) pane.lookup("#label_login_msg");
		btn_login = (Button) pane.lookup("#button_login_login");
		//注册监听器等
		loginControl();
	}
	
	private void loginControl() {
		
		tf_account.setOnKeyTyped(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				lb_msg.setText("");
			}
		});//当有账号密码输入时，清除提示信息
		pf_pwd.setOnKeyTyped(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				lb_msg.setText("");
			}
		});
		
		btn_login.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				String account = tf_account.getText();
				String pwd = pf_pwd.getText();
				if("".equals(account) || "".equals(pwd) ) {
					lb_msg.setText("请输入账户和密码!");
					return;
				}
				//向后台接口提交数据，检查账户密码正确性，获取登陆token
				checkAccount(account,pwd);
			}; 
		});
	}
	
	/**
	 * 检查账户是否正确
	 * @param account
	 * @param pwd
	 */
	private void checkAccount(String account,String pwd) {
		//显示一个等待对话框
		Stage dialog = getLoginInfoDialog();
		dialog.show();
		//提交给服务器
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String strUrl = "http://localhost:8888/api/admin/Public/login";
				HashMap<String,String> map = new HashMap<String, String>();
				map.put("username", account);
				map.put("password", pwd);
				map.put("device_type", "pc");
				String jsonStr = NetworkHelper.downloadString(strUrl, map, "POST");
				////解析JSON数据
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				int code = jsonObj.getInt("code");
				final String msg = jsonObj.getString("msg");
				if(code != 1) {
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							dialog.hide();
							lb_msg.setText(msg);
						}
					});
				}else {
					JSONObject data = jsonObj.getJSONObject("data");
					final String token = data.getString("token");
					final AdminstratorInfo adminInfo =  new AdminstratorInfo(); 
					JSONObject adminObj = data.getJSONObject("adminstrator");
					adminInfo.setBirthday(adminObj.getInt("birthday"));
					adminInfo.setCreate_time(adminObj.getInt("create_time"));
					adminInfo.setId(adminObj.getInt("id"));
					adminInfo.setMobile(adminObj.getString("mobile"));
					adminInfo.setName(adminObj.getString("name"));
					adminInfo.setPost_id(adminObj.getInt("post_id"));
					adminInfo.setPost_name(adminObj.getString("post_name"));
					adminInfo.setSex(adminObj.getInt("sex"));
					adminInfo.setUser_login(adminObj.getString("user_login"));
					adminInfo.setUser_status(adminObj.getInt("user_status"));
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							dialog.hide();
							Main.setToken(token);
							Main.setAdminInfo(adminInfo);
							Logger.getLogger(LoginControl.class.getSimpleName()).log(Level.INFO, adminInfo.toString());
							LoginControl.this.stage.hide();
							(new GoodsControl()).start();
						}
					});
				}
			}
		})).start();
	}
	
	
	private Stage getLoginInfoDialog() {
		Stage dialog = new Stage(StageStyle.TRANSPARENT);
		ProgressIndicator pi = new ProgressIndicator();
		Text text = new Text("登陆中");
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(10);
		vbox.setPadding(new Insets(10,10,10,10));
		pi.setPadding(new Insets(10, 10, 10, 10));
		text.setFill(Color.WHITE);
		vbox.getChildren().addAll(pi,text);
		vbox.setStyle("-fx-background-color:#000000;-fx-border-radiu:10px;-fx-background-radiu:10px;");
		Scene scene = new Scene(vbox);
		dialog.setScene(scene);
		dialog.setOpacity(0.5);
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(this.stage);
		return dialog;
	}
	
}
