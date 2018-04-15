package checkin;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.CartBean;
import beans.GoodsBean;
import beans.InventoryBean;
import helper.NetworkHelper;
import helper.UHFHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CheckInPayControl implements Initializable {
	//定时器,间隔轮询标签
	private Timer timer;
	//是否科查询的标记
	private boolean inventoryFlag;
	//轮询参数
	private InventoryBean inventoryBean;
	//标签
	private ArrayList<String> epcList;
	private ArrayList<GoodsBean> goodsList;
	private ObservableList<CartBean> cart;
	//总额
	private float totalPrice;
	//表格控件
	private TableView<CartBean> tableView;
	//总额
	private Label label_total;
	//扫描按钮
	private Button btn_inventory;
	//支付按钮
	private Button btn_pay;
	
	@SuppressWarnings("unchecked")
	public CheckInPayControl() {
		// TODO Auto-generated constructor stub
		URL location = getClass().getResource("goods_checkin.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		loader.setRoot(this);
		loader.setController(this);
		Parent parent = null;
		try {
			parent = loader.load();
			//TODO 初始化控件
			tableView = (TableView<CartBean>) parent.lookup("#table_cart");
			label_total = (Label) parent.lookup("#label_goods_checkin_total");
			btn_inventory = (Button) parent.lookup("#btn_goods_check_inventory");
			btn_inventory.setOnMouseClicked(new EventHandler<Event>() {
				public void handle(Event event) {
					inventoryFlag = true;//只能把这个手动置为true,然后就会开始询查
					if(timer==null) {//可能之前出错了，要重新新建timer
						startInventory();
					}
				};
			});
			btn_pay = (Button) parent.lookup("#btn_goods_check_pay");
			btn_pay.setOnMouseClicked(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					startPay();
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inventoryBean = new InventoryBean();
		inventoryFlag = true;
		epcList = new ArrayList<>();
		goodsList = new ArrayList<>();
		cart = FXCollections.observableArrayList();
		totalPrice = 0L;
	}
	
	/**
	 * 开始处理业务逻辑
	 */
	public void start() {
		startInventory();
	}
	
	/**
	 * 开始处理支付逻辑
	 */
	private void startPay() {
		Stage dialog = new Stage(StageStyle.UNDECORATED);//无标题栏
		BorderPane borderPane = null;
		Button btn_cancel = null;
		try {
			borderPane = FXMLLoader.load(getClass().getResource("goods_pay.fxml"));
			btn_cancel = (Button) borderPane.lookup("#btn_cancel_pay");
			btn_cancel.setOnMouseClicked(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					dialog.hide();
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene scene = new Scene(borderPane);
		dialog.setScene(scene);
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.show();
	}
	
	/**
	 * 开始巡查逻辑
	 */
	private void startInventory() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {//新线程
				if(!inventoryFlag) return;//目前有别的工作正在进行
				inventory();
			}
		}, 0 , 2*1000);//2s轮询一次，读写器的返回时间是1s
	}
	
	private void inventory() {
		//初始化
		inventoryBean.setTotallen(0);//置0
		inventoryBean.setCardNum(0);//标签数置0
		epcList.clear();
		goodsList.clear();
		cart.clear();
		totalPrice = 0L;
		//询查
		int ret = UHFHelper.inventory_G2(inventoryBean);
		/**
		 * 0x01	询查时间结束前返回
		 * 0x02	询查时间结束使得询查退出
		 * 0x03	如果读到的标签数量无法在一条消息内传送完，将分多次发送。如果Status为0x0D，则表示这条数据结束后，还有数据。
		 * 0x04	还有电子标签未读取，电子标签数量太多，MCU存储不了
		 * 0xFB 无电子标签可操作
		 */
		if ((ret == 0x01) || (ret == 0x02) || (ret == 0x03) || (ret == 0x04)) {// || (ret == 0xFB)
			inventoryFlag = false;// 未处理完这个任务前不可再轮询
			int m=1;
			for (int CardIndex = 0; CardIndex < inventoryBean.getCardNum(); CardIndex++){
				int EPClen = inventoryBean.getEPClenandEPC()[m-1];
				StringBuilder builder = new StringBuilder();
				for(int i=0;i<EPClen;++i) {
					builder.append(String.format("%02X", Integer.valueOf(inventoryBean.getEPClenandEPC()[m+i])));
				}
				m = m + EPClen + 1;
				epcList.add(builder.toString());
				Logger.getLogger(CheckInPayControl.class.getSimpleName()).log(Level.INFO,builder.toString());
			}
			//TODO 查询数据库, 获取列表中商品的信息.更新列表
			HashMap<String,String> map = new HashMap<>();
			int size = epcList.size();
			for(int i=0;i<size;++i) {
				map.put(String.valueOf(i), epcList.get(i));
			}
			String spec = "http://localhost:8888/api/market/Goods/getGoodsInfo";
			String json = NetworkHelper.downloadString(spec, map, "POST");
			JSONObject jsonObj = JSONObject.fromObject(json);
			final int code = jsonObj.getInt("code");
			//TODO delete msg
			String msg = jsonObj.getString("msg");
			Logger.getLogger(CheckInPayControl.class.getSimpleName()).log(Level.INFO, msg);
			if(code == 1) {
				goodsList.clear();
				JSONObject dataObj = jsonObj.getJSONObject("data");
				if(dataObj.containsKey("goods")) {
					JSONArray goods = dataObj.getJSONArray("goods");
					int count = goods.size();
					for(int i=0;i<count;++i) {
						JSONObject obj = goods.getJSONObject(i);
						GoodsBean bean = new GoodsBean();
						bean.setAddress(obj.getString("address"));
						bean.setBatch_number(obj.getString("batch_number"));
						bean.setCompany(obj.getString("company"));
						bean.setGoods_id(obj.getString("id"));
						bean.setImages(obj.getString("images"));
						bean.setManufacture_date(obj.getLong("manufacture_date"));
						bean.setName(obj.getString("name"));
						bean.setPrice((float)obj.getDouble("price"));
						bean.setStatus(obj.getInt("status"));
						bean.setType_id(obj.getInt("type_id"));
						bean.setId(bean.getType_id());
						goodsList.add(bean);
					}
					generateCart();//goods list to cart list
				}
			}
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(code == 1) {
						tableView.setItems(cart);
					}
					//inventoryFlag = true;//手动开启询查，或者等支付完成后开启询查
				}
			});
		}else if(ret == 0xFB){
			//NONE 
			inventoryFlag = true;//无标签查询
		}else {
			//出错
			Logger.getLogger(CheckInPayControl.class.getSimpleName()).log(Level.INFO, "出错:"+String.format("0x%x", ret));
			timer.cancel();//出错取消询查
		}
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				label_total.setText(String.valueOf(totalPrice));
			}
		});
	}
	
	/**
	 * 将商品列表中数据填充到购物车中
	 */
	private void generateCart() {
		int size = goodsList.size();
		int index = 0;
		HashMap<Integer,CartBean> types = new HashMap<>();
		for(int i=0;i<size;++i) {//统计
			GoodsBean bean = goodsList.get(i);
			totalPrice += bean.getPrice();
			if(types.containsKey(bean.getType_id())) {
				CartBean cartBean = types.get(bean.getType_id());
				cartBean.numsInc();
				types.replace(bean.getType_id(), cartBean);
			}else {
				CartBean cartBean = new CartBean();
				cartBean.setIndex(index++);
				cartBean.setName(bean.getName());
				cartBean.setNums(1);
				cartBean.setPrice(bean.getPrice());
				switch(bean.getStatus()) {
				case 1:{
					cartBean.setStatus("待售");
					break;
				}
				case 2:{
					cartBean.setStatus("已售");
					break;
				}
				default:{
					cartBean.setStatus("未知");
				}
				}
				types.put(bean.getType_id(), cartBean);
			}
		}//for
		Iterator<Integer> it = types.keySet().iterator();
		while(it.hasNext()) {
			int key = it.next();
			CartBean bean = types.get(key);
			cart.add(bean);
		}
		types.clear();
		types = null;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
}
