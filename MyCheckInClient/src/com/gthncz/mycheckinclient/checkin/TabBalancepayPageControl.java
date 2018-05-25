package com.gthncz.mycheckinclient.checkin;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gthncz.mycheckinclient.beans.BalancepayPreCreateResponseBean;
import com.gthncz.mycheckinclient.beans.BalancepayTradeQueryResponse;
import com.gthncz.mycheckinclient.beans.Params;
import com.gthncz.mycheckinclient.beans.UserBean;
import com.gthncz.mycheckinclient.checkin.CheckInControl.OnShowPageListener;
import com.gthncz.mycheckinclient.checkin.CheckInControl.Page;
import com.gthncz.mycheckinclient.helper.NetworkHelper;
import com.gthncz.mycheckinclient.helper.QRCodeHelper;

import application.Main;
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
 * 余额支付标签页控制器
 * @author GT
 *	ParentController CheckInPayController3
 */
public class TabBalancepayPageControl implements Initializable {
	private static final String TAG = TabBalancepayPageControl.class.getSimpleName();

	@FXML
	private BorderPane borderPane_tab_balancepay_root;
	@FXML
	private Label label_info;// 信息提示
	@FXML
	private ImageView imageView_qrcode;// 二维码显示
	@FXML
	private Label label_result;// 预下单返回信息
	@FXML
	private Label label_title;// 标题

	/* 余额支付预下单结果 */
	private BalancepayPreCreateResponseBean balancepayPreCreateResponseBean;
	/* 余额支付交易查询结果 */
	private BalancepayTradeQueryResponse balancepayTradeQueryResponse;
	
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
		label_title.setText("余额支付");

		// 初始化数据结构
		balancepayPreCreateResponseBean = new BalancepayPreCreateResponseBean();
		balancepayTradeQueryResponse = new BalancepayTradeQueryResponse();
	}

	/**
	 * 开始处理余额支付界面逻辑
	 * 
	 * @param outTradeNo
	 *            商户订单号
	 */
	public void start(String outTradeNo, UserBean userBean) {
		if(outTradeNo == null) return;//测试时可能为null
		borderPane_tab_balancepay_root.setVisible(false);
		if(userBean == null) {
			borderPane_tab_balancepay_root.setVisible(true);
			label_info.setText(null);
			label_result.setText("用户未授权!");
			return;
		}
		if (outTradeNo.equals(balancepayPreCreateResponseBean.getOutTradeNo())) {
			showQrCode();//直接显示
		} else {
			balancePayPreCreatePost(outTradeNo, userBean);// 需要重新预下单
		}
	}

	/**
	 * 余额支付预下单 提交
	 */
	private void balancePayPreCreatePost(String outTradeNo, UserBean userBean) {
		balancepayPreCreateResponseBean.clear();
		balancepayTradeQueryResponse.clear();
		// 显示提示对话框
		Stage dialog = getLoadingDialog();
		dialog.show();
		(new Thread(new Runnable() {

			@Override
			public void run() {
				String spec = Params.URL_BALANCE_PAY_PRECREATE;
				HashMap<String, String> map = new HashMap<>();
				map.put("out_trade_no", outTradeNo);
				map.put("user_id", String.valueOf(userBean.getId()));
				String json = NetworkHelper.downloadString(spec, map, "POST");
				JSONObject jsonObj = JSONObject.fromObject(json);
				Logger.getLogger(TabBalancepayPageControl.class.getSimpleName()).log(Level.INFO, "** 信息 >> balance pay precreate json: "+json);
				int code = jsonObj.getInt("code");
				final String msg = jsonObj.getString("msg");
				balancepayPreCreateResponseBean.setCode(code);
				balancepayPreCreateResponseBean.setMsg(msg);
				if (code == 1) {
					JSONObject data = jsonObj.getJSONObject("data");
					if (data.has("balancepay")) {
						/**
						 * 余额支付
						 */
						JSONObject balancepay = data.getJSONObject("balancepay");
						balancepayPreCreateResponseBean.setId(balancepay.getString("id"));
						balancepayPreCreateResponseBean.setOutTradeNo(balancepay.getString("out_trade_no"));
						balancepayPreCreateResponseBean.setUserId(balancepay.getString("user_id"));
						balancepayPreCreateResponseBean.setToken(balancepay.getString("token"));
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
							showQrCode(); // 只是为了显示错误信息
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
		label_info.setText("请使用手机客户端扫描二维码!");
		// 余额处理结果
		if (balancepayPreCreateResponseBean.getCode() == 1) {
			String url = "market://balancePay/" + balancepayPreCreateResponseBean.getToken();
			Image img_balancepay_qrcode = QRCodeHelper.zxingQRCodeCreate(url, 300,
					300);
			Logger.getLogger(getClass().getSimpleName()).log(Level.INFO,
					"balancepay qrcode is null : " + (img_balancepay_qrcode == null));
			imageView_qrcode.setImage(img_balancepay_qrcode);
			label_result.setText("");//设置空值，否则二维码上面会有文字
		} else {
			label_result.setText(balancepayPreCreateResponseBean.getMsg());
		}
		borderPane_tab_balancepay_root.setVisible(true);
		if (balancepayPreCreateResponseBean.getCode() == 1) {
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
					if(Main.DEBUG) {
						Logger.getLogger(TAG).log(Level.INFO, "** 信息 >> BalancePay query Timer is running...");
					}
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
				queryBalancePayStatus();
				
				Logger.getLogger(TabBalancepayPageControl.class.getSimpleName()).log(Level.INFO, "query code :" + (balancepayTradeQueryResponse.getCode()));
				
				if (balancepayTradeQueryResponse.getCode() == 1 
						&& ( balancepayTradeQueryResponse.getStatus() == 1  || balancepayTradeQueryResponse.getStatus() == 2)) {
					queryFlag = true;// 余额支付都没有完成支付
				} else {
					// queryFlag = false;// 已完成支付，不再进行询查
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							forceStopTimer();
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
	 * 询查余额支付状态
	 */
	private void queryBalancePayStatus() {
		// 清空查询信息
		balancepayTradeQueryResponse.clear();
		
		String spec = Params.URL_BALANCE_PAY_TRADE_QUERY;
		HashMap<String, String> map = new HashMap<>();
		map.put("out_trade_no", balancepayPreCreateResponseBean.getOutTradeNo());
		map.put("token", balancepayPreCreateResponseBean.getToken());
		String json = NetworkHelper.downloadString(spec, map, "POST");

		Logger.getLogger(TabBalancepayPageControl.class.getSimpleName()).log(Level.INFO, json);

		JSONObject jsonObj = JSONObject.fromObject(json);
		int code = jsonObj.getInt("code");
		String msg = jsonObj.getString("msg");
		balancepayTradeQueryResponse.setCode(code);
		balancepayTradeQueryResponse.setMsg(msg);
		if (code == 1) {
			JSONObject data = jsonObj.getJSONObject("data");
			balancepayTradeQueryResponse.setStatus(data.getInt("status"));
			balancepayTradeQueryResponse.setOutTradeNo(data.getString("out_trade_no"));
			balancepayTradeQueryResponse.setStatusDetail(data.getString("status_detail"));
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

	public BalancepayTradeQueryResponse getBalancepayTradeQueryResponse() {
		return balancepayTradeQueryResponse;
	}

}
