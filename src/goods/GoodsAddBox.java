package goods;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import beans.GoodsTypeBean;
import helper.NetworkHelper;
import helper.UHFHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GoodsAddBox extends BorderPane {
	
	private ChoiceBox<String> choiceBox;
	private DatePicker datePicker;
	private TextField tf_batch_number;
	private TextField tf_goods_id;
	private ScrollPane scrollPane_info;
	private VBox vbox_info;
	
	private Button btn_getGoodsId;
	private Button btn_writeIn;
	
	private Button btn_submit;
	
	private HashMap<String,Integer> goodsTypeMap;
	
	@SuppressWarnings("unchecked")
	public GoodsAddBox() {
		URL location = getClass().getResource("goods_add.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		loader.setRoot(this);
		loader.setController(this);
		try {
			Parent parent = loader.load();
			//初始化控件
			choiceBox = (ChoiceBox<String>) parent.lookup("#chbox_goodstype");
			datePicker = (DatePicker) parent.lookup("#datepicker_goodsdate");
			tf_batch_number =  (TextField) parent.lookup("#tf_goodsbatchnumber");
			tf_goods_id = (TextField) parent.lookup("#tf_goodsid");
			scrollPane_info = (ScrollPane) parent.lookup("#scrollpane_info");
			
			btn_getGoodsId = (Button) parent.lookup("#btn_getgoodsId");
			btn_writeIn = (Button) parent.lookup("#btn_writein");
			btn_submit = (Button) parent.lookup("#btn_submit");
			
			//为scrollPane 添加content
			vbox_info = new VBox();
			vbox_info.setMaxHeight(Double.MAX_VALUE);
			vbox_info.setMaxWidth(Double.MAX_VALUE);
			vbox_info.setSpacing(10);
			
			scrollPane_info.setContent(vbox_info);
			
			btn_getGoodsId.setOnMouseClicked(new EventHandler<Event>() {
				public void handle(Event event) {
					getGoodsId();
				}; 
			});
			
			btn_submit.setDisable(true);//初始时不可提交
			btn_submit.setOnMouseClicked(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					submit();
				}
			});
			
			btn_writeIn.setDisable(true);//初始设置写入按钮无效
			btn_writeIn.setOnMouseClicked(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					writeIn();
				}
			});
			
			datePicker.setValue(LocalDate.now());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		goodsTypeMap = new HashMap<>();
	}
	
	/**
	 * 写入商品ID到标签
	 */
	private void writeIn() {
		// TODO Auto-generated method stub
		if(!Main.isUhfConnected()) {
			Text text = new Text("UHF读写器尚未连接!");
			vbox_info.getChildren().add(text);
			return;
		}
		
		String goods_id = tf_goods_id.getText();
		int ret = UHFHelper.writeEPC_G2(goods_id);
		if(ret == 0) {
			Text text = new Text("写入成功:"+goods_id);
			vbox_info.getChildren().add(text);
			btn_submit.setDisable(false);
		}else {
			Text text = new Text("写入失败 : "+String.format("0x%x", ret));
			vbox_info.getChildren().add(text);
		}
	}
	
	/**
	 * 提交表单逻辑
	 */
	private void submit() {
		//商品类别id
		String type=choiceBox.getSelectionModel().getSelectedItem();
		final int type_id = goodsTypeMap.get(type);
		//商品生产时间
		Logger.getLogger(GoodsAddBox.class.getSimpleName()).log(Level.INFO, datePicker.getValue().toString());
		final Timestamp timestamp = Timestamp.valueOf( datePicker.getValue().atStartOfDay());
		final String batchNumber = tf_batch_number.getText();
		if("".equals(batchNumber)) {
			Text text = new Text("请输入生产批号!");
			vbox_info.getChildren().add(text);
			return;
		}
		final String goods_id = tf_goods_id.getText();
		Text text = new Text("提交新商品:"+goods_id);
		vbox_info.getChildren().add(text);
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String spec = "http://localhost:8888/api/market/Goods/submit";
				HashMap<String,String> map = new HashMap<>();
				map.put("id", goods_id);
				map.put("type_id", String.valueOf(type_id));
				map.put("manufacture_date", String.valueOf(timestamp.getTime()/1000));
				map.put("batch_number", batchNumber);
				String json = NetworkHelper.downloadString(spec, map, "POST");
				JSONObject jsonObj = JSONObject.fromObject(json);
				final int code = jsonObj.getInt("code");
				final String msg = jsonObj.getString("msg");
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Text text = new Text(msg);
						vbox_info.getChildren().add(text);
						
						if(code == 1) {
							tf_goods_id.setText("");//清空goods_id
							btn_submit.setDisable(true);
						}
					}
				});
			}
		})).start();
		
	}
	
	/**
	 * 开始内部加载逻辑
	 */
	public void start() {
		Text text = new Text("开始加载商品类别...");
		vbox_info.getChildren().add(text);
		
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String spec = "http://localhost:8888/api/market/Goods_Type/index";
				String json = NetworkHelper.downloadString(spec, null, "GET");
				//解析json数据
				JSONObject jsonObj = JSONObject.fromObject(json);
				int code =  jsonObj.getInt("code");
				final String msg = jsonObj.getString("msg");
				if(code!=1) {
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Text text = new Text(msg);
							vbox_info.getChildren().add(text);
						}
					});
				}else {
					final ArrayList<GoodsTypeBean> goodsTypeList = new ArrayList<>();
					JSONObject data = jsonObj.getJSONObject("data");
					JSONArray goodsTypeArr = data.getJSONArray("goods_type");
					int len = goodsTypeArr.size();
					for(int i=0;i<len;++i) {
						GoodsTypeBean bean = new GoodsTypeBean();
						JSONObject obj = goodsTypeArr.getJSONObject(i);
						bean.setId(obj.getInt("id"));
						bean.setName(obj.getString("name"));
						bean.setPrice((float)obj.getDouble("price"));
						bean.setImages(obj.getString("images"));
						bean.setAddress(obj.getString("address"));
						bean.setCompany(obj.getString("company"));
						
						goodsTypeList.add(bean);
					}
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Text text = new Text(msg);
							vbox_info.getChildren().add(text);
							goodsTypeMap.clear();
							//为choiceBox设置items
							ArrayList<String> list = new ArrayList<>();
							int size = goodsTypeList.size();
							for(int i=0;i<size;++i) {
								GoodsTypeBean bean = goodsTypeList.get(i);
								list.add(bean.getName());
								goodsTypeMap.put(bean.getName(), bean.getId());
							}
							choiceBox.setItems(FXCollections.observableArrayList(list));
							if(size>0) {
								choiceBox.getSelectionModel().select(0);
							}
						}
					});
				}
			}
		})).start();
	}
	
	/**
	 * 获取商品ID
	 */
	private void getGoodsId() {
		Text text = new Text("开始获取商品ID...");
		vbox_info.getChildren().add(text);
		
		String item = choiceBox.getSelectionModel().getSelectedItem();
		final int goodsType = goodsTypeMap.get(item);
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String spec = "http://localhost:8888/api/market/Goods/getGoodsId";
				HashMap<String,String> map = new HashMap<>();
				map.put("goodsType", String.valueOf(goodsType));
				String json = NetworkHelper.downloadString(spec, map, "POST");
				Logger.getLogger(GoodsAddBox.class.getSimpleName()).log(Level.INFO," get json: " + json);
				
				JSONObject jsonObj = JSONObject.fromObject(json);
				int code = jsonObj.getInt("code");
				final String msg = jsonObj.getString("msg");
				if(code != 1) {
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Text text = new Text(msg);
							GoodsAddBox.this.vbox_info.getChildren().add(text);
						}
					});
				}else {
					JSONObject data = jsonObj.getJSONObject("data");
					final String id = data.getString("goods_id");
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Text text = new Text(msg + " 商品ID: " + id);
							GoodsAddBox.this.vbox_info.getChildren().add(text);
							GoodsAddBox.this.tf_goods_id.setText(String.valueOf(id));
							
							btn_writeIn.setDisable(false);//写入按钮有效
						}
					});
				}
			}
		})).start();
	}
}
