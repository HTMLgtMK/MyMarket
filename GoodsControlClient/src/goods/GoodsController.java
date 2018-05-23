package goods;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
public class GoodsController implements Initializable{
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
	
	/*控制器的舞台*/
	private Stage mStage;
	/*父类容器*/
	private Parent container;
	/*中间部分面板改变监听器*/
	private MyPaneWidthChangedListener panecenterWidthChangedListener;
	private MyPaneHeightChangedListener panecenterHeightChangedListener;
	
	public GoodsController() {
		// !important 不能使用这种构造方法
	}
	
	/**
	 * 获取GoodsControl的唯一方法
	 * @return
	 */
	public static GoodsController getInstance() {
		URL location = GoodsController.class.getResource("goods_main.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GoodsController instance = loader.getController();
		instance.container = parent;
		return instance;
	}
	
	
	/**
	 * 开始显示商品管理主界面
	 */
	public void start() {
		mStage = new Stage();
		Scene scene = new Scene(container, 1000, 800);
		mStage.setScene(scene);
		Image logo16 = new Image("file:resource/drawable/logo16.png");
		Image logo32 = new Image("file:resource/drawable/logo32.png");
		mStage.getIcons().addAll(logo16, logo32);
		mStage.setTitle("无人超市商品管理");
		mStage.show();
		Logger.getLogger(GoodsController.class.getSimpleName()).log(Level.INFO	,"in start");
		
		showWelcome();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Logger.getLogger(GoodsController.class.getSimpleName()).log(Level.INFO	, "in initialize");
		
		panecenterWidthChangedListener = new MyPaneWidthChangedListener();
		panecenterHeightChangedListener = new MyPaneHeightChangedListener();
		pane_center.widthProperty().addListener(panecenterWidthChangedListener);
		pane_center.heightProperty().addListener(panecenterHeightChangedListener);
	}
	
	@SuppressWarnings("unchecked")
	@FXML
	private void onTreeViewClicked(MouseEvent event){
		Logger.getLogger(GoodsController.class.getSimpleName()).log(Level.INFO	, "treeView Clicked: " + event.toString());
		Node node = event.getPickResult().getIntersectedNode();//获取点击的节点
		if(node instanceof Text || (node instanceof TreeCell && ((TreeCell<String>) node).getText() != null)) {
			String value = treeView.getSelectionModel().getSelectedItem().getValue();
			Logger.getLogger(GoodsController.class.getSimpleName()).log(Level.INFO	, "treeView Clicked: " + value);
			{//despatch event
				if("商品列表".equals(value)) {
					showGoodsIndex();
				}else if("添加商品".equals(value)) {
					showGoodsAdd();
				}else if("商品类列表".equals(value)) {
					showGoodsTypeIndex();
				}else if("添加商品类".equals(value)) {
					showGoodsTypeAdd();
				}else if("欢迎".equals(value)){
					showWelcome();
				}
			};
		}
	}
	
	/**
	 * 显示添加商品类界面
	 */
	private void showGoodsTypeAdd() {
		GoodsTypeAddController goodsTypeAddController = GoodsTypeAddController.getInstance();
		Parent parent = goodsTypeAddController.getRoot();
		pane_center.getChildren().clear();
		pane_center.getChildren().add(parent);
		panecenterWidthChangedListener.setParent(parent);
		panecenterHeightChangedListener.setParent(parent);
		goodsTypeAddController.start();
	}
	
	/**
	 * 显示商品类列表
	 */
	private void showGoodsTypeIndex() {
		GoodsTypeIndexController controller = GoodsTypeIndexController.getInstance();
		Parent parent = controller.getRoot();
		pane_center.getChildren().clear();
		pane_center.getChildren().add(parent);
		panecenterWidthChangedListener.setParent(parent);
		panecenterHeightChangedListener.setParent(parent);
		controller.start();
	}
	
	/**
	 * 显示欢迎界面
	 */
	private void showWelcome() {
		GoodsWelcomeController goodsWelcomeController = GoodsWelcomeController.getInstance();
		Parent parent = goodsWelcomeController.getRoot();
		pane_center.getChildren().clear();
		pane_center.getChildren().add(parent);
		panecenterWidthChangedListener.setParent(parent);
		panecenterHeightChangedListener.setParent(parent);
		goodsWelcomeController.start();
	}
	
	/**
	 * 显示商品列表
	 */
	private void showGoodsIndex() {
		GoodsIndexController controller = GoodsIndexController.getInstance();
		Parent parent = controller.getRoot();
		pane_center.getChildren().clear();
		pane_center.getChildren().add(parent);
		panecenterWidthChangedListener.setParent(parent);
		panecenterHeightChangedListener.setParent(parent);
		controller.start();
	}
	
	/**
	 * 添加商品列表界面
	 */
	private void showGoodsAdd() {
		GoodsAddController controller = GoodsAddController.getInstance();
		Parent parent = controller.getRoot();
		pane_center.getChildren().clear();//清除原有控件
		pane_center.getChildren().add(parent);//添加到面板上
		panecenterWidthChangedListener.setParent(parent);
		panecenterHeightChangedListener.setParent(parent);
		controller.start();
	}
	
	private class MyPaneWidthChangedListener implements ChangeListener<Number> {
		
		private Pane pane; // 继承关系 pane <-- Region <-- Parent <-- Node
		
		public void setParent(Parent parent) {
			if(parent instanceof Pane) {
				this.pane = (Pane) parent;
				Bounds bounds = pane_center.getBoundsInLocal();
				pane.setPrefWidth(bounds.getWidth());
			}else {
				pane = null;
			}
		}
		
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(pane!=null) {
				pane.setPrefWidth(newValue.doubleValue());
			}
		}
	}
	
	private class MyPaneHeightChangedListener implements ChangeListener<Number> {
		
		private Pane pane; // pane <-- Region <-- Parent <-- Node
		
		public void setParent(Parent parent) {
			if(parent instanceof Pane) {
				this.pane = (Pane) parent;
				Bounds bounds = pane_center.getBoundsInLocal();
				pane.setPrefHeight(bounds.getHeight());
			}else {
				pane = null;
			}
		}
		
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(pane!=null) {
				pane.setPrefHeight(newValue.doubleValue());
			}
		}
	}
	
}
