package goods;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import beans.AdminstratorInfo;
import beans.UHFReaderBean;
import helper.UHFHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * 商品管理模块控制
 * @author GT
 *
 */
public class GoodsControl implements Initializable{
	@FXML
	private Menu menu_control;
	@FXML
	private Menu menu_setting;
	@FXML
	private Menu menu_help;
	@FXML
	private TreeView<String> treeView;
	@FXML
	private Pane pane_center;
	@FXML
	private Pane pane_bottom;
	@FXML
	private Label label_uhf_reader_status;
	@FXML
	private Button btn_uhf_togleconn;
	
	/*控制器的舞台*/
	private Stage mStage;
	/*父类容器*/
	private Parent container;
	/*中间部分面板改变监听器*/
	private MyPaneWidthChangedListener panecenterWidthChangedListener;
	private MyPaneHeightChangedListener panecenterHeightChangedListener;
	
	public GoodsControl() {
		// !important 不能使用这种构造方法
	}
	
	/**
	 * 获取GoodsControl的唯一方法
	 * @return
	 */
	public static GoodsControl getInstance() {
		URL location = GoodsControl.class.getResource("goods_main.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GoodsControl instance = loader.getController();
		instance.container = parent;
		return instance;
	}
	
	
	/**
	 * 开始显示商品管理主界面
	 */
	public void start() {
		mStage = new Stage();
//		Pane pane = null;
//		try {
//			pane = FXMLLoader.load(getClass().getResource("goods_management.fxml"));
//		} catch (IOException e) {
//			e.printStackTrace();
//			Platform.exit();//退出程序
//		}
		Scene scene = new Scene(container, 1000, 800);
		mStage.setScene(scene);
		Image logo16 = new Image("file:resource/drawable/logo16.png");
		Image logo32 = new Image("file:resource/drawable/logo32.png");
		mStage.getIcons().addAll(logo16, logo32);
		mStage.show();
		Logger.getLogger(GoodsControl.class.getSimpleName()).log(Level.INFO	,"in start");
		
		showWelcome();
		initializeStatusBar();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Logger.getLogger(GoodsControl.class.getSimpleName()).log(Level.INFO	, "in initialize");
		
		panecenterWidthChangedListener = new MyPaneWidthChangedListener();
		panecenterHeightChangedListener = new MyPaneHeightChangedListener();
		pane_center.widthProperty().addListener(panecenterWidthChangedListener);
		pane_center.heightProperty().addListener(panecenterHeightChangedListener);
	}
	
	private void initializeStatusBar() {
		/**
		 * 初始化DLL库
		 */
		Main.setUhfConnected(false);
		int ret = UHFHelper.init();
		Logger.getLogger(GoodsControl.class.getSimpleName()).log(Level.INFO	,"init DLL:"+ String.format("0x%02x", ret));
		if(ret == 0) {
			label_uhf_reader_status.setText("已连接");
			btn_uhf_togleconn.setText("断开");
			Main.setUhfConnected(true);
			UHFReaderBean uhfBean = new UHFReaderBean();
			ret = UHFHelper.getUHFReaderInfo(uhfBean);
			if(ret == 0) {
				Main.setUhfBean(uhfBean);
			}
		}else {
			label_uhf_reader_status.setText("连接失败 : "+String.format(" %s (0x%02x)", UHFHelper.CODE_MSG_MAP.get(ret) ,ret));
		}
		btn_uhf_togleconn.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				if(Main.isUhfConnected()) {//已连接,关闭连接
					int ret = UHFHelper.closeComPort();
					if(ret == 0) {
						Main.setUhfConnected(false);
						btn_uhf_togleconn.setText("连接");
						label_uhf_reader_status.setText("尚未连接");
					}
				}else {
					int ret = UHFHelper.autoOpenComPort();
					if(ret == 0) {
						label_uhf_reader_status.setText("已连接");
						Main.setUhfConnected(true);
						btn_uhf_togleconn.setText("断开");
					}else {
						label_uhf_reader_status.setText("连接失败 : "+ String.format(" %s (0x%02x)", UHFHelper.CODE_MSG_MAP.get(ret) ,ret));
					}
				}
			}
			
		});
		
	}
	
	@SuppressWarnings("unchecked")
	@FXML
	private void onTreeViewClicked(MouseEvent event){
		Logger.getLogger(GoodsControl.class.getSimpleName()).log(Level.INFO	, "treeView Clicked: " + event.toString());
		Node node = event.getPickResult().getIntersectedNode();//获取点击的节点
		if(node instanceof Text || (node instanceof TreeCell && ((TreeCell<String>) node).getText() != null)) {
			String value = treeView.getSelectionModel().getSelectedItem().getValue();
			Logger.getLogger(GoodsControl.class.getSimpleName()).log(Level.INFO	, "treeView Clicked: " + value);
			{//despatch event
				if("商品列表".equals(value)) {
					showGoodsIndex();
				}else if("添加商品".equals(value)) {
					showGoodsAdd();
				}else if("自助收银".equals(value)){
					showCheckIn();
				}else if("商品类列表".equals(value)) {
					showGoodsTypeIndex();
				}else if("添加商品类".equals(value)) {
					showGoodsTypeAdd();
				}
			};
		}
	}
	
	/**
	 * 显示添加商品类界面
	 */
	private void showGoodsTypeAdd() {
		GoodsTypeAddBox box = new GoodsTypeAddBox();
		pane_center.getChildren().clear();
		pane_center.getChildren().add(box);
		panecenterWidthChangedListener.setPane(box);
		panecenterHeightChangedListener.setPane(box);
		box.start();
	}
	
	/**
	 * 显示商品类列表
	 */
	private void showGoodsTypeIndex() {
		GoodsTypeIndexBox box = new GoodsTypeIndexBox();
		pane_center.getChildren().clear();
		pane_center.getChildren().add(box);
		panecenterWidthChangedListener.setPane(box);
		panecenterHeightChangedListener.setPane(box);
		box.start();
	}
	
	/**
	 * 显示自助收银服务
	 */
	private void showCheckIn() {
		/*
		CheckInPayControl checkInPayControl = new CheckInPayControl();
		pane_center.getChildren().clear();//清除原有控件
		pane_center.getChildren().add(checkInPayControl);//添加到面板上
		checkInPayControl.start();
		*/
		/*新建界面，全屏显示*/
		// 两个不同实例
		//CheckInControl control = new CheckInControl();
		//control.start();
		// 相同实例
		//CheckInControl control = CheckInControl.getInstance();
		//control.start();
	}
	
	/**
	 * 显示欢迎界面
	 */
	private void showWelcome() {
		Parent parent = null;
		try {
			parent = FXMLLoader.load(getClass().getResource("adminstrator.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Platform.exit();//退出程序
		}
		Label lb_user_login = (Label) parent.lookup("#label_user_login");
		Label lb_name = (Label) parent.lookup("#label_name");
		Label lb_mobile = (Label) parent.lookup("#label_mobile");
		Label lb_user_status = (Label) parent.lookup("#label_user_status");
		Label lb_create_time = (Label) parent.lookup("#label_create_time");
		Label lb_post_name = (Label) parent.lookup("#label_post_name");
		
		AdminstratorInfo info = Main.getAdminInfo();
		if(info != null) {
			lb_user_login.setText(info.getUser_login());
			lb_name.setText(info.getName());
			lb_mobile.setText(info.getMobile());
			switch(info.getUser_status()) {
			case 0:{
				lb_user_status.setText("已离职");
				break;
			}
			case 1:{
				lb_user_status.setText("正常");
				break;
			}
			case 2:{
				lb_user_status.setText("未验证");
				break;
			}
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String c_date = format.format(new Date(info.getCreate_time() * 1000));
		lb_create_time.setText(c_date);
		lb_post_name.setText(info.getPost_name());
		
		pane_center.getChildren().clear();//清除原有控件
		pane_center.getChildren().add(parent);//添加到面板上
	}
	
	/**
	 * 显示商品列表
	 */
	private void showGoodsIndex() {
		GoodsIndexBox box = new GoodsIndexBox();
		pane_center.getChildren().clear();
		pane_center.getChildren().add(box);
		panecenterWidthChangedListener.setPane(box);
		panecenterHeightChangedListener.setPane(box);
		box.start();
	}
	
	/**
	 * 添加商品列表界面
	 */
	private void showGoodsAdd() {
		GoodsAddBox goodsAddBox = new GoodsAddBox();
		pane_center.getChildren().clear();//清除原有控件
		pane_center.getChildren().add(goodsAddBox);//添加到面板上
		goodsAddBox.start();
	}
	
	private class MyPaneWidthChangedListener implements ChangeListener<Number> {
		
		private Pane pane;
		
		public void setPane(Pane pane) {
			this.pane = pane;
			Bounds bounds = pane_center.getBoundsInLocal();
			pane.setPrefWidth(bounds.getWidth());
		}
		
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(pane!=null) {
				pane.setPrefWidth(newValue.doubleValue());
			}
		}
	}
	
	private class MyPaneHeightChangedListener implements ChangeListener<Number> {
		
		private Pane pane;
		
		public void setPane(Pane pane) {
			this.pane = pane;
			Bounds bounds = pane_center.getBoundsInLocal();
			pane.setPrefHeight(bounds.getHeight());
		}
		
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(pane!=null) {
				pane.setPrefHeight(newValue.doubleValue());
			}
		}
	}
	
}
