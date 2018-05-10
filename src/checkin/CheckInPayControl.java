package checkin;

import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.AlipayTradeQueryResponseBean;
import beans.DiscountBean;
import beans.GoodsBean;
import beans.Params;
import beans.WxpayOrderQueryResponseBean;
import checkin.CheckInControl.OnGetDealListener;
import checkin.CheckInControl.OnGetTradeQueryResponseListener;
import checkin.CheckInControl.OnShowPageListener;
import checkin.CheckInControl.Page;
import helper.NetworkHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 含有标签页的支付界面
 * 
 * @author GT
 *
 */
public class CheckInPayControl implements Initializable,OnGetTradeQueryResponseListener {

	private ArrayList<GoodsBean> goodsList;// 提交的商品列表
	private int totalPrice;// 总额
	private int discountPrice;// 折扣金额
	private int payPrice;// 支付金额
	private ArrayList<DiscountBean> discountList;//提交的优惠使用情况
	
	private String outTradeNo;//生成的商家订单号

	@FXML
	private BorderPane checkin_pay2_root;
	@FXML
	private Button btn_cancel_pay;
	@FXML
	private TabPane tabPane_checkin;
	@FXML
	private Tab tabAlipay;
	@FXML
	private TabAlipayPageControl tabAlipayPageController;
	@FXML
	private Tab tabWxpay;
	 @FXML 
	private TabWxpayPageControl tabWxpayPageController;

	private OnShowPageListener showPageListener;

	private OnGetDealListener getDealListener;

	public void setOnGetDealListener(OnGetDealListener getDealListener) {
		this.getDealListener = getDealListener;
	}

	public void setOnShowPageListener(OnShowPageListener listener) {
		this.showPageListener = listener;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getBounds();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		checkin_pay2_root.setPrefSize(width, height);

		// 初始化按钮
		Image img_return = new Image("file:assets/drawable/return.png");
		btn_cancel_pay.setGraphic(new ImageView(img_return));
		btn_cancel_pay.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				// TODO 取消此次交易
				revokeDeal();
			};
		});
		
		//初始化 tab切换
		tabPane_checkin.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
				Logger.getLogger(CheckInPayControl.class.getSimpleName()).log(Level.INFO, "tab changed: " + newValue.getText());
				
				if(newValue.equals(tabAlipay)) {
					tabWxpayPageController.forceStopTimer();
					tabAlipayPageController.start(outTradeNo);
				}else if(newValue.equals(tabWxpay)) {
					tabAlipayPageController.forceStopTimer();
					tabWxpayPageController.start(outTradeNo);
				}
			}
			
		});
	}

	/**
	 * 开始业务逻辑
	 */
	public void start() {
		//设置界面跳转回调, 必须在ParentController的Initialize执行完成后执行
		tabAlipayPageController.setOnShowPageListener(showPageListener);
		tabWxpayPageController.setOnShowPageListener(showPageListener);
		// 获取商品信息
		if (getDealListener != null) {
			goodsList = getDealListener.getGoodsList();
			totalPrice = getDealListener.getTotalPrice();
			discountPrice = getDealListener.getDiscountPrice();
			payPrice = getDealListener.getPayPrice();
			discountList = getDealListener.getDiscountList();
		}
		// 开始清空结果信息， 设置二维码展示面板不可见
		tabAlipay.getTabPane().setVisible(false);
		tabWxpay.getTabPane().setVisible(false);
		outTradeNo = "";
		/**
		 * 业务流程： 1. 提交订单，获取商家订单号 2. 由支付宝或者微信支付发起预支付
		 */
		Stage stage = getLoadingDialog("提交数据中...");
		stage.show();
		(new Thread(new Runnable() {

			@Override
			public void run() {
				String spec = Params.URL_SUBMIT_DEAL;
				HashMap<String, String> map = new HashMap<>();
				map.put("user_id", "1");// TODO 用户ID先固定为非VIP用户
				map.put("store_id", "1");// TODO 店铺ID先固定为1号店
				map.put("terminal_id", "1");// TODO 终端ID先固定为1号终端
				map.put("pay_amount", String.valueOf(payPrice));
				map.put("discount_amount", String.valueOf(discountPrice));
				map.put("total_amount", String.valueOf(totalPrice));
				// 组装商品详情
				JSONArray objArr = JSONArray.fromObject(goodsList);
				String goods_detail = objArr.toString();
				Logger.getLogger(CheckInPayControl.class.getSimpleName()).log(Level.INFO,
						"goods_detail: " + goods_detail);
				try {
					goods_detail = Base64.getEncoder().encodeToString(goods_detail.getBytes());// !important
					// goods_detail = URLEncoder.encode(goods_detail, "utf-8");
					map.put("goods_detail", goods_detail);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 组装优惠信息
				JSONArray discountArr = JSONArray.fromObject(discountList);
				String discount_detail = discountArr.toString();
				Logger.getLogger(CheckInPayControl.class.getSimpleName()).log(Level.INFO,
						"discount_str: " + discount_detail);
				try {
					discount_detail = Base64.getEncoder().encodeToString(discount_detail.getBytes());// !important
					map.put("discount_detail", discount_detail);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				String json = NetworkHelper.downloadString(spec, map, "POST", true);// TODO delete debug

				Logger.getLogger(CheckInPayControl.class.getSimpleName()).log(Level.INFO, json);

				JSONObject jsonObj = JSONObject.fromObject(json);
				int code = jsonObj.getInt("code");
				final String msg = jsonObj.getString("msg");
				if(code == 1) {
					JSONObject data = jsonObj.getJSONObject("data");
					CheckInPayControl.this.outTradeNo = data.getString("out_trade_no");
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							stage.hide();
							tabAlipay.getTabPane().setVisible(true);
							tabWxpay.getTabPane().setVisible(true);
							//开始标签页的逻辑
							startTabPageLogic();
						}
					});
				}else {
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							stage.hide();
							showErrorMsgBox(msg);
						}
					});
				}
			}
		})).start();
	}
	
	/**
	 * 撤销本次交易
	 */
	private void revokeDeal() {
		if(outTradeNo == null || outTradeNo == "") {
			tabAlipayPageController.forceStopTimer();
			tabWxpayPageController.forceStopTimer();
			if (showPageListener != null) {
				showPageListener.showPage(Page.PAGE_CART);
			}
		}else {
			Stage dialog = getLoadingDialog("撤销交易中...");
			dialog.show();
			(new Thread(new Runnable() {
				
				@Override
				public void run() {
					String spec = Params.URL_REVOKE_DEAL;
					HashMap<String, String> map = new HashMap<>();
					map.put("out_trade_no", outTradeNo);
					String json = NetworkHelper.downloadString(spec, map, "POST");
					JSONObject jsonObj = JSONObject.fromObject(json);
					final int code = jsonObj.getInt("code");
					final String msg = jsonObj.getString("msg");
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							dialog.hide();
							if(code == 1) {//成功
								tabAlipayPageController.forceStopTimer();
								tabWxpayPageController.forceStopTimer();
								if (showPageListener != null) {
									showPageListener.showPage(Page.PAGE_CART);
								}
							}else {
								showErrorMsgBox(msg);
							}
						}
					});
				}
			})).start();
		}
	}
	
	/**
	 * 开始标签页的逻辑
	 */
	private void startTabPageLogic() {
		showTabPage(0);//显示支付宝界面逻辑
		tabAlipayPageController.start(outTradeNo);
	}

	/**
	 * 显示错误信息的对话框
	 */
	private void showErrorMsgBox(String contentText) {
		Alert alert = new Alert(AlertType.ERROR,contentText, ButtonType.CLOSE);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initStyle(StageStyle.UNDECORATED);
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
	
	protected void showTabPage(int index) {
		tabPane_checkin.getSelectionModel().select(index);
	}

	/**
	 * 显示页面标签的代理类
	 * @author GT
	 */
	private class MyShowTabPageListener implements OnShowTabPageListener{

		@Override
		public void showTabPage(int index) {
			CheckInPayControl.this.showTabPage(index);
		}
		
	}
	
	/**
	 * 显示选项标签页的监听器
	 * @author GT
	 *
	 */
	public interface OnShowTabPageListener{
		public void showTabPage(int index);
	}

	@Override
	public AlipayTradeQueryResponseBean geAlipayTradeQueryResponseBean() {
		return tabAlipayPageController.getAlipayTradeQueryResponseBean();
	}

	@Override
	public WxpayOrderQueryResponseBean getWxpayOrderQueryResponseBean() {
		return tabWxpayPageController.getWxpayOrderQueryResponseBean();
	}

}
