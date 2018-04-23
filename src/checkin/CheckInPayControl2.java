package checkin;

import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.AlipayPreCreateResponseBean;
import beans.AlipayTradeQueryResponseBean;
import beans.GoodsBean;
import beans.WxpayOrderQueryResponseBean;
import beans.WxpayPreCreateResponseBean;
import beans.WxpayPreCreateResponseBean.RETURN_CODE;
import checkin.CheckInControl.OnGetDealListener;
import checkin.CheckInControl.OnGetTradeQueryResponseListener;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CheckInPayControl2 implements Initializable, OnGetTradeQueryResponseListener {

	private ArrayList<GoodsBean> goodsList;// 提交的商品列表
	private double totalPrice;// 总额
	private double discountPrice;// 折扣金额
	private double payPrice;// 支付金额

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
	private ImageView imageView_wxpay_qrcode;
	@FXML
	private Label label_alipay_result;
	@FXML
	private Label label_wxpay_result;

	/**
	 * 定时查询支付状态
	 */
	private Timer timer;
	private boolean queryFlag;// 可查询标识

	/**
	 * 支付宝支付预下单请求的结果
	 */
	private AlipayPreCreateResponseBean alipayPreCreateResponseBean;
	/**
	 * 支付宝交易查询请求结果
	 */
	private AlipayTradeQueryResponseBean alipayTradeQueryResponseBean;
	/**
	 * 微信支付预下单请求结果
	 */
	private WxpayPreCreateResponseBean wxpayPreCreateResponseBean;

	/**
	 * 微信支付支付结果查询结果
	 */
	private WxpayOrderQueryResponseBean wxpayOrderQueryResponseBean;

	private OnShowPageListener showPageListener;

	private OnGetDealListener getDealListener;

	public void setOnGetDealListener(OnGetDealListener getDealListener) {
		this.getDealListener = getDealListener;
	}

	public void setOnShowPageListener(OnShowPageListener listener) {
		this.showPageListener = listener;
	}

	/**
	 * 退出窗口时调用，强制停止计时器
	 */
	public void exitForceStopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	/**
	 * 开始处理支付逻辑
	 */
	public void start() {
		// 获取商品信息
		if (getDealListener != null) {
			goodsList = getDealListener.getGoodsList();
			totalPrice = getDealListener.getTotalPrice();
			discountPrice = getDealListener.getDiscountPrice();
			payPrice = getDealListener.getPayPrice();
		}
		// 开始清空结果信息， 设置二维码展示面板不可见
		label_alipay_result.setText("");
		label_wxpay_result.setText("");
		alipay_qrpane.setVisible(false);
		wxpay_qrpane.setVisible(false);
		// 提交给后台，获取二维码信息
		Stage dialog = getLoadingDialog();
		dialog.show();
		(new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String spec = "http://localhost:8888/api/market/Goods_Sale/submit";
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
				Logger.getLogger(CheckInPayControl2.class.getSimpleName()).log(Level.INFO,
						"goods_detail: " + goods_detail);
				try {
					goods_detail = Base64.getEncoder().encodeToString(goods_detail.getBytes());// !important
					// goods_detail = URLEncoder.encode(goods_detail, "utf-8");
					map.put("goods_detail", goods_detail);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String json = NetworkHelper.downloadString(spec, map, "POST", true);// TODO delete debug

				Logger.getLogger(CheckInPayControl2.class.getSimpleName()).log(Level.INFO, json);

				JSONObject jsonObj = JSONObject.fromObject(json);
				int code = jsonObj.getInt("code");
				final String msg = jsonObj.getString("msg");
				if (code == 1) {
					JSONObject data = jsonObj.getJSONObject("data");
					if (data.has("alipay")) {
						/**
						 * 支付宝支付
						 */
						JSONObject alipay = data.getJSONObject("alipay");
						JSONObject alipay_response = alipay.getJSONObject("alipay_trade_precreate_response");
						String alipay_code = alipay_response.getString("code");
						alipayPreCreateResponseBean.setCode(alipay_code);
						alipayPreCreateResponseBean.setMsg(alipay_response.getString("msg"));
						if (alipay_code.equals("10000")) {
							alipayPreCreateResponseBean.setOut_trade_no(alipay_response.getString("out_trade_no"));
							alipayPreCreateResponseBean.setQr_code(alipay_response.getString("qr_code"));
						} else {
							alipayPreCreateResponseBean.setSubcode(alipay_response.getString("sub_code"));
							alipayPreCreateResponseBean.setSubmsg(alipay_response.getString("sub_msg"));
						}
						alipayPreCreateResponseBean.setSign(alipay.getString("sign"));
					}
					if (data.has("wxpay")) {
						/**
						 * 微信支付
						 */
						JSONObject wxpay = data.getJSONObject("wxpay");
						wxpayPreCreateResponseBean.setReturn_code(wxpay.getString("return_code"));
						wxpayPreCreateResponseBean.setReturn_msg(wxpay.getString("return_msg"));
						wxpayPreCreateResponseBean.setOut_trade_no(wxpay.getString("out_trade_no"));
						if (wxpayPreCreateResponseBean.getReturn_code().equals(String.valueOf(RETURN_CODE.SUCCESS))) {
							wxpayPreCreateResponseBean.setAppid(wxpay.getString("appid"));
							wxpayPreCreateResponseBean.setMch_id(wxpay.getString("mch_id"));
							wxpayPreCreateResponseBean.setDevice_info(wxpay.optString("devie_info"));
							wxpayPreCreateResponseBean.setNonce_str(wxpay.getString("nonce_str"));
							wxpayPreCreateResponseBean.setSign(wxpay.getString("sign"));
							wxpayPreCreateResponseBean.setResult_code(wxpay.getString("result_code"));
							wxpayPreCreateResponseBean.setErr_code(wxpay.optString("err_code"));
							wxpayPreCreateResponseBean.setErr_code_des(wxpay.optString("err_code_des"));
							if (wxpayPreCreateResponseBean.getResult_code()
									.equals(String.valueOf(RETURN_CODE.SUCCESS))) {
								wxpayPreCreateResponseBean.setTrade_type(wxpay.getString("trade_type"));
								wxpayPreCreateResponseBean.setCode_url(wxpay.getString("code_url"));
								wxpayPreCreateResponseBean.setPrepay_id(wxpay.getString("prepay_id"));
							}
						}
					}
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							dialog.hide();
							showQrCode();
						}
					});
				} else {
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
		label_pay_msg.setText("请使用支付宝或者微信扫码支付!");
		// 支付宝处理结果
		if (alipayPreCreateResponseBean.getCode().equals("10000")) {
			Image img_alipay_qrcode = QRCodeHelper.zxingQRCodeCreate(alipayPreCreateResponseBean.getQr_code(), 300,
					300);
			Logger.getLogger(getClass().getSimpleName()).log(Level.INFO,
					"alipay qrcode is null : " + (img_alipay_qrcode == null));
			imageView_alipay_qrcode.setImage(img_alipay_qrcode);
		} else {
			label_alipay_result.setText(alipayPreCreateResponseBean.getSubmsg());
		}
		// 微信支付处理结果
		if (wxpayPreCreateResponseBean.getReturn_code().equals(String.valueOf(RETURN_CODE.SUCCESS))
				&& wxpayPreCreateResponseBean.getResult_code().equals(String.valueOf(RETURN_CODE.SUCCESS))) {
			Image img_wxpay_qrcode = QRCodeHelper.zxingQRCodeCreate(wxpayPreCreateResponseBean.getCode_url(), 300, 300);
			Logger.getLogger(getClass().getSimpleName()).log(Level.INFO,
					"wxpay qrcode is null : " + (img_wxpay_qrcode == null));
			imageView_wxpay_qrcode.setImage(img_wxpay_qrcode);
		} else {
			label_wxpay_result.setText(
					wxpayPreCreateResponseBean.getReturn_msg() + " " + wxpayPreCreateResponseBean.getErr_code_des());
		}
		alipay_qrpane.setVisible(true);
		wxpay_qrpane.setVisible(true);

		// 可以询查支付情况
		if (alipayPreCreateResponseBean.getCode().equals("10000")
				|| (wxpayPreCreateResponseBean.getReturn_code().equals(String.valueOf(RETURN_CODE.SUCCESS))
						&& wxpayPreCreateResponseBean.getResult_code().equals(String.valueOf(RETURN_CODE.SUCCESS)))) {
			startQuery();
		} // else 均未生成二维码，不可询查支付情况
	}

	/**
	 * 开始询查支付状态
	 */
	private void startQuery() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (queryFlag == false)
						return;
					queryPayStatus();
				}
			}, 0, 2 * 1000);// 2s查询一次
		}
		queryFlag = true;
	}

	/**
	 * 查询支付状态
	 */
	protected void queryPayStatus() {
		// 清空查询信息
		alipayTradeQueryResponseBean.clear();
		wxpayOrderQueryResponseBean.clear();
		queryFlag = false;// 置可询查标识为false, 防止重复发起请求
		(new Thread(new Runnable() {

			@Override
			public void run() {
				queryAlipayStatus();
				queryWxpaySatus();

				if (alipayTradeQueryResponseBean.getCode() == 1 && wxpayOrderQueryResponseBean.getCode() == 1) {
					queryFlag = true;// 支付宝支付和微信支付都没有完成支付
				} else {
					// queryFlag = false;//以及完成支付，不再进行询查
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (showPageListener != null) {
								showPageListener.showPage(4);// 显示最后一页
							}
						}
					});
				}
			}
		})).start();
	}

	/**
	 * 询查支付宝支付状态
	 */
	private void queryAlipayStatus() {
		String spec = "http://localhost:8888/api/market/Goods_Sale/alipayQuery";
		HashMap<String, String> map = new HashMap<>();
		map.put("out_trade_no", alipayPreCreateResponseBean.getOut_trade_no());
		String json = NetworkHelper.downloadString(spec, map, "POST");

		Logger.getLogger(CheckInPayControl2.class.getSimpleName()).log(Level.INFO, json);

		JSONObject jsonObj = JSONObject.fromObject(json);
		int code = jsonObj.getInt("code");
		if (code == 1) {
			JSONObject data = jsonObj.getJSONObject("data");
			alipayTradeQueryResponseBean.setCode(data.getInt("code"));
			alipayTradeQueryResponseBean.setStatus(data.getString("status"));
			alipayTradeQueryResponseBean.setOut_trade_no(data.getString("out_trade_no"));
		} else {
			// TODO 错误处理
		}
	}

	/**
	 * 查询微信支付支付状态
	 */
	private void queryWxpaySatus() {
		String spec = "http://localhost:8888/api/market/Goods_Sale/wxpayQuery";
		HashMap<String, String> map = new HashMap<>();
		map.put("out_trade_no", wxpayPreCreateResponseBean.getOut_trade_no());
		String json = NetworkHelper.downloadString(spec, map, "POST");

		Logger.getLogger(CheckInPayControl2.class.getSimpleName()).log(Level.INFO, json);

		JSONObject jsonObj = JSONObject.fromObject(json);
		int code = jsonObj.getInt("code");
		if (code == 1) {
			JSONObject data = jsonObj.getJSONObject("data");
			wxpayOrderQueryResponseBean.setCode(data.getInt("code"));
			wxpayOrderQueryResponseBean.setStatus(data.getString("status"));
			wxpayOrderQueryResponseBean.setOut_trade_no(data.getString("out_trade_no"));
		} else {
			// TODO 错误处理
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getBounds();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		checkin_pay_root.setPrefSize(width, height);
		// 初始化按钮
		Image img_return = new Image("file:assets/drawable/return.png");
		btn_cancel_pay.setGraphic(new ImageView(img_return));
		btn_cancel_pay.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				if (showPageListener != null) {
					showPageListener.showPage(2);
				}
			};
		});
		// 初始化微信qr code
		// Image image_wx_qrcode = new Image("file:assets/drawable/wx_qrcode.png");
		// imageView_wx_qrcode.setImage(image_wx_qrcode);

		// 西方和美国真让人恶心！！！
		alipayPreCreateResponseBean = new AlipayPreCreateResponseBean();
		alipayTradeQueryResponseBean = new AlipayTradeQueryResponseBean();
		wxpayPreCreateResponseBean = new WxpayPreCreateResponseBean();
		wxpayOrderQueryResponseBean = new WxpayOrderQueryResponseBean();
	}

	private Stage getLoadingDialog() {
		Stage dialog = new Stage(StageStyle.TRANSPARENT);
		ProgressIndicator pi = new ProgressIndicator();
		Text text = new Text("提交数据中...");
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

	@Override
	public AlipayTradeQueryResponseBean geAlipayTradeQueryResponseBean() {
		return alipayTradeQueryResponseBean;
	}

	@Override
	public WxpayOrderQueryResponseBean getWxpayOrderQueryResponseBean() {
		return wxpayOrderQueryResponseBean;
	}

}
