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
import beans.UserBean;
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
	 * 注: Controller变量的命名必须是<fx:id>Controller格式 否则无法正常工作！
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
	
	private Parent parent;
	
	public CheckInControl() {
		// 非系统调用不能使用 !important
	}
	
	/**
	 * 必须通过这个方法获取控制器实例, 内部的使用方法才是同一个实例
	 * @return
	 */
	public static CheckInControl getInstance() {
		FXMLLoader loader = new FXMLLoader(CheckInControl.class.getResource("checkin_main.fxml"));
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		CheckInControl instance = loader.getController();
		instance.parent = parent;
		return instance;
	}

	/* 各个页面页码 */
	public static enum Page {
		SELF, PAGE_WELCOME, PAGE_USERLOGIN, PAGE_CART, PAGE_PAY, PAGE_PAY_RESULT
	}

	private final int PAGECOUNT = 5;// 页面总数

	private Stage stage;

	public void start() {
		stage = new Stage(StageStyle.UNDECORATED);
//		try {
//			URL location = getClass().getResource("checkin_main.fxml");
//			parent = FXMLLoader.load(location);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		Scene scene = new Scene(parent);
		stage.setScene(scene);
		stage.setMaximized(true);// 全屏
		Image logo16 = new Image("file:resource/drawable/logo16.png");
		Image logo32 = new Image("file:resource/drawable/logo32.png");
		stage.getIcons().addAll(logo16, logo32);
		stage.show();
		stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ESCAPE)) {
					stage.hide();
				} else {
					event.consume();// blocks all others key bingdings
				}
			}
		});
		stage.addEventHandler(WindowEvent.WINDOW_HIDING, new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				// TODO force stop timer!
				if (CheckInControl.this.cartController != null) {
					CheckInControl.this.cartController.exitForceStopTimer();
					Logger.getLogger(CheckInControl.class.getSimpleName()).log(Level.INFO, "Force stop timers !");
				}
				if (CheckInControl.this.welcomeController != null) {
					CheckInControl.this.welcomeController.releaseMedia();
					Logger.getLogger(CheckInControl.class.getSimpleName()).log(Level.INFO, "release media resource !");
				}
				if(userLoginController != null) {
					userLoginController.exitForceStopTimer();
				}
			};
		});
		showPage(Page.PAGE_WELCOME);
	}

	/**
	 * the initialize method is called after all @FXML annotated members have been
	 * injected. Called to initialize a controller after its root element has been
	 * completely processed.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {// implement Initializable interface
		ShowPageListener listener = new ShowPageListener();
		welcomeController.setOnShowPageListener(listener);
		userLoginController.setOnShowPageListener(listener);
		cartController.setOnShowPageListener(listener);// 这段代码是当childController 执行完成后执行的，ChildController里面设置的Listener将为空
		payController.setOnShowPageListener(listener);
		payResultController.setOnShowPageListener(listener);

		MyGetDealListener getDealListener = new MyGetDealListener();
		payController.setOnGetDealListener(getDealListener);// 也可以直接设置为cartController

		MyGetTradeResponseListener getTradeResponseListener = new MyGetTradeResponseListener();
		payResultController.setOnGetTradeQueryResponseListener(getTradeResponseListener);

		MyGetUserListener getUserListener = new MyGetUserListener();
		cartController.setOnGetUserListener(getUserListener);
		payController.setOnGetUserListener(getUserListener);
	}

	/* 显示界面 */
	private void showPage(Page page) {
		int index = page.ordinal();
		if (index < 1 || index > PAGECOUNT)
			return;
		double x = Double.valueOf(index - 1) / (PAGECOUNT - 1);
		scrollPane_checkin_main_root.setHvalue(x);
		if (page.equals(Page.PAGE_WELCOME)) {
			// 清空上个用户的痕迹
			userLoginController.clearUser();
			cartController.clearGoods();
			welcomeController.start();
		} else if (page.equals(Page.PAGE_USERLOGIN)) {// 会员登陆界面
			userLoginController.start();
		} else if (page.equals(Page.PAGE_CART)) {// 购物车界面
			cartController.start();// 开始处理询查逻辑
		} else if (page.equals(Page.PAGE_PAY)) {
			payController.start();// 开始处理支付逻辑
		} else if (page.equals(Page.PAGE_PAY_RESULT)) {
			payResultController.start();// 开始处理显示逻辑
		}
	}

	private class ShowPageListener implements OnShowPageListener {

		@Override
		public void showPage(Page page) {
			CheckInControl.this.showPage(page);// 否则出现 StackOverflowError 错误
		}
	}

	/**
	 * 显示界面回调
	 * 
	 * @author GT
	 *
	 */
	public interface OnShowPageListener {
		/**
		 * 
		 * @param page
		 *            枚举的页面
		 */
		public void showPage(Page page);
	}

	/**
	 * 获取扫描到的商品交易信息 代理类
	 * 
	 * @author GT
	 *
	 */
	private class MyGetDealListener implements OnGetDealListener {

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
	 * 
	 * @author GT
	 *
	 */
	public interface OnGetDealListener {
		/**
		 * 获取当前扫描的商品列表(包括已付费的)
		 * 
		 * @return
		 */
		public ArrayList<GoodsBean> getGoodsList();

		/**
		 * 获取商品总额
		 * 
		 * @return
		 */
		public int getTotalPrice();

		/**
		 * 获取折扣总额
		 * 
		 * @return
		 */
		public int getDiscountPrice();

		/**
		 * 获取支付总额
		 * 
		 * @return
		 */
		public int getPayPrice();

		/**
		 * 商品优惠使用情况
		 * 
		 * @return
		 */
		public ArrayList<DiscountBean> getDiscountList();
	}

	/**
	 * 获取支付宝交易查询信息代理类
	 * 
	 * @author GT
	 *
	 */
	private class MyGetTradeResponseListener implements OnGetTradeQueryResponseListener {

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
	 * 
	 * @author GT
	 *
	 */
	public interface OnGetTradeQueryResponseListener {
		/**
		 * 获取支付宝交易查询结果
		 * 
		 * @return
		 */
		public AlipayTradeQueryResponseBean geAlipayTradeQueryResponseBean();

		/**
		 * 获取微信支付交易查询结果
		 * 
		 * @return
		 */
		public WxpayOrderQueryResponseBean getWxpayOrderQueryResponseBean();
	}

	/**
	 * 获取授权用户的中间代理类
	 * 
	 * @author GT
	 *
	 */
	public class MyGetUserListener implements OnGetUserListener {
		@Override
		public UserBean getUser() {
			return userLoginController.getUser();
		}
	}

	/**
	 * 获取授权用户的回调接口
	 * 
	 * @author GT
	 *
	 */
	public interface OnGetUserListener {
		public UserBean getUser();
	}
}
