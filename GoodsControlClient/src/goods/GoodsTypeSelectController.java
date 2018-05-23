package goods;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import beans.GoodsTypeBean;
import beans.Params;
import helper.DialogUtil;
import helper.NetworkHelper;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GoodsTypeSelectController implements Initializable {
	@FXML private TextField tf_keywords;
	@FXML private Button btn_search;
	@FXML private Button btn_clear;
	@FXML private TableView<GoodsTypeBean> table_goodstype;
	private TableColumn<GoodsTypeBean, String> priceColumn;
	private TableColumn<GoodsTypeBean, Integer> actionColumn;
	private ToggleGroup actionGroup;
	@FXML private Button btn_previous;
	@FXML private Button btn_next;
	@FXML private Label label_current_type;
	
	private Parent parent;
	/* 当前页码 */
	private int page;
	/* 商品类别列表 */
	private ObservableList<GoodsTypeBean> goodsTypeList;
	/*当前商品类别*/
	private GoodsTypeBean typeBean;

	public GoodsTypeSelectController() {
		page = 1;/* 初始化数据 */
		goodsTypeList = FXCollections.observableArrayList();
	}
	
	public Parent getRoot() {
		return parent;
	}
	
	public static GoodsTypeSelectController getInstance() {
		URL url = GoodsTypeSelectController.class.getResource("goodstype_select.fxml");
		FXMLLoader loader = new FXMLLoader(url);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GoodsTypeSelectController instance = loader.getController();
		instance.parent = parent;
		return instance;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initViews();
	}

	@SuppressWarnings("unchecked")
	private void initViews() {
		actionGroup = new ToggleGroup();
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
		actionGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				
			}
		});
	}
	
	public void setSelectItem(GoodsTypeBean bean) {
		this.typeBean = bean;
		if(typeBean != null) {
			label_current_type.setText(bean.getId() + "  " + bean.getName());
		}
	}
	
	/**
	 * 获取当前选择的商品类型
	 * @return
	 */
	public GoodsTypeBean getSelectedItem() {
		return typeBean;
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
		
		private RadioButton radioButton;
		
		public ActionTableCell() {
			radioButton = new RadioButton();
			radioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					int id = getItem();
					GoodsTypeBean bean = getGoodsTypeBeanFromList(id);
					setSelectItem(bean);
				}
			});
			radioButton.setToggleGroup(actionGroup);
		} 

		@Override
		protected void updateItem(Integer item, boolean empty) {
			super.updateItem(item, empty);
			if(!empty) {
				setContentDisplay(ContentDisplay.CENTER);
				setGraphic(radioButton);
				if(typeBean != null && item == typeBean.getId()) {
					radioButton.setSelected(true);
				}else {
					radioButton.setSelected(false);
				}
			}else {
				setGraphic(null);
			}
		}
	}
	
}
