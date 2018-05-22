package checkin;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.Params;
import beans.WxpayOrderQueryResponseBean;
import beans.WxpayPreCreateResponseBean;
import beans.WxpayPreCreateResponseBean.RETURN_CODE;
import checkin.CheckInControl.OnShowPageListener;
import checkin.CheckInControl.Page;
import helper.NetworkHelper;
import helper.QRCodeHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.json.JSONObject;

/**
 * 微信支付标签页控制器
 * 
 * @author GT ParentController CheckInPayController3
 */
public class TabWxpayPageControl implements Initializable {

	@FXML
	private BorderPane borderPane_tab_wxpay_root;
	@FXML
	private Label label_info;// 信息提示
	@FXML
	private ImageView imageView_qrcode;// 二维码显示
	@FXML
	private Label label_result;// 预下单返回信息
	@FXML
	private Label label_title;// 标题

	/* 微信支付预下单结果 */
	private WxpayPreCreateResponseBean wxpayPreCreateResponseBean;
	/* 微信支付易查询结果 */
	private WxpayOrderQueryResponseBean wxpayOrderQueryResponseBean;

	/* 支付结果定时器 */
	private Timer timer;
	private boolean queryFlag;

	private OnShowPageListener showPageListener;

	public void setOnShowPageListener(OnShowPageListener listener) {
		this.showPageListener = listener;
	}
	
	public void forceStopTimer() {
		queryFlag = false;
		if(timer!=null) {
			timer.cancel();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		label_title.setText("微信支付");

		// 初始化数据结构
		wxpayPreCreateResponseBean = new WxpayPreCreateResponseBean();
		wxpayOrderQueryResponseBean = new WxpayOrderQueryResponseBean();
	}

	/**
	 * 开始处理支付宝界面逻辑
	 * 
	 * @param outTradeNo
	 *            商户订单号
	 */
	public void start(String outTradeNo) {
		if(outTradeNo == null) return;//测试时可能为null
		borderPane_tab_wxpay_root.setVisible(false);
		if (outTradeNo.equals(wxpayPreCreateResponseBean.getOut_trade_no())) {
			showQrCode();// 直接显示
		} else {
			wxpayPreCreatePost(outTradeNo);// 需要重新预下单
		}
	}

	/**
	 * 微信支付预下单 提交
	 */
	private void wxpayPreCreatePost(String outTradeNo) {
		wxpayPreCreateResponseBean.clear();
		wxpayOrderQueryResponseBean.clear();
		// 显示提示对话框
		Stage dialog = getLoadingDialog();
		dialog.show();
		(new Thread(new Runnable() {

			@Override
			public void run() {
				String spec = Params.URL_WXPAY_PRECREATE;
				HashMap<String, String> map = new HashMap<>();
				map.put("out_trade_no", outTradeNo);
				String json = NetworkHelper.downloadString(spec, map, "POST");
				
				Logger.getLogger(TabWxpayPageControl.class.getSimpleName()).log(Level.INFO, "wxpay  precreate: "+json);
				
				JSONObject jsonObj = JSONObject.fromObject(json);
				int code = jsonObj.getInt("code");
				final String msg = jsonObj.getString("msg");
				if (code == 1) {
					JSONObject data = jsonObj.getJSONObject("data");
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
							dialog.hide();
							showQrCode();
						}
					});
				} else {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							dialog.hide();
							label_info.setText(msg);
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
		label_info.setText("请使用微信扫描二维码!");
		// 微信支付处理结果
		if (wxpayPreCreateResponseBean.getReturn_code().equals(String.valueOf(RETURN_CODE.SUCCESS))
				&& wxpayPreCreateResponseBean.getResult_code().equals(String.valueOf(RETURN_CODE.SUCCESS))) {
			Image img_wxpay_qrcode = QRCodeHelper.zxingQRCodeCreate(wxpayPreCreateResponseBean.getCode_url(), 300, 300);
			Logger.getLogger(getClass().getSimpleName()).log(Level.INFO,
					"wxpay qrcode is null : " + (img_wxpay_qrcode == null));
			imageView_qrcode.setImage(img_wxpay_qrcode);
		} else {
			label_result.setText(
					wxpayPreCreateResponseBean.getReturn_msg() + " " + wxpayPreCreateResponseBean.getErr_code_des());
		}
		borderPane_tab_wxpay_root.setVisible(true);
		if (wxpayPreCreateResponseBean.getReturn_code().equals(String.valueOf(RETURN_CODE.SUCCESS))
				&& wxpayPreCreateResponseBean.getResult_code().equals(String.valueOf(RETURN_CODE.SUCCESS))) {
			startQuery();// 开始询查
		}
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
		wxpayOrderQueryResponseBean.clear();
		queryFlag = false;// 置可询查标识为false, 防止重复发起请求
		(new Thread(new Runnable() {

			@Override
			public void run() {
				queryWxpaySatus();
				
				if (wxpayOrderQueryResponseBean.getCode() == 1) {
					queryFlag = true;// 支付宝支付和微信支付都没有完成支付
				} else {
					// queryFlag = false;// 已完成支付，不再进行询查
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							if (showPageListener != null) {
								showPageListener.showPage(Page.PAGE_PAY_RESULT);// 显示最后一页
							}
						}
					});
				}
			}
		})).start();
	}

	/**
	 * 查询微信支付支付状态
	 */
	private void queryWxpaySatus() {
		String spec = Params.URL_WXPAY_QUERY;
		HashMap<String, String> map = new HashMap<>();
		map.put("out_trade_no", wxpayPreCreateResponseBean.getOut_trade_no());
		String json = NetworkHelper.downloadString(spec, map, "POST");

		Logger.getLogger(CheckInPayControl.class.getSimpleName()).log(Level.INFO, json);

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

	/**
	 * 等待对话框
	 * 
	 * @return
	 */
	private Stage getLoadingDialog() {
		Stage dialog = new Stage(StageStyle.TRANSPARENT);
		ProgressIndicator pi = new ProgressIndicator();
		Text text = new Text("获取二维码...");
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

	public WxpayOrderQueryResponseBean getWxpayOrderQueryResponseBean() {
		return wxpayOrderQueryResponseBean;
	}

}
