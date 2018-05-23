package goods;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import beans.Params;
import helper.DialogUtil;
import helper.NetworkHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.sf.json.JSONObject;

public class GoodsTypeAddController implements Initializable{

	@FXML private TextField tf_add_name;
	@FXML private TextField tf_add_price;
	@FXML private TextField tf_add_address;
	@FXML private TextField tf_add_company;
	@FXML private Button btn_add_submit;
	@FXML private ScrollPane scrollpane_add_info;
	private VBox vbox_info;
	
	private Parent parent;

	public GoodsTypeAddController() {
		// !important 不要使用这个构造器创建实例
	}
	
	public static GoodsTypeAddController getInstance() {
		URL location = GoodsTypeAddController.class.getResource("goodstype_add.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			// continue ?
		}
		GoodsTypeAddController instance = loader.getController();
		instance.parent = parent;
		return instance;
	}
	
	public Parent getRoot() {
		return parent;
	}
	
	/**
	 * 业务逻辑
	 */
	public void start() {

	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initViews();
	}
	

	private void initViews() {
		vbox_info = new VBox();
		vbox_info.setSpacing(10);
		vbox_info.setPadding(new Insets(10));
		scrollpane_add_info.setContent(vbox_info);
		btn_add_submit.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				submit();
			}
		});
		//设置面板滚动到最底端
		vbox_info.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				scrollpane_add_info.setVvalue(1);
			}
		});
	}

	private void submit() {
		String name = tf_add_name.getText();
		String price = tf_add_price.getText();
		String address = tf_add_address.getText();
		String company = tf_add_company.getText();
		if (!"".equals(name) && !"".equals(price) && !"".equals(address) && !"".equals(company)) {
			Stage dialog = DialogUtil.getLoadingDialog(null);
			dialog.show();
			float priceFloat = Float.valueOf(price);
			priceFloat *= 100;
			final String mPrice = String.valueOf((int)priceFloat);
			(new Thread(new Runnable() {

				@Override
				public void run() {
					String spec = Params.URL_GOODSTYPE_ADDPOST;
					HashMap<String, String> map = new HashMap<>();
					map.put("name", name);
					map.put("price", mPrice);
					map.put("address", address);
					map.put("company", company);
					String json = NetworkHelper.downloadString(spec, map, "POST", true);
					JSONObject jsonObj = JSONObject.fromObject(json);
					if (jsonObj != null) {
						final int code = jsonObj.getInt("code");
						final String msg = jsonObj.getString("msg");
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								dialog.close();
								Text text = new Text(msg);
								vbox_info.getChildren().add(text);
								if (code == 1) {
									// 清空填写的数据
									tf_add_name.setText("");
									tf_add_price.setText("");
									tf_add_address.setText("");
									tf_add_company.setText("");
								}
							}
						});
					} else {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								Text text = new Text("未知错误");
								vbox_info.getChildren().add(text);
								dialog.close();
							}
						});
					}
				}
			})).start();
		} else {
			Text text = new Text("请检查所有字段是否填写!");
			vbox_info.getChildren().add(text);
		}
	}

}
