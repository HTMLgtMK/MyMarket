package goods;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import beans.GoodsTypeBean;
import beans.Params;
import helper.NetworkHelper;
import helper.UHFHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.sf.json.JSONObject;

public class GoodsAddBox extends BorderPane {
	
	private TextField tf_typename;
	private Button btn_select_goodsType;
	
	private DatePicker datePicker;
	private TextField tf_batch_number;
	private TextField tf_goods_id;
	private ScrollPane scrollPane_info;
	private VBox vbox_info;
	
	private Button btn_getGoodsId;
	private Button btn_writeIn;
	
	private Button btn_submit;
	
	private GoodsTypeBean goodsTypeBean;/*选择的商品类别*/
	
	private Timer timer;
	
	public GoodsAddBox() {
		URL location = getClass().getResource("goods_add.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		loader.setRoot(this);
		loader.setController(this);
		try {
			Parent parent = loader.load();
			//初始化控件
			tf_typename = (TextField) parent.lookup("#tf_typename");
			btn_select_goodsType = (Button) parent.lookup("#btn_select_goodsType");
			datePicker = (DatePicker) parent.lookup("#datepicker_goodsdate");
			tf_batch_number =  (TextField) parent.lookup("#tf_goodsbatchnumber");
			tf_goods_id = (TextField) parent.lookup("#tf_goodsid");
			scrollPane_info = (ScrollPane) parent.lookup("#scrollpane_info");
			
			btn_getGoodsId = (Button) parent.lookup("#btn_getgoodsId");
			btn_writeIn = (Button) parent.lookup("#btn_writein");
			btn_submit = (Button) parent.lookup("#btn_submit");
			
			//为scrollPane 添加content
			vbox_info = new VBox();
			vbox_info.setSpacing(10);
			vbox_info.setPadding(new Insets(10));
			
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
					startWriteIn();
				}
			});
			
			datePicker.setValue(LocalDate.now());
			
			btn_select_goodsType.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					Dialog<GoodsTypeBean> dialog = new Dialog<>();
					GoodsTypeSelectBox box = new GoodsTypeSelectBox();
					box.setSelectItem(goodsTypeBean);
					dialog.getDialogPane().setContent(box);
					dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.NO);
					dialog.setResultConverter(new Callback<ButtonType, GoodsTypeBean>() {
						
						@Override
						public GoodsTypeBean call(ButtonType param) {
							if(param.equals(ButtonType.OK)) {
								GoodsTypeBean typeBean = box.getSelectedItem();
								GoodsAddBox.this.goodsTypeBean = typeBean;
								if(typeBean != null) {
									tf_typename.setText(typeBean.getName());
								}
								return typeBean;
							}
							return null;
						}
					});
					dialog.show();
					box.start();
				}
			});
			/*设置面板滚动*/
			vbox_info.heightProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					scrollPane_info.setVvalue(1);
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.addEventFilter(WindowEvent.WINDOW_HIDING, new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				if(GoodsAddBox.this.timer != null) {
					timer.cancel();
				}
			}
		});
	}
	
	public void startWriteIn() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				writeIn();
			}
		}, 0, 100);
		
	}
	
	/**
	 * 写入商品ID到标签
	 */
	private void writeIn() {
		if(!Main.isUhfConnected()) {
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					Text text = new Text("UHF读写器尚未连接!");
					vbox_info.getChildren().add(text);
					if(timer!=null) {
						timer.cancel();
						timer = null;						
					}
					return;
				}
			});
		}
		
		final String goods_id = tf_goods_id.getText();
		final int ret = UHFHelper.writeEPC_G2(goods_id);
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				if(ret == 0) {
					Text text = new Text("写入成功:"+goods_id);
					vbox_info.getChildren().add(text);
					btn_submit.setDisable(false);
					Logger.getLogger(GoodsAddBox.class.getSimpleName()).log(Level.INFO, "写入成功:"+goods_id);
					if(timer!=null) {
						timer.cancel();
						timer = null;
					}
				}else {
					Text text = new Text("写入失败 : "+String.format("0x%x", ret));
					vbox_info.getChildren().add(text);
				}
			}
		});
	}
	
	/**
	 * 提交表单逻辑
	 */
	private void submit() {
		//商品类别id
		int type_id= goodsTypeBean == null ? 0 : goodsTypeBean.getId();
		if(type_id == 0) {
			Text text = new Text("请先选择商品类别!");
			vbox_info.getChildren().add(text);
			return;
		}
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
				String spec = Params.URL_GOODS_SUBMIT;
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
		
	}
	
	/**
	 * 获取商品ID
	 */
	private void getGoodsId() {
		int type_id = goodsTypeBean == null ? 0 : goodsTypeBean.getId();
		if(type_id == 0) {
			Text text = new Text("请先选择商品类别!");
			vbox_info.getChildren().add(text);
			return;
		}
		
		Text text = new Text("开始获取商品ID...");
		vbox_info.getChildren().add(text);
		
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				String spec = Params.URL_GOODS_GETGOODSID;
				HashMap<String,String> map = new HashMap<>();
				map.put("goodsType", String.valueOf(type_id));
				String json = NetworkHelper.downloadString(spec, map, "POST");
				Logger.getLogger(GoodsAddBox.class.getSimpleName()).log(Level.INFO," get json: " + json);
				
				JSONObject jsonObj = JSONObject.fromObject(json);
				int code = jsonObj.getInt("code");
				final String msg = jsonObj.getString("msg");
				if(code != 1) {
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
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
