package goods;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import beans.GoodsBean;
import beans.Params;
import helper.DialogUtil;
import helper.NetworkHelper;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GoodsIndexController implements Initializable {
	
	@FXML private TextField tf_name;
	@FXML private TextField tf_keywords;
	@FXML private TextField tf_batch_number;
	@FXML private ChoiceBox<String> chb_status;
	@FXML private Button btn_search;
	@FXML private Button btn_clear;
	@FXML private TableView<GoodsBean> table_goods;
	private TableColumn<GoodsBean, String> priceColumn;
	private TableColumn<GoodsBean, String> dateColumn;
	private TableColumn<GoodsBean, String> statusColumn;
	private TableColumn<GoodsBean, String> actionColumn; 
	@FXML private Button btn_previous;
	@FXML private Button btn_next;
	@FXML private ScrollPane scrollPane_center;
	private Parent parent;
	//当前页码
	private int page;
	private ObservableList<GoodsBean> goodsList;
	
	public GoodsIndexController() {
		page = 1;
		goodsList = FXCollections.observableArrayList();
		// !important Do Not use this construct create instance
	}
	
	public static GoodsIndexController getInstance() {
		URL location = GoodsIndexController.class.getResource("goods_index.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			// continue ?
		}
		GoodsIndexController instance = loader.getController();
		instance.parent = parent;
		return instance;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initViews();
	}
	
	public Parent getRoot() {
		return parent;
	}

	@SuppressWarnings("unchecked")
	private void initViews() {
		table_goods = (TableView<GoodsBean>) scrollPane_center.getContent();
		ObservableList<TableColumn<GoodsBean, ?>> columnList  = table_goods.getColumns();
		priceColumn = (TableColumn<GoodsBean, String>) columnList.get(2);
		dateColumn = (TableColumn<GoodsBean, String>) columnList.get(3);
		statusColumn = (TableColumn<GoodsBean, String>) columnList.get(5);
		actionColumn = (TableColumn<GoodsBean, String>) columnList.get(6);
		
		chb_status.getSelectionModel().select(1);//待售
		
		priceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GoodsBean,String>, ObservableValue<String>>() {
			
			@Override
			public ObservableValue<String> call(CellDataFeatures<GoodsBean, String> param) {
				String price = String.format("%.2f", ((float)(param.getValue().getPrice())/100));
				return new SimpleStringProperty(price);
			}
		});
		
		dateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GoodsBean,String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<GoodsBean, String> param) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String date = format.format(new Date(param.getValue().getManufacture_date()*1000));
				return new SimpleStringProperty(date);
			}
		});
		
		statusColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GoodsBean,String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<GoodsBean, String> param) {
				String status=null;
				switch(param.getValue().getStatus()) {
				case 1:{
					status=  "待售";
					break;
				}
				case 2:{
					status = "已售";
					break;
				}
				case 3:{
					status = "被锁定";
					break;
				}
				default:{
					status = "未知";
				}
				}
				return new SimpleStringProperty(status);
			}
		});
		
		actionColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GoodsBean,String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<GoodsBean, String> param) {
				return new SimpleStringProperty(param.getValue().getGoods_id());
			}
		});
		actionColumn.setCellFactory(new Callback<TableColumn<GoodsBean,String>, TableCell<GoodsBean,String>>() {

			@Override
			public TableCell<GoodsBean, String> call(TableColumn<GoodsBean, String> param) {
				ActionTableCell cell = new ActionTableCell();
				return cell;
			}
		});
		
		table_goods.setItems(goodsList);
		
		btn_previous.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(page == 1) return;
				else {
					--page;
					start();
				}
			}
		});
		
		btn_next.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				++page;
				start();
			}
		});
		
		btn_search.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				page = 1;
				start();
			}
		});
		btn_clear.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				page = 1;
				tf_name.setText("");
				tf_keywords.setText("");
				tf_batch_number.setText("");
				chb_status.getSelectionModel().select(1);//待售
				start();
			}
		});
	}
	
	/**
	 * 开始业务逻辑
	 */
	public void start() {
		String name = tf_name.getText().trim();
		String keywords = tf_keywords.getText().trim();
		String batch_number = tf_batch_number.getText().trim();
		int status = chb_status.getSelectionModel().getSelectedIndex();
		goodsList.clear();
		Stage dialog = DialogUtil.getLoadingDialog(null);
		dialog.show();
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				String spec = Params.URL_GOODS_INDEX;
				HashMap<String,String> map = new HashMap<>();
				map.put("name", name);
				map.put("keywords", keywords);
				map.put("batch_number", batch_number);
				map.put("status", String.valueOf(status));
				map.put("page", String.valueOf(page));
				String json = NetworkHelper.downloadString(spec, map, "POST");
				JSONObject jsonObj = JSONObject.fromObject(json);
				if(jsonObj != null) {
					final int code = jsonObj.getInt("code");
					final String msg = jsonObj.getString("msg");
					if(code == 1) {
						JSONObject dataObj = jsonObj.getJSONObject("data");
						dataObj = dataObj.getJSONObject("goods");
						JSONArray goods = dataObj.getJSONArray("data");
						int count = goods.size();
						for(int i=0;i<count;++i) {
							JSONObject obj = goods.getJSONObject(i);
							GoodsBean bean = new GoodsBean();
							bean.setBatch_number(obj.optString("batch_number"));
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
					}
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							dialog.close();
							if(code == 1) {
								table_goods.setItems(goodsList);
							}else {
								Alert alert = new Alert(AlertType.INFORMATION, msg, ButtonType.CLOSE);
								alert.show();
							}
						}
					});
				}else {// jsonObj is null
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							dialog.close();
							Alert alert = new Alert(AlertType.INFORMATION, "未知错误!", ButtonType.CLOSE);
							alert.show();
						}
					});
				}
			}
		})).start();
	}
	
	private GoodsBean getGoodsBeanFromList(String id) {
		for(GoodsBean bean: goodsList) {
			if(bean.getGoods_id().equals(id)) {
				return bean;
			}
		}
		return null;
	}
	
	/*操作列*/
	private class ActionTableCell extends TableCell<GoodsBean, String>  {
		
		private Button btn_edit;
		private Button btn_delete;
		private HBox hbox;
		
		public ActionTableCell() {
			btn_edit = new Button("编辑");
			btn_delete = new Button("下架");
			hbox = new HBox();
			hbox.setSpacing(10);
			hbox.setAlignment(Pos.CENTER);
			hbox.getChildren().addAll(btn_edit, btn_delete);
			
			btn_edit.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					String id = ActionTableCell.this.getItem();
					GoodsBean bean = getGoodsBeanFromList(id);
					if(bean.getStatus() != 1) {
						Alert alert = new Alert(AlertType.ERROR, "只有待售商品可以编辑!", ButtonType.CLOSE);
						alert.show();
					}else {
						editGoods(bean);
					}
				}
			});
			btn_delete.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					String id = ActionTableCell.this.getItem();
					GoodsBean bean = getGoodsBeanFromList(id);
					if(bean.getStatus() != 1) {
						Alert alert = new Alert(AlertType.ERROR, "只有待售商品可以下架!", ButtonType.CLOSE);
						alert.show();
					}else {
						Alert alert = new Alert(AlertType.WARNING, "确定下架此商品?", ButtonType.OK, ButtonType.NO);
						Optional<ButtonType> opt = alert.showAndWait();
						if(opt.get().equals(ButtonType.OK)) {
							deleteGoods(id);
						}
					}
				}
			});
			
		}
		
		/*编辑商品*/
		protected void editGoods(GoodsBean goodsBean) {
			Dialog<GoodsBean> dialog = new Dialog<>();
			dialog.setHeaderText("编辑商品");
			GoodsEditController controller = GoodsEditController.getInstance();
			Parent parent = controller.getRoot();
			dialog.getDialogPane().setContent(parent);
			ButtonType submitButton = new ButtonType("提交", ButtonData.APPLY);
			dialog.getDialogPane().getButtonTypes().addAll(submitButton, ButtonType.CANCEL);
			dialog.setResultConverter(new Callback<ButtonType, GoodsBean>() {

				@Override
				public GoodsBean call(ButtonType param) {
					if(param.equals(submitButton)) {
						GoodsBean goodsBean = controller.applySubmit();
						editGoodsPost(goodsBean);
						return goodsBean;
					}
					return null;
				}
			});
			dialog.show();
			controller.start(goodsBean);
		}
		
		/*编辑商品提交*/
		protected void editGoodsPost(GoodsBean goodsBean) {
			Stage dialog = DialogUtil.getLoadingDialog(null);
			dialog.show();
			(new Thread(new Runnable() {
				
				@Override
				public void run() {
					String spec = Params.URL_GOODS_EDITPOST;
					HashMap<String,String> map = new HashMap<>();
					map.put("id", goodsBean.getGoods_id());
					map.put("type_id", String.valueOf(goodsBean.getType_id()));
					map.put("manufacture_date", String.valueOf(goodsBean.getManufacture_date()));
					map.put("batch_number", goodsBean.getBatch_number());
					
					String json = NetworkHelper.downloadString(spec, map, "POST");
					JSONObject jsonObj = JSONObject.fromObject(json);
					if(jsonObj != null) {
						final int code = jsonObj.getInt("code");
						final String msg = jsonObj.getString("msg");
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								dialog.close();
								if(code == 1) {
									start();
								}else {
									Alert alert = new Alert(AlertType.ERROR,  msg, ButtonType.CLOSE);
									alert.show();
								}
							}
						});
					}else {
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								dialog.close();
								Alert alert = new Alert(AlertType.ERROR, "未知错误!", ButtonType.CLOSE);
								alert.show();
							}
						});
					}
				}
			})).start();
		}

		protected void deleteGoods(String id) {
			Stage dialog = DialogUtil.getLoadingDialog(null);
			dialog.show();
			(new Thread(new Runnable() {
				
				@Override
				public void run() {
					String spec = Params.URL_GOODS_DELETE;
					HashMap<String, String> map = new HashMap<>();
					map.put("id", id);
					String json = NetworkHelper.downloadString(spec, map, "POST");
					JSONObject jsonObj = JSONObject.fromObject(json);
					if(json!=null) {
						final int code = jsonObj.getInt("code");
						final String msg = jsonObj.getString("msg");
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								dialog.close();
								if(code == 1) {
									//重新载入列表
									start();
								}else {
									Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.CLOSE);
									alert.show();
								}
							}
						});
					}else {
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								dialog.close();
								Alert alert = new Alert(AlertType.ERROR, "未知错误!", ButtonType.CLOSE);
								alert.show();
							}
						});
					}
				}
			})).start();
		}

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if(!empty) {
				setContentDisplay(ContentDisplay.CENTER);
				setGraphic(hbox);
			}else {
				setGraphic(null);
			}
		}
		
	}

}
