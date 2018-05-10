package checkin;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.AlipayPreCreateResponseBean;
import beans.AlipayTradeQueryResponseBean;
import beans.Params;
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
 * 支付宝支付标签页控制器
 * @author GT
 *	ParentController CheckInPayController3
 */
public class TabAlipayPageControl implements Initializable {

	@FXML
	private BorderPane borderPane_tab_alipay_root;
	@FXML
	private Label label_info;// 信息提示
	@FXML
	private ImageView imageView_qrcode;// 二维码显示
	@FXML
	private Label label_result;// 预下单返回信息
	@FXML
	private Label label_title;// 标题

	/* 支付宝预下单结果 */
	private AlipayPreCreateResponseBean alipayPreCreateResponseBean;
	/* 支付宝交易查询结果 */
	private AlipayTradeQueryResponseBean alipayTradeQueryResponseBean;
	
	/*支付结果定时器*/
	private Timer timer;
	private boolean queryFlag;
	
	private OnShowPageListener showPageListener;
	
	public void setOnShowPageListener(OnShowPageListener listener) {
		this.showPageListener = listener;
	}

	/**
	 * 强制停止计时器
	 */
	public void forceStopTimer() {
		queryFlag = false;
		if(timer!=null) {
			timer.cancel();
			timer = null;
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		label_title.setText("支付宝支付");

		// 初始化数据结构
		alipayPreCreateResponseBean = new AlipayPreCreateResponseBean();
		alipayTradeQueryResponseBean = new AlipayTradeQueryResponseBean();
	}

	/**
	 * 开始处理支付宝界面逻辑
	 * 
	 * @param outTradeNo
	 *            商户订单号
	 */
	public void start(String outTradeNo) {
		if(outTradeNo == null) return;//测试时可能为null
		borderPane_tab_alipay_root.setVisible(false);
		if (outTradeNo.equals(alipayPreCreateResponseBean.getOut_trade_no())) {
			showQrCode();//直接显示
		} else {
			alipayPreCreatePost(outTradeNo);// 需要重新预下单
		}
	}

	/**
	 * 支付宝预下单 提交
	 */
	private void alipayPreCreatePost(String outTradeNo) {
		alipayPreCreateResponseBean.clear();
		alipayTradeQueryResponseBean.clear();
		// 显示提示对话框
		Stage dialog = getLoadingDialog();
		dialog.show();
		(new Thread(new Runnable() {

			@Override
			public void run() {
				String spec = Params.URL_ALIPAY_PRECREATE;
				HashMap<String, String> map = new HashMap<>();
				map.put("out_trade_no", outTradeNo);
				String json = NetworkHelper.downloadString(spec, map, "POST");
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
		label_info.setText("请使用支付宝扫描二维码!");
		// 支付宝处理结果
		if (alipayPreCreateResponseBean.getCode().equals("10000")) {
			Image img_alipay_qrcode = QRCodeHelper.zxingQRCodeCreate(alipayPreCreateResponseBean.getQr_code(), 300,
					300);
			Logger.getLogger(getClass().getSimpleName()).log(Level.INFO,
					"alipay qrcode is null : " + (img_alipay_qrcode == null));
			imageView_qrcode.setImage(img_alipay_qrcode);
			label_result.setText("");//设置空值，否则二维码上面会有文字
		} else {
			label_result.setText(alipayPreCreateResponseBean.getSubmsg());
		}
		borderPane_tab_alipay_root.setVisible(true);
		if (alipayPreCreateResponseBean.getCode().equals("10000")) {
			startQuery();//开始询查
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
		queryFlag = false;// 置可询查标识为false, 防止重复发起请求
		(new Thread(new Runnable() {

			@Override
			public void run() {
				int code = queryAlipayStatus();
				
				Logger.getLogger(TabAlipayPageControl.class.getSimpleName()).log(Level.INFO, "query code :" + (alipayTradeQueryResponseBean.getCode()));
				if (code != 1 || alipayTradeQueryResponseBean.getCode() == 1) {
					queryFlag = true;// 支付宝支付都没有完成支付
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
	 * 询查支付宝支付状态
	 */
	private int queryAlipayStatus() {
		// 清空查询信息
		alipayTradeQueryResponseBean.clear();
		
		String spec = Params.URL_ALIPAY_QUERY;
		HashMap<String, String> map = new HashMap<>();
		map.put("out_trade_no", alipayPreCreateResponseBean.getOut_trade_no());
		String json = NetworkHelper.downloadString(spec, map, "POST");

		Logger.getLogger(TabAlipayPageControl.class.getSimpleName()).log(Level.INFO, json);

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
		return code;
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

	public AlipayTradeQueryResponseBean getAlipayTradeQueryResponseBean() {
		// TODO Auto-generated method stub
		return alipayTradeQueryResponseBean;
	}

}
