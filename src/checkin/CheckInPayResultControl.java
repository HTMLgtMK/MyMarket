package checkin;

import java.net.URL;
import java.util.ResourceBundle;

import beans.AlipayTradeQueryResponseBean;
import beans.WxpayOrderQueryResponseBean;
import checkin.CheckInControl.OnGetTradeQueryResponseListener;
import checkin.CheckInControl.OnShowPageListener;
import checkin.CheckInControl.Page;
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
				Image img = new Image("file:assets/drawable/pay_closed.png");
				label_result_msg.setText("交易关闭!");
				label_result_msg.setGraphic(new ImageView(img));
				
				label_tip.setText("请在5min内完成付款!");
			}else if(alipayTradeQueryResponseBean.getCode() == 3 || wxpayOrderQueryResponseBean.getCode() == 3) {
				Image img = new Image("file:assets/drawable/pay_ok.png");
				label_result_msg.setText("交易成功!");
				label_result_msg.setGraphic(new ImageView(img));
				
				label_tip.setText("感谢您使用自助收银系统!欢迎再次光临!");
			}else if(alipayTradeQueryResponseBean.getCode() == -1  || wxpayOrderQueryResponseBean.getCode() == -1) {
				Image img = new Image("file:assets/drawable/pay_fail.png");
				label_result_msg.setText("出错!");
				label_result_msg.setGraphic(new ImageView(img));
				
				label_tip.setText(alipayTradeQueryResponseBean.getStatus() + " 请联系管理员!");
			}
		}
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getBounds();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		checkin_pay_result_root.setPrefSize(width, height);
		
		btn_return_welcome.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				if(showPageListener!=null) {//返回欢迎页
					showPageListener.showPage(Page.PAGE_WELCOME);
				}
			}; 
		});
	}

}
