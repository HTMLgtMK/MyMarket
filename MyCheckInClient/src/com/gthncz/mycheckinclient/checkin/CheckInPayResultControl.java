package com.gthncz.mycheckinclient.checkin;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gthncz.mycheckinclient.beans.AlipayTradeQueryResponseBean;
import com.gthncz.mycheckinclient.beans.BalancepayTradeQueryResponse;
import com.gthncz.mycheckinclient.beans.GoodsBean;
import com.gthncz.mycheckinclient.beans.Params;
import com.gthncz.mycheckinclient.beans.WxpayOrderQueryResponseBean;
import com.gthncz.mycheckinclient.checkin.CheckInControl.OnGetDealListener;
import com.gthncz.mycheckinclient.checkin.CheckInControl.OnGetTradeQueryResponseListener;
import com.gthncz.mycheckinclient.checkin.CheckInControl.OnShowPageListener;
import com.gthncz.mycheckinclient.checkin.CheckInControl.Page;
import com.gthncz.mycheckinclient.helper.INIHelper;

import application.Main;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import net.sf.json.JSONArray;

public class CheckInPayResultControl implements Initializable {
	private static final String TAG = CheckInPayResultControl.class.getSimpleName();
	
	@FXML
	private BorderPane checkin_pay_result_root;
	@FXML
	private Button btn_return_welcome;
	@FXML
	private Label label_result_msg;
	@FXML
	private Label label_tip;
	@FXML
	private Label label_return_timer;
	/*自动返回计时器*/
	private Timer timer;
	
	private int count;
	
	private ExecutorService executor;
	private int nThreads;
	private static final int DEFAULT_nTHREAD = 5;
	
	private String safeguardAddr = null;
	private static final String SAFEGUARD_ADDR = "127.0.0.1";
	private int safeguardPort = DEFAULT_SAFEGUARD_PORT;
	private static final int DEFAULT_SAFEGUARD_PORT = 8686;
	
	private OnShowPageListener showPageListener;
	
	private OnGetTradeQueryResponseListener getTradeQueryResponseListener;
	
	private ArrayList<GoodsBean> goodsList;// 提交的商品列表
	private OnGetDealListener getDealListener;
	
	public void setDealListener(OnGetDealListener listener) {
		this.getDealListener = listener;
	}
	
	public void setOnShowPageListener(OnShowPageListener listener) {
		this.showPageListener = listener;
	}
	
	public void setOnGetTradeQueryResponseListener(OnGetTradeQueryResponseListener listener) {
		this.getTradeQueryResponseListener = listener;
	}
	
	public void terminateReporter() {
		if(executor != null) {
			executor.shutdown();
		}
	}
	
	/**
	 * 开始处理业务逻辑
	 */
	public void start() {
		if(getDealListener != null) {
			goodsList = getDealListener.getGoodsList();
		}
		if(getTradeQueryResponseListener!=null) {
			AlipayTradeQueryResponseBean alipayTradeQueryResponseBean = getTradeQueryResponseListener.geAlipayTradeQueryResponseBean();
			WxpayOrderQueryResponseBean wxpayOrderQueryResponseBean = getTradeQueryResponseListener.getWxpayOrderQueryResponseBean();
			BalancepayTradeQueryResponse  balancepayTradeQueryResponse= getTradeQueryResponseListener.geBalancepayTradeQueryResponse();
			
			/*支付宝支付*/
			if(alipayTradeQueryResponseBean.getCode() == 2 || wxpayOrderQueryResponseBean.getCode() == 2 
					|| balancepayTradeQueryResponse.getStatus() == 4) {//交易关闭，可能是由于超时引起
				Image img = new Image("file:resource/drawable/pay_closed.png");
				label_result_msg.setText("交易关闭!");
				label_result_msg.setGraphic(new ImageView(img));
				
				label_tip.setText("请在5min内完成付款!");
			}else if(alipayTradeQueryResponseBean.getCode() == 3 || wxpayOrderQueryResponseBean.getCode() == 3
					|| balancepayTradeQueryResponse.getStatus() == 3) {
				Image img = new Image("file:resource/drawable/pay_ok.png");
				label_result_msg.setText("交易成功!");
				label_result_msg.setGraphic(new ImageView(img));
				
				label_tip.setText("感谢您使用自助收银系统!欢迎再次光临!");
				
				if(goodsList != null) {
					ReportGoodsTask task = new ReportGoodsTask(goodsList);
					executor.execute(task);
				}
				
			}else if(alipayTradeQueryResponseBean.getCode() == -1  || wxpayOrderQueryResponseBean.getCode() == -1
					|| balancepayTradeQueryResponse.getCode() == 0) {
				Image img = new Image("file:resource/drawable/pay_fail.png");
				label_result_msg.setText("出错!");
				label_result_msg.setGraphic(new ImageView(img));
				
				label_tip.setText("请联系管理员!");
			}
		}
		count = 5;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// 返回首页
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						if(Main.DEBUG) {
							Logger.getLogger(TAG).log(Level.INFO, "** 信息 >> CheckInPayResult CountDown Timer is running...");
						}
						if(count == 0) {
							timer.cancel();
							timer = null;
							
							if(showPageListener!=null) {
								showPageListener.showPage(Page.PAGE_WELCOME);
							}
							return;
						}
						label_return_timer.setText(String.format("还有 %d 秒返回首页", count--));
					}
				});
			}
		}, 0, 1*1000);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getBounds();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		checkin_pay_result_root.setPrefSize(width, height);
		
		btn_return_welcome.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				if(timer != null) { // 取消倒计时
					timer.cancel();
					timer = null;
				}
				if(showPageListener!=null) {//返回欢迎页
					showPageListener.showPage(Page.PAGE_WELCOME);
				}
			}; 
		});
		nThreads = DEFAULT_nTHREAD;
		executor = Executors.newFixedThreadPool(nThreads);
		
		HashMap<String, String> ini = INIHelper.getIniSet(Params.INI_NAME);
		if(ini == null) {
			safeguardAddr = SAFEGUARD_ADDR;
			safeguardPort = DEFAULT_SAFEGUARD_PORT;
		} else {
			String addr = ini.get("safeguard_addr");
			String port = ini.get("safeguard_port");
			safeguardAddr = addr == null ? SAFEGUARD_ADDR : addr;
			safeguardPort = port == null ? DEFAULT_SAFEGUARD_PORT : Integer.valueOf(port);
		}
	}
	
	private class ReportGoodsTask implements Runnable{
		private ArrayList<GoodsBean> goodsList;
		
		public ReportGoodsTask(ArrayList<GoodsBean> goodsList) {
			this.goodsList = new ArrayList<>();
			this.goodsList.addAll(goodsList); // 备份, 防止被清空
		}
		
		@Override
		public void run() {
			Logger.getLogger(TAG).log(Level.INFO, "runing the report task...");
			Socket socket = null;
			OutputStream os  = null;
			OutputStreamWriter writer = null;
			try {
				socket = new Socket(safeguardAddr, safeguardPort);
				socket.setSoTimeout(10000);
				os= socket.getOutputStream();
				writer = new OutputStreamWriter(os, "UTF-8");
				JSONArray jsonArr = JSONArray.fromObject(goodsList);
				String goodsListDetail = jsonArr.toString();
				Logger.getLogger(TAG).log(Level.INFO, goodsListDetail);
				writer.write(goodsListDetail);
				writer.flush();
			} catch (IOException e) {
//				e.printStackTrace(); // 不打印异常
			} finally {
				try {
					if(writer != null) {
						writer.close();
					}
					if(os != null) {
						os.close();
					}
					if(socket != null) {
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
