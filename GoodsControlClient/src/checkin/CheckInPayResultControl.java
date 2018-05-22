package checkin;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import beans.AlipayTradeQueryResponseBean;
import beans.WxpayOrderQueryResponseBean;
import checkin.CheckInControl.OnGetTradeQueryResponseListener;
import checkin.CheckInControl.OnShowPageListener;
import checkin.CheckInControl.Page;
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

public class CheckInPayResultControl implements Initializable {
	
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
	
	private OnShowPageListener showPageListener;
	
	private OnGetTradeQueryResponseListener getTradeQueryResponseListener;
	
	public void setOnShowPageListener(OnShowPageListener listener) {
		this.showPageListener = listener;
	}
	
	public void setOnGetTradeQueryResponseListener(OnGetTradeQueryResponseListener listener) {
		this.getTradeQueryResponseListener = listener;
	}
	
	/**
	 * 开始处理业务逻辑
	 */
	public void start() {
		if(getTradeQueryResponseListener!=null) {
			AlipayTradeQueryResponseBean alipayTradeQueryResponseBean = getTradeQueryResponseListener.geAlipayTradeQueryResponseBean();
			WxpayOrderQueryResponseBean wxpayOrderQueryResponseBean = getTradeQueryResponseListener.getWxpayOrderQueryResponseBean();
			
			/*支付宝支付*/
			if(alipayTradeQueryResponseBean.getCode() == 2 || wxpayOrderQueryResponseBean.getCode() == 2) {//交易关闭，可能是由于超时引起
				Image img = new Image("file:resource/drawable/pay_closed.png");
				label_result_msg.setText("交易关闭!");
				label_result_msg.setGraphic(new ImageView(img));
				
				label_tip.setText("请在5min内完成付款!");
			}else if(alipayTradeQueryResponseBean.getCode() == 3 || wxpayOrderQueryResponseBean.getCode() == 3) {
				Image img = new Image("file:resource/drawable/pay_ok.png");
				label_result_msg.setText("交易成功!");
				label_result_msg.setGraphic(new ImageView(img));
				
				label_tip.setText("感谢您使用自助收银系统!欢迎再次光临!");
			}else if(alipayTradeQueryResponseBean.getCode() == -1  || wxpayOrderQueryResponseBean.getCode() == -1) {
				Image img = new Image("file:resource/drawable/pay_fail.png");
				label_result_msg.setText("出错!");
				label_result_msg.setGraphic(new ImageView(img));
				
				label_tip.setText(alipayTradeQueryResponseBean.getStatus() + " 请联系管理员!");
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
	}

}
