package checkin;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.AlipayTradeQueryResponseBean;
import beans.DiscountBean;
import beans.GoodsBean;
import beans.WxpayOrderQueryResponseBean;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class CheckInControl implements Initializable {
	@FXML
	private ScrollPane scrollPane_checkin_main_root;
	/**
	 * 注: Controller变量的命名必须是<fx:id>Controller格式
	 * 否则无法正常工作！
	 */
	@FXML
	private CheckInWelcomeControl welcomeController;
	@FXML
	private CheckInUserLoginControl userLoginController;
	@FXML
	private CheckInCartControl cartController;
	@FXML
	private CheckInPayControl payController;
	@FXML
	private CheckInPayResultControl payResultController;
	/*各个页面页码*/
	public static enum Page{
		SELF,
		PAGE_WELCOME,
		PAGE_USERLOGIN,
		PAGE_CART,
		PAGE_PAY,
		PAGE_PAY_RESULT
	}
	
	private final int PAGECOUNT = 5;//页面总数
	
	public void start() {
		Stage stage = new Stage(StageStyle.UNDECORATED);
		Parent parent = null;
		try {
			URL location = getClass().getResource("checkin_main.fxml");
			parent = FXMLLoader.load(location);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene scene = new Scene(parent);
		stage.setScene(scene);
		stage.setMaximized(true);//全屏
		Image logo16 = new Image("file:assets/drawable/logo16.png");
		Image logo32 = new Image("file:assets/drawable/logo32.png");
		stage.getIcons().addAll(logo16, logo32);
		stage.show();
		
		stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode().equals(KeyCode.ESCAPE)) {
					stage.hide();
				}else {
					event.consume();//blocks all others key bingdings
				}
			}
		});
		
		stage.addEventHandler(WindowEvent.WINDOW_HIDING, new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if(cartController!=null) {
					cartController.exitForceStopTimer();
				}
				// TODO force stop timer!
				/*
				if(cartController!=null) {
					cartController.exitForceStopTimer();
				}
				*/
				Logger.getLogger(CheckInControl.class.getSimpleName()).log(Level.INFO, "Force stop timers !");
			};
		});
	}
	
	/**
	 * the initialize method is called after all @FXML annotated members have been injected.
	 * Called to initialize a controller after its root element has been completely processed.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ShowPageListener listener = new ShowPageListener();
		welcomeController.setOnShowPageListener(listener);
		userLoginController.setOnShowPageListener(listener);
		cartController.setOnShowPageListener(listener);//这段代码是当childController 执行完成后执行的，ChildController里面设置的Listener将为空
		payController.setOnShowPageListener(listener);
		payResultController.setOnShowPageListener(listener);
		
		MyGetDealListener getDealListener = new MyGetDealListener();
		payController.setOnGetDealListener(getDealListener);//也可以直接设置为cartController
		
		MyGetTradeResponseListener getTradeResponseListener = new MyGetTradeResponseListener();
		payResultController.setOnGetTradeQueryResponseListener(getTradeResponseListener);
	}
	
	private class ShowPageListener implements OnShowPageListener{

		@Override
		public void showPage(Page page) {
			int index = page.ordinal();
			if(index < 1 || index>PAGECOUNT) return;
			double x = Double.valueOf(index-1) / (PAGECOUNT-1);
			scrollPane_checkin_main_root.setHvalue(x);
			if(page.equals(Page.PAGE_WELCOME)) {
			}else if(page.equals(Page.PAGE_USERLOGIN)) {// 会员登陆界面
				userLoginController.start();
			}else if(page.equals(Page.PAGE_CART)) {//购物车界面
				cartController.start();//开始处理询查逻辑
			}else if(page.equals(Page.PAGE_PAY)) {
				payController.start();//开始处理支付逻辑
			}else if(page.equals(Page.PAGE_PAY_RESULT)) {
				payResultController.start();//开始处理显示逻辑
			}
		}
	}
	
	/**
	 * 显示界面回调
	 * @author GT
	 *
	 */
	public interface OnShowPageListener{
		/**
		 * 
		 * @param page 枚举的页面
		 */
		public void showPage(Page page);
	}
	
	
	/**
	 * 获取扫描到的商品交易信息 代理类
	 * @author GT
	 *
	 */
	private class MyGetDealListener implements OnGetDealListener{

		@Override
		public ArrayList<GoodsBean> getGoodsList() {
			return cartController.getGoodsList();
		}

		@Override
		public int getTotalPrice() {
			return cartController.getTotalPrice();
		}

		@Override
		public int getDiscountPrice() {
			return cartController.getDiscountPrice();
		}

		@Override
		public int getPayPrice() {
			return cartController.getPayPrice();
		}

		@Override
		public ArrayList<DiscountBean> getDiscountList() {
			return cartController.getDiscountList();
		}
	}
	
	/**
	 * 获取当前扫描到的商品接口回调
	 * @author GT
	 *
	 */
	public interface OnGetDealListener{
		/**
		 * 获取当前扫描的商品列表(包括已付费的)
		 * @return
		 */
		public ArrayList<GoodsBean> getGoodsList();
		/**
		 * 获取商品总额
		 * @return
		 */
		public int getTotalPrice();
		/**
		 * 获取折扣总额
		 * @return
		 */
		public int getDiscountPrice();
		/**
		 * 获取支付总额
		 * @return
		 */
		public int getPayPrice();
		/**
		 * 商品优惠使用情况
		 * @return
		 */
		public ArrayList<DiscountBean> getDiscountList();
	}
	
	/**
	 * 获取支付宝交易查询信息代理类
	 * @author GT
	 *
	 */
	private class MyGetTradeResponseListener implements OnGetTradeQueryResponseListener{

		@Override
		public AlipayTradeQueryResponseBean geAlipayTradeQueryResponseBean() {
			return payController.geAlipayTradeQueryResponseBean();
		}

		@Override
		public WxpayOrderQueryResponseBean getWxpayOrderQueryResponseBean() {
			return payController.getWxpayOrderQueryResponseBean();
		}
	}
	
	/**
	 * 获取支付交易查询结果回调接口
	 * @author GT
	 *
	 */
	public interface OnGetTradeQueryResponseListener{
		/**
		 * 获取支付宝交易查询结果
		 * @return
		 */
		public AlipayTradeQueryResponseBean geAlipayTradeQueryResponseBean();
		/**
		 * 获取微信支付交易查询结果
		 * @return
		 */
		public WxpayOrderQueryResponseBean getWxpayOrderQueryResponseBean();
	}
}
