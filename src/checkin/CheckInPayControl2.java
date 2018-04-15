package checkin;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.AlipayPreCreateResponseBean;
import beans.GoodsBean;
import checkin.CheckInControl.OnGetDealListener;
import checkin.CheckInControl.OnShowPageListener;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CheckInPayControl2 implements Initializable {
	
	private ArrayList<GoodsBean> goodsList;//提交的商品列表
	private double totalPrice;//总额
	private double discountPrice;//折扣金额
	private double payPrice;//支付金额
	
	@FXML
	private BorderPane checkin_pay_root;
	@FXML
	private Label label_pay_msg;
	@FXML
	private VBox alipay_qrpane;
	@FXML
	private VBox wxpay_qrpane;
	@FXML
	private Button btn_cancel_pay;
	@FXML
	private ImageView imageView_alipay_qrcode;
	@FXML
	private ImageView imageView_wx_qrcode;
	
	/**
	 * 支付宝支付预下单请求的结果
	 */
	private AlipayPreCreateResponseBean alipayPreCreateResponseBean;
	
	private OnShowPageListener showPageListener;
	
	private OnGetDealListener getDealListener;
	
	public void setOnGetDealListener(OnGetDealListener getDealListener) {
		this.getDealListener = getDealListener;
	}
	
	public void setOnShowPageListener(OnShowPageListener listener) {
		this.showPageListener = listener;
	}
	
	/**
	 * 开始处理支付逻辑
	 */
	public void start() {
		//获取商品信息
		if(getDealListener!=null) {
			goodsList = getDealListener.getGoodsList();
			totalPrice = getDealListener.getTotalPrice();
			discountPrice = getDealListener.getDiscountPrice();
			payPrice = getDealListener.getPayPrice();
		}
		//提交给后台，获取二维码信息
		Stage dialog = getLoadingDialog();
		dialog.show();
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String spec = "http://localhost:8888/api/market/Goods_Sale/submit";
				HashMap<String,String> map = new HashMap<>();
				map.put("user_id", "1");//TODO 用户ID先固定为非VIP用户
				map.put("store_id", "1");// TODO 店铺ID先固定为1号店
				map.put("terminal_id", "1");//TODO 终端ID先固定为1号终端
				map.put("pay_amount", String.valueOf(payPrice));
				map.put("discount_amount", String.valueOf(discountPrice));
				map.put("total_amount", String.valueOf(totalPrice));
				//组装商品详情
				JSONArray objArr = JSONArray.fromObject(goodsList);
				String goods_detail = objArr.toString();
				Logger.getLogger(CheckInPayControl2.class.getSimpleName()).log(Level.INFO,"goods_detail: " + goods_detail);
				try {
					goods_detail = Base64.getEncoder().encodeToString(goods_detail.getBytes());// !important
					//goods_detail = URLEncoder.encode(goods_detail, "utf-8");
					map.put("goods_detail", goods_detail);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String json = NetworkHelper.downloadString(spec, map, "POST");
				
				Logger.getLogger(CheckInPayControl2.class.getSimpleName()).log(Level.INFO, json);
				
				JSONObject jsonObj = JSONObject.fromObject(json);
				int code = jsonObj.getInt("code");
				final String msg = jsonObj.getString("msg");
				if(code==1) {
					JSONObject data = jsonObj.getJSONObject("data");
					if(data.has("alipay")) {
						JSONObject alipay = data.getJSONObject("alipay");
						JSONObject alipay_response = alipay.getJSONObject("alipay_trade_precreate_response");
						String alipay_code = alipay_response.getString("code");
						alipayPreCreateResponseBean.setCode(alipay_code);
						alipayPreCreateResponseBean.setMsg(alipay_response.getString("msg"));
						if(alipay_code.equals("10000")) {
							alipayPreCreateResponseBean.setOut_trade_no(alipay_response.getString("out_trade_no"));
							alipayPreCreateResponseBean.setQr_code(alipay_response.getString("qr_code"));
						}else {
							alipayPreCreateResponseBean.setSubcode(alipay_response.getString("sub_code"));
							alipayPreCreateResponseBean.setSubmsg(alipay_response.getString("sub_msg"));
						}
						alipayPreCreateResponseBean.setSign(alipay.getString("sign"));
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								dialog.hide();
								showQrCode();
							}
						});
					}
				}else {
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							dialog.hide();
							label_pay_msg.setText(msg);
						}
					});
				}
			}
		})).start();
	}
	
	/**
	 * 展示二维码
	 */
	private void showQrCode() {
		if(alipayPreCreateResponseBean.getCode().equals("10000")) {
			label_pay_msg.setText("请使用支付宝或者微信扫码支付!");
			Image img_alipay_qrcode = QRCodeHelper.zxingQRCodeCreate(alipayPreCreateResponseBean.getQr_code(), 300, 300);
			Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "qrcode is null : " + (img_alipay_qrcode == null));
			imageView_alipay_qrcode.setImage(img_alipay_qrcode);
			alipay_qrpane.setVisible(true);
		}else {
			label_pay_msg.setText(alipayPreCreateResponseBean.getSubmsg());
		}
		wxpay_qrpane.setVisible(true);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getBounds();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		checkin_pay_root.setPrefSize(width, height);
		//初始化按钮
		Image img_return = new Image("file:assets/drawable/return.png");
		btn_cancel_pay.setGraphic(new ImageView(img_return));
		btn_cancel_pay.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				if(showPageListener!=null) {
					showPageListener.showPage(2);
				}
			};
		});
		//初始化微信qr code
		Image image_wx_qrcode = new Image("file:assets/drawable/wx_qrcode.png");
		imageView_wx_qrcode.setImage(image_wx_qrcode);
		
		alipay_qrpane.setVisible(false);//开始设置二维码展示面板不可见
		wxpay_qrpane.setVisible(false);
		//西方和美国真让人恶心！！！
		alipayPreCreateResponseBean = new AlipayPreCreateResponseBean();
	}
	
	private Stage getLoadingDialog() {
		Stage dialog = new Stage(StageStyle.TRANSPARENT);
		ProgressIndicator pi = new ProgressIndicator();
		Text text = new Text("提交数据中...");
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
		return dialog;
	}

}
