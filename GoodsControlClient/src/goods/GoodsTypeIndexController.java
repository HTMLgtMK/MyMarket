package goods;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.GoodsTypeBean;
import beans.Params;
import helper.DialogUtil;
import helper.NetworkHelper;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
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

public class GoodsTypeIndexController implements Initializable {
	@FXML private TextField tf_keywords;
	@FXML private Button btn_search;
	@FXML private Button btn_clear;
	@FXML private TableView<GoodsTypeBean> table_goodstype;
	private TableColumn<GoodsTypeBean, String> priceColumn;
	private TableColumn<GoodsTypeBean, Integer> actionColumn;
	@FXML  private Button btn_previous;
	@FXML private Button btn_next;
	@FXML private ScrollPane scrollPane_center;
	
	private Parent parent;
	/* 当前页码 */
	private int page;
	/* 商品类别列表 */
	private ObservableList<GoodsTypeBean> goodsTypeList;

	public GoodsTypeIndexController() {
		page = 1;/* 初始化数据 */
		goodsTypeList = FXCollections.observableArrayList();
	}
	
	public static GoodsTypeIndexController getInstance() {
		URL url = GoodsTypeIndexController.class.getResource("goodstype_index.fxml");
		FXMLLoader loader = new FXMLLoader(url);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GoodsTypeIndexController instance = loader.getController();
		instance.parent = parent;
		return instance;
	}
	
	public Parent getRoot() {
		return parent;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initViews();
	}

	@SuppressWarnings("unchecked")
	private void initViews() {
		table_goodstype = (TableView<GoodsTypeBean>) scrollPane_center.getContent();
		ObservableList<TableColumn<GoodsTypeBean, ?>> columnList = table_goodstype.getColumns();
		priceColumn = (TableColumn<GoodsTypeBean, String>) columnList.get(3);
		actionColumn = (TableColumn<GoodsTypeBean, Integer>) columnList.get(6);
		
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
				tf_keywords.setText("");
				page = 1;
				start();
			}
		});
		btn_previous.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				event.consume();
				if(page!=1) {
					--page;
					start();
				}
			}
		});
		btn_next.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				event.consume();
				++page;
				start();
			}
		});
		table_goodstype.setItems(goodsTypeList);
		priceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GoodsTypeBean,String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<GoodsTypeBean, String> param) {
				String price = String.format("%.2f", ((float)param.getValue().getPrice())/100);
				return new SimpleStringProperty(price);// 简单String属性
			}
		});
		actionColumn.setCellFactory(new Callback<TableColumn<GoodsTypeBean,Integer>, TableCell<GoodsTypeBean,Integer>>() {

			@Override
			public TableCell<GoodsTypeBean, Integer> call(TableColumn<GoodsTypeBean, Integer> param) {
				ActionTableCell cell = new ActionTableCell();
				return cell;
			}
		});
		actionColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GoodsTypeBean,Integer>, ObservableValue<Integer>>() {

			@Override
			public ObservableValue<Integer> call(CellDataFeatures<GoodsTypeBean, Integer> param) {
				// ObservableValue<Integer> obsInt = new ReadOnlyObjectWrapper<>(intValue);// old version
				return (new SimpleIntegerProperty(param.getValue().getId())).asObject();//javaFx 8
			}
		});
	}

	/**
	 * 开始业务逻辑
	 */
	public void start() {
		goodsTypeList.clear();
		Stage dialog = DialogUtil.getLoadingDialog(null);
		dialog.show();
		String keyword = tf_keywords.getText();
		(new Thread(new Runnable() {
			@Override
			public void run() {
				String spec = Params.URL_GOODSTYPE_INDEX;
				HashMap<String, String> map = new HashMap<>();
				if (!"".equals(keyword)) {
					map.put("keyword", keyword);
				}
				map.put("page", String.valueOf(page));
				String json = NetworkHelper.downloadString(spec, map, "POST");
				JSONObject jsonObj = JSONObject.fromObject(json);
				if (jsonObj != null) {
					int code =  jsonObj.getInt("code");
					final String msg = jsonObj.getString("msg");
					if(code!=1) {
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								Alert alert = new Alert(AlertType.ERROR);
								alert.setContentText(msg);
								alert.getButtonTypes().add(ButtonType.CLOSE);
							}
						});
					}else {
						JSONObject data = jsonObj.getJSONObject("data");
						JSONObject typeObj = data.getJSONObject("goods_type");
						JSONArray goodsTypeArr = typeObj.getJSONArray("data");
						int len = goodsTypeArr.size();
						for(int i=0;i<len;++i) {
							GoodsTypeBean bean = new GoodsTypeBean();
							JSONObject obj = goodsTypeArr.getJSONObject(i);
							bean.setId(obj.getInt("id"));
							bean.setName(obj.getString("name"));
							bean.setPrice(obj.getInt("price"));
							bean.setImages(obj.getString("images"));
							bean.setAddress(obj.getString("address"));
							bean.setCompany(obj.getString("company"));
							goodsTypeList.add(bean);
						}
					
						Platform.runLater(new Runnable() {
	
							@Override
							public void run() {
								table_goodstype.setItems(goodsTypeList);
								dialog.close();
							}
						});
					}// if code == 1
				}else{
					Platform.runLater(new Runnable() {
			
						@Override
						public void run() {
							dialog.close();
						}
					});
				}// if jsonObj == null
			}// run
		})).start();

	}

	private GoodsTypeBean getGoodsTypeBeanFromList(int id) {
		for(GoodsTypeBean bean:goodsTypeList) {
			if(bean.getId() == id) return bean;
		}
		return null;
	}
	
	private class ActionTableCell extends TableCell<GoodsTypeBean, Integer>{
		
		private Button btn_edit;
		private Button btn_delete;
		private HBox hbox;
		
		public ActionTableCell() {
			btn_edit = new Button("编辑");
			btn_delete = new Button("删除");
			hbox = new HBox();
			hbox.setSpacing(10);
			hbox.setAlignment(Pos.CENTER);
			hbox.getChildren().addAll(btn_edit, btn_delete);
			btn_edit.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					int id = ActionTableCell.this.getItem();
					GoodsTypeBean bean = getGoodsTypeBeanFromList(id);
					Dialog<GoodsTypeBean> dialog = new Dialog<>();
					GoodsTypeEditController controller = GoodsTypeEditController.getInstance();
					Parent parent = controller.getRoot();
					dialog.getDialogPane().setContent(parent);
					dialog.setTitle("编辑商品类别");
					ButtonType submitButton = new ButtonType("提交",ButtonData.APPLY);
					dialog.getDialogPane().getButtonTypes().addAll(submitButton, ButtonType.CANCEL);
					dialog.show();
					dialog.setResultConverter(new Callback<ButtonType, GoodsTypeBean>() {

						@Override
						public GoodsTypeBean call(ButtonType param) {
							if(param.getButtonData().equals(ButtonData.APPLY)) {
								GoodsTypeBean newBean = controller.applySubmit();
								submitEdit(newBean);
								return newBean;
							}
							return bean;
						}
					});
					controller.start(bean);
				}
			});
			btn_delete.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					final int id = ActionTableCell.this.getItem();
					Alert alert = new Alert(AlertType.WARNING, "确定删除该商品类?", ButtonType.OK, ButtonType.NO);
					Optional<ButtonType> result = alert.showAndWait();
					if(result.get().equals(ButtonType.OK)) {
						final Stage dialog = DialogUtil.getLoadingDialog(null);
						dialog.show();
						final GoodsTypeBean goodsTypeBean = GoodsTypeIndexController.this.getGoodsTypeBeanFromList(id);
						(new Thread(new Runnable() {
							
							@Override
							public void run() {
								String spec = Params.URL_GOODSTYPE_DELETE;
								HashMap<String,String> map = new HashMap<>();
								map.put("id", String.valueOf(id));
								String json = NetworkHelper.downloadString(spec, map, "POST");
								JSONObject jsonObj = JSONObject.fromObject(json);
								if(jsonObj!=null) {
									final int code = jsonObj.getInt("code");
									final String msg = jsonObj.getString("msg");
									Platform.runLater(new Runnable() {
										
										@Override
										public void run() {
											dialog.close();
											if(code == 1) {
												GoodsTypeIndexController.this.goodsTypeList.remove(goodsTypeBean);
												GoodsTypeIndexController.this.table_goodstype.setItems(goodsTypeList);
											}else {
												Alert alert = new Alert(AlertType.INFORMATION, msg, ButtonType.CLOSE);
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
						Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "clicked delete: "+ id);
					}
				}
			});
		} 

		@Override
		protected void updateItem(Integer item, boolean empty) {
			super.updateItem(item, empty);
			if(!empty) {
				setContentDisplay(ContentDisplay.CENTER);
				setGraphic(hbox);
			}else {
				setGraphic(null);
			}
		}
		
	}
	
	private void submitEdit(GoodsTypeBean bean) {
		String id = String.valueOf(bean.getId());
		String name = bean.getName();
		String price = String.valueOf(bean.getPrice());
		String address = bean.getAddress();
		String company = bean.getCompany();
		if (!"".equals(name) && !"".equals(price) && !"".equals(address) && !"".equals(company)) {
			Stage dialog = DialogUtil.getLoadingDialog(null);
			dialog.show();
			(new Thread(new Runnable() {

				@Override
				public void run() {
					String spec = Params.URL_GOODSTYPE_EDITPOST;
					HashMap<String, String> map = new HashMap<>();
					map.put("id", id);
					map.put("name", name);
					map.put("price", price);// 分
					map.put("address", address);
					map.put("company", company);
					String json = NetworkHelper.downloadString(spec, map, "POST");
					JSONObject jsonObj = JSONObject.fromObject(json);
					if (jsonObj != null) {
						final int code = jsonObj.getInt("code");
						final String msg = jsonObj.getString("msg");
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								dialog.close();
								if(code == 1) {
									//更新数据列表
									start();
								}else {
									Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.CLOSE);
									alert.show();
								}
							}
						});
					} else {
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
		}else {
			Alert alert = new Alert(AlertType.ERROR, "请检查各项填写完整!", ButtonType.CLOSE);
			alert.show();
		}
	}
}
