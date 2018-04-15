package checkin;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.GoodsBean;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CheckInControl implements Initializable {
	@FXML
	private ScrollPane scrollPane_checkin_main_root;
	/**
	 * 注: Controller变量的命名必须是<fx:id>Controller格式
	 * 否则无法正常工作！
	 */
	@FXML
	private CheckInWelcomeControl page1Controller;
	@FXML
	private CheckInCartControl page2Controller;
	@FXML
	private CheckInPayControl2 page3Controller;
	@FXML
	private CheckInPayResultControl page4Controller;
	
	private final int PAGECOUNT = 4;//页面总数
	
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
		stage.show();
		
		stage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.getCode().equals(KeyCode.ESCAPE)) {
					stage.close();
				}
			}
		});
	}
	
	/**
	 * the initialize method is called after all @FXML annotated members have been injected.
	 * Called to initialize a controller after its root element has been completely processed.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ShowPageListener listener = new ShowPageListener();
		page1Controller.setOnShowPageListener(listener);
		page2Controller.setOnShowPageListener(listener);
		page3Controller.setOnShowPageListener(listener);
		page4Controller.setOnShowPageListener(listener);
		
		MyGetDealListener getDealListener = new MyGetDealListener();
		page3Controller.setOnGetDealListener(getDealListener);//也可以直接设置为page2Controller
	}
	
	private class ShowPageListener implements OnShowPageListener{

		@Override
		public void showPage(int index) {
			// TODO Auto-generated method stub
			if(index < 1 || index>PAGECOUNT) return;
			double x = Double.valueOf(index-1) / (PAGECOUNT-1);
			scrollPane_checkin_main_root.setHvalue(x);
			switch(index){
			case 1:{
				break;
			}
			case 2:{//购物车界面
				page2Controller.start();//开始处理询查逻辑
				break;
			}
			case 3:{//支付界面
				page3Controller.start();//开始处理支付逻辑
				break;
			}
			case 4:{
				break;
			}
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
		 * @param index 第几个界面，下标从1开始
		 */
		public void showPage(int index);
	}
	
	
	/**
	 * 获取扫描到的商品交易信息 代理类
	 * @author GT
	 *
	 */
	private class MyGetDealListener implements OnGetDealListener{

		@Override
		public ArrayList<GoodsBean> getGoodsList() {
			// TODO Auto-generated method stub
			return page2Controller.getGoodsList();
		}

		@Override
		public double getTotalPrice() {
			// TODO Auto-generated method stub
			return page2Controller.getTotalPrice();
		}

		@Override
		public double getDiscountPrice() {
			// TODO Auto-generated method stub
			return page2Controller.getDiscountPrice();
		}

		@Override
		public double getPayPrice() {
			// TODO Auto-generated method stub
			return page2Controller.getPayPrice();
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
		public double getTotalPrice();
		/**
		 * 获取折扣总额
		 * @return
		 */
		public double getDiscountPrice();
		/**
		 * 获取支付总额
		 * @return
		 */
		public double getPayPrice();
		
		//TODO 后续添加商品折扣信息表
	}
}
