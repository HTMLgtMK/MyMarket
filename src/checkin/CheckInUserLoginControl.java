package checkin;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.Params;
import beans.UserBean;
import beans.UserGrantReqBean;
import checkin.CheckInControl.OnShowPageListener;
import checkin.CheckInControl.Page;
import helper.NetworkHelper;
import helper.QRCodeHelper;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.json.JSONObject;

/**
 * 会员登陆
 * @author GT
 *
 */
public class CheckInUserLoginControl implements Initializable {

	@FXML
	private BorderPane checkin_userlogin_root;
	@FXML
	private Button btn_cancel_login;
	@FXML
	private ImageView img_login_qrcode;
	@FXML
	private Label label_hint;
	
	private UserGrantReqBean userGrantReqBean; // 会员授权状态信息
	private UserBean userBean; // 会员信息
	
	private Timer timer;// 轮询授权状态的计时器
	private boolean checkFlag; // 可轮询标识
	
	private OnShowPageListener showPageListener;
	
	public void setOnShowPageListener(OnShowPageListener listener) {
		this.showPageListener = listener;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getBounds();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		checkin_userlogin_root.setPrefSize(width, height);

		// 初始化按钮
		Image img_return = new Image("file:assets/drawable/return.png");
		btn_cancel_login.setGraphic(new ImageView(img_return));
		btn_cancel_login.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				closeGrantReq();
			};
		});
		
		checkFlag = false;
	}
	
	/**
	 * 开始业务逻辑
	 */
	public void start() {
		label_hint.setText(null);
		img_login_qrcode.setImage(null);
		
		Stage dialog = getLoadingDialog("请求授权中...");
		dialog.show();
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				String spec = Params.URL_USER_GRANT_REQ;
				String json = NetworkHelper.downloadString(spec, null, "POST");
				Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, json);
				JSONObject jsonObj = JSONObject.fromObject(json);
				if(jsonObj != null) {
					final int code = jsonObj.getInt("code");
					final String msg = jsonObj.getString("msg");
					if(code == 1) {
						JSONObject dataObj = jsonObj.getJSONObject("data");
						dataObj = dataObj.getJSONObject("token");
						userGrantReqBean = new UserGrantReqBean();
						userGrantReqBean.setId(dataObj.getInt("id"));
						userGrantReqBean.setUser_id(dataObj.getInt("user_id"));
						userGrantReqBean.setToken(dataObj.getString("token"));
						userGrantReqBean.setAction(dataObj.getString("action"));
						userGrantReqBean.setStatus(dataObj.getInt("status"));
						userGrantReqBean.setCreat_time(dataObj.getInt("create_time"));
						userGrantReqBean.setExpire_time(dataObj.getInt("expire_time"));
					}
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							dialog.close();
							if(code == 1) {
								showGrantQrCode();
							}else {
								showErrorMsgBox(msg);
							}
						}
					});
				}else {
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							dialog.close();
							showErrorMsgBox("请求失败！");
						}
					});
				}
			}
		})).start();
	}

	/**
	 * 显示授权二维码
	 */
	private void showGrantQrCode() {
		String url = "market://grant/" + userGrantReqBean.getToken();
		Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "qrcode url: "+url);
		Image image = QRCodeHelper.zxingQRCodeCreate( url, 200, 200);
		img_login_qrcode.setImage(image);
		label_hint.setText("请使用手机扫描二维码授权登陆！");
		startCheckGrantStatus();
	}
	
	/*开始轮询授权状态*/
	private void startCheckGrantStatus() {
		if(timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					if(checkFlag == false) {
						return;
					}
					checkGrantStatus();
				}
				
			}, 0, 2*1000);
		}
		checkFlag = true;
	}
	
	/*新建线程检查授权状态*/
	private void checkGrantStatus() {
		checkFlag = false;
		userBean = null;
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				String spec = Params.URL_GRANT_CHECK_STATUS;
				HashMap<String, String> map = new HashMap<>();
				map.put("id", String.valueOf(userGrantReqBean.getId()));
				String json = NetworkHelper.downloadString(spec, map, "POST");
				Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, json);
				JSONObject jsonObj = JSONObject.fromObject(json);
				if(jsonObj != null) {
					int code = jsonObj.getInt("code");
					final String msg = jsonObj.getString("msg");
					if(code == 1) {
						JSONObject dataObj = jsonObj.getJSONObject("data");
						int status = dataObj.getInt("status");
						//String status_detail = dataObj.getString("status_detail");
						switch(status) {
						case 1: // 未扫描
						case 4:{// 已扫描，等待确认授权
							checkFlag = true;// 可继续询查
							break;
						}
						case 2:{ // 已授权
							JSONObject userObj = dataObj.getJSONObject("user");
							userBean = new UserBean();
							userBean.setAvatar(userObj.getString("avatar"));
							userBean.setId(userObj.getLong("id"));
							userBean.setName(userObj.getString("name"));
							userBean.setUser_login(userObj.getString("user_login"));
							userBean.setUser_nickname(userObj.getString("user_nickname"));
							userBean.setPoint(userObj.getInt("point"));
							userBean.setBalance(userObj.getInt("balance"));
							userBean.setUser_level(userObj.getLong("user_level"));
							// 停止继续询查
							checkFlag = false;
							timer.cancel();
							timer = null;
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									showCartPage();
								}
							});
							break;
						}
						case 3:{ // 授权请求已关闭
							checkFlag = false;
							closeGrantReq(); // 直接返回到欢迎页
							break;
						}
						default:{
							checkFlag = true; // 未定义情形， continue?
						}
						}
						
					}else {// 请求失败
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								//checkFlag = false;
								// 取消轮询
								timer.cancel();
								timer = null;
								Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.OK);
								alert.setOnCloseRequest(new EventHandler<DialogEvent>() {

									@Override
									public void handle(DialogEvent event) {
										closeGrantReq();// 回退到欢迎页
									}
								});
								alert.show();
							}
						});
					}
				}else {
					checkFlag = true;
				}
			}
		})).start();
	}
	
	private void showCartPage() {
		if(showPageListener != null) {
			showPageListener.showPage(Page.PAGE_CART);
		}
	}

	/**
	 * 关闭授权请求
	 */
	private void closeGrantReq() {
		if(userGrantReqBean == null) {
			if(showPageListener != null) {
				showPageListener.showPage(Page.PAGE_WELCOME);
			}
			return;
		}
		if(timer != null) { // 关闭计时器
			timer.cancel();
			timer = null;
		}
		Stage dialog = getLoadingDialog("关闭授权请求");
		dialog.show();
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				String spec = Params.URL_USER_CLOSE_GRANT;
				HashMap<String, String> map = new HashMap<>();
				map.put("id", String.valueOf(userGrantReqBean.getId()));
				map.put("token", userGrantReqBean.getToken());
				String json = NetworkHelper.downloadString(spec, map, "POST");
				JSONObject jsonObj = JSONObject.fromObject(json);
				if(jsonObj != null) {
					//final int code = jsonObj.getInt("code");
					String msg = jsonObj.getString("msg");
					Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, msg);
				}
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						dialog.close();
						if(showPageListener != null) {
							showPageListener.showPage(Page.PAGE_WELCOME);
						}
					}
				});
			}
		})).start();
	}
	
	
	/**
	 * 显示错误信息的对话框
	 */
	private void showErrorMsgBox(String contentText) {
		Alert alert = new Alert(AlertType.ERROR,contentText, ButtonType.CLOSE);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.setOnCloseRequest(new EventHandler<DialogEvent>() {

			@Override
			public void handle(DialogEvent event) {
				closeGrantReq();
			}
		});
		alert.show();
	}
	
	/**
	 * 等待对话框
	 * 
	 * @return
	 */
	private Stage getLoadingDialog(String msg) {
		Stage dialog = new Stage(StageStyle.TRANSPARENT);
		ProgressIndicator pi = new ProgressIndicator();
		Text text = new Text(msg);
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(10);
		vbox.setPadding(new Insets(10, 10, 10, 10));
		pi.setPadding(new Insets(10, 10, 10, 10));
		text.setFill(Color.WHITE);
		vbox.getChildren().addAll(pi, text);
		vbox.setStyle("-fx-background-color:#000000;-fx-border-radiu:10px;-fx-background-radiu:10px;");
		Scene scene = new Scene(vbox);
		dialog.setScene(scene);
		dialog.setOpacity(0.5);
		dialog.initModality(Modality.APPLICATION_MODAL);
		return dialog;
	}
	
}
