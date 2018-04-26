package checkin;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.CartBean;
import beans.DiscountBean;
import beans.GoodsBean;
import beans.InventoryBean;
import beans.Params;
import checkin.CheckInControl.OnGetDealListener;
import checkin.CheckInControl.OnShowPageListener;
import helper.NetworkHelper;
import helper.UHFHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CheckInCartControl implements Initializable,OnGetDealListener {
	@FXML
	private BorderPane checkin_cart_root;
	@FXML
	private Button btn_return;
	@FXML
	private Button btn_scan;//Invisible nodes never receive mouse events or keyboard focus and never maintain keyboard focus when they become invisible.
	@FXML
	private Label label_msg;
	@FXML
	private TableView<CartBean> table_cart;
	@FXML
	private TableColumn<CartBean, String> indexColumn;
	@FXML
	private TableColumn<CartBean, String> nameColumn;
	@FXML
	private TableColumn<CartBean, String> numsColumn;
	@FXML
	private TableColumn<CartBean, String> priceColumn;
	@FXML
	private TableColumn<CartBean, String> statusColumn;
	@FXML
	private Label label_total;
	@FXML
	private Label label_discount;
	@FXML
	private Label label_pay;
	@FXML
	private Button btn_pay;

	/**
	 *  定时器,间隔轮询标签
	 *  界面可见时创建新线程扫描，出错或退出自动收银撤销询查线程
	 *  扫描成功和其他界面不撤销线程，但是设置inventoryFlag = false
	 *  即表示询查代码不可达
	 */
	private Timer timer;
	// 是否科查询的标记
	private boolean inventoryFlag;
	// 轮询参数
	private InventoryBean inventoryBean;
	// 标签
	private ArrayList<String> epcList;
	private ArrayList<GoodsBean> goodsList;
	private ObservableList<CartBean> cart;
	private HashMap<Integer,ArrayList<DiscountBean>> discountMap;
	// 金额
	private int totalPrice;
	private int discountPrice;
	private int payPrice;
	//表格栏数
	private final int COLUMN_NUMS = 5;
	
	
	//显示界面接口回掉
	private OnShowPageListener showPageListener;

	public void setOnShowPageListener(OnShowPageListener listener) {
		this.showPageListener = listener;
	}
	
	/**
	 * 退出窗口时调用，强制停止计时器
	 */
	public void exitForceStopTimer() {
		if(timer!=null) {
			timer.cancel();
			timer = null;
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getBounds();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		checkin_cart_root.setPrefSize(width, height);
		//设置表格宽度
		table_cart.setPrefWidth(width);
		double column_width = width / COLUMN_NUMS;
		indexColumn.setPrefWidth(column_width);
		nameColumn.setPrefWidth(column_width);
		numsColumn.setPrefWidth(column_width);
		priceColumn.setPrefWidth(column_width);
		statusColumn.setPrefWidth(column_width);
		//初始化按钮
		Image img_return = new Image("file:assets/drawable/return.png");
		btn_return.setGraphic(new ImageView(img_return));
		//initial 
		inventoryBean = new InventoryBean();
		inventoryFlag = false;
		epcList = new ArrayList<>();
		goodsList = new ArrayList<>();
		cart = FXCollections.observableArrayList();
		discountMap = new HashMap<>();
		totalPrice = 0;
		
		btn_pay.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				startPay();
			};
		});
		btn_scan.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				start();//重新扫描
			}
		});
		btn_return.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				if(showPageListener!=null) {
					inventoryFlag = false;
					showPageListener.showPage(1);
				}
			}
		});
	}

	/**
	 * 当前可见，开始处理逻辑事务
	 */
	public void start() {
		if(timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				
				@Override
				public void run() {//创建新线程
					if(!inventoryFlag) return;//目前有别的工作正在进行
					inventory();
				}
			}, 0 , 100);//100ms轮询一次，读写器的返回时间是1s
		}
		inventoryFlag = true;//可询查
		label_msg.setText("开始扫描...");
		//btn_scan.setVisible(false);
		label_total.setText("0");
		label_discount.setText("0");
		label_pay.setText("0");
	}
	
	/**
	 * 开始处理支付逻辑
	 */
	private void startPay() {
		if(showPageListener!=null) {
			inventoryFlag = false;
			showPageListener.showPage(3);//显示支付界面
		}
	}
	
	/**
	 * 开始询查，运行在子线程
	 */
	private void inventory() {
		//初始化
		inventoryBean.setTotallen(0);//置0
		inventoryBean.setCardNum(0);//标签数置0
		epcList.clear();
		goodsList.clear();
		cart.clear();
		totalPrice = 0;
		discountPrice = 0;
		payPrice = 0;
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
			String spec = Params.URL_GETGOODSINFO;
			String json = NetworkHelper.downloadString(spec, map, "POST");
			JSONObject jsonObj = JSONObject.fromObject(json);
			final int code = jsonObj.getInt("code");
			//TODO delete msg
			String msg = jsonObj.getString("msg");
			Logger.getLogger(CheckInPayControl.class.getSimpleName()).log(Level.INFO, msg);
			if(code == 1) {
				goodsList.clear();
				discountMap.clear();
				JSONObject dataObj = jsonObj.getJSONObject("data");
				if(dataObj.containsKey("discount")) {//所有符合条件优惠
					JSONArray discounts = dataObj.getJSONArray("discount");
					int count = discounts.size();
					for(int i=0;i<count;++i) {
						JSONObject obj = discounts.getJSONObject(i);
						DiscountBean bean = new DiscountBean();
						bean.setId(obj.getInt("id"));
						bean.setDiscount_id(obj.getInt("discount_id"));
						bean.setGoods_type_id(obj.getInt("goods_type_id"));
						bean.setName(obj.getString("name"));
						bean.setExtent((float)obj.getDouble("extent"));
						bean.setCoin(obj.getInt("coin"));
						bean.setRest(obj.getInt("rest"));
						
						if(discountMap.containsKey(bean.getGoods_type_id())) {
							ArrayList<DiscountBean> list = discountMap.get(bean.getGoods_type_id());
							list.add(bean);
							discountMap.replace(bean.getGoods_type_id(), list);
						}else {
							ArrayList<DiscountBean> list = new ArrayList<>();
							list.add(bean);
							discountMap.put(bean.getGoods_type_id(), list);
						}
						
						//给优惠排序
						Iterator<Integer> it = discountMap.keySet().iterator();
						while(it.hasNext()) {
							int type_id = it.next();
							ArrayList<DiscountBean> discountList = discountMap.get(type_id);
							Collections.sort(discountList);
							discountMap.replace(type_id, discountList);
						}
					}
				}
				if(dataObj.containsKey("goods")) {//标签商品具体详情
					JSONArray goods = dataObj.getJSONArray("goods");
					int count = goods.size();
					for(int i=0;i<count;++i) {
						JSONObject obj = goods.getJSONObject(i);
						GoodsBean bean = new GoodsBean();
						bean.setAddress(obj.optString("address"));
						bean.setBatch_number(obj.optString("batch_number"));
						bean.setCompany(obj.optString("company"));
						bean.setGoods_id(obj.getString("id"));
						bean.setImages(obj.optString("images"));
						bean.setManufacture_date(obj.optLong("manufacture_date"));
						bean.setName(obj.getString("name"));
						bean.setPrice(obj.getInt("price"));
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
						table_cart.setItems(cart);
					}
					//inventoryFlag = true;//手动开启询查，或者等支付完成后开启询查
					label_msg.setText("扫描成功,已停止自动扫描!");
					label_total.setText(String.format("%.2f", Float.valueOf(totalPrice)/100));
					label_pay.setText(String.format("%.2f", Float.valueOf(payPrice)/100));
					label_discount.setText(String.format("%.2f", Float.valueOf(discountPrice)/100));
				}
			});
		}else if(ret == 0xFB){
			//NONE 
			inventoryFlag = true;//无标签查询,继续扫描
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					label_msg.setText("扫描结果: "+ String.format("0x%02x", ret) +" time:" +System.currentTimeMillis() );
				}
			});
		}else {
			//出错
			Logger.getLogger(CheckInPayControl.class.getSimpleName()).log(Level.INFO, "出错:"+String.format("0x%x", ret));
			timer.cancel();//出错取消询查
			timer = null;//设置为null, 否则无法再进入
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					label_msg.setText( "出错:"+String.format("0x%x", ret));
				}
			});
			
		}
	}
	
	/**
	 * 将商品列表中数据填充到购物车中
	 */
	private void generateCart() {
		int index = 0;
		HashMap<Integer,CartBean> types = new HashMap<>();
		Iterator<GoodsBean> it_goods = goodsList.iterator();
		while(it_goods.hasNext()) {//统计
			GoodsBean bean = it_goods.next();
			if(bean.getStatus() == 1) {//待售
				totalPrice += bean.getPrice();
				//统计优惠
				if(discountMap.containsKey(bean.getType_id())) {//这类商品的优惠
					ArrayList<DiscountBean> discountList = discountMap.get(bean.getType_id());
					Iterator<DiscountBean> it_discount = discountList.iterator();
					while(it_discount.hasNext()) {
						DiscountBean discountBean = it_discount.next();
						if(discountBean.inc()) {//有优惠可用，计算优惠金额
							discountPrice += discountBean.calculateDiscount(bean.getPrice(), 1);
							bean.setDiscount(discountBean);
							break;
						}else {//无优惠可用
							continue;
						}
					}
				}//该商品待售状态下统计可使用优惠
			}
			//统计商品种类
			if(types.containsKey(bean.getType_id())) {
				CartBean cartBean = types.get(bean.getType_id());
				cartBean.numsInc();
				types.replace(bean.getType_id(), cartBean);
			}else {
				CartBean cartBean = new CartBean();
				cartBean.setIndex(index++);
				cartBean.setName(bean.getName());
				cartBean.setNums(1);
				cartBean.setPrice((float)bean.getPrice()/100);
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
			}// load into cart
		}//while has next
		
		// 装载到表格
		Iterator<Integer> it = types.keySet().iterator();
		while(it.hasNext()) {
			int key = it.next();// goods_type_id
			CartBean bean = types.get(key);
			cart.add(bean);
		}
		payPrice = totalPrice-discountPrice;
		types.clear();
		types = null;
	}

	@Override
	public ArrayList<GoodsBean> getGoodsList() {
		return goodsList;
	}

	@Override
	public int getTotalPrice() {
		return totalPrice;
	}

	@Override
	public int getDiscountPrice() {
		return discountPrice;
	}

	@Override
	public int getPayPrice() {
		return payPrice;
	}

	@Override
	public ArrayList<DiscountBean> getDiscountList() {
		ArrayList<DiscountBean> discountList = new ArrayList<>();
		Iterator<Integer> it = discountMap.keySet().iterator();
		while(it.hasNext()) {
			int key = it.next();
			ArrayList<DiscountBean> list = discountMap.get(key);
			discountList.addAll(list);
		}
		return discountList;
	}

}
