package goods;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import beans.Params;
import helper.DialogUtil;
import helper.NetworkHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.sf.json.JSONObject;

public class GoodsTypeAddBox extends BorderPane {

	private TextField tf_name;
	private TextField tf_price;
	private TextField tf_address;
	private TextField tf_company;
	private Button btn_submit;
	private VBox vbox_info;

	public GoodsTypeAddBox() {
		URL location = getClass().getResource("goodstype_add.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		loader.setRoot(this);
		loader.setController(this);
		try {
			Parent parent = loader.load();
			initViews(parent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initViews(Parent parent) {
		tf_name = (TextField) parent.lookup("#tf_add_name");
		tf_price = (TextField) parent.lookup("#tf_add_price");
		tf_address = (TextField) parent.lookup("#tf_add_address");
		tf_company = (TextField) parent.lookup("#tf_add_company");

		btn_submit = (Button) parent.lookup("#btn_add_submit");
		ScrollPane scrollpane_add_info = (ScrollPane) parent.lookup("#scrollpane_add_info");
		vbox_info = new VBox();
		vbox_info.setSpacing(10);
		vbox_info.setPadding(new Insets(10));
		scrollpane_add_info.setContent(vbox_info);
		btn_submit.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
		String name = tf_name.getText();
		String price = tf_price.getText();
		String address = tf_address.getText();
		String company = tf_company.getText();
		if (!"".equals(name) && !"".equals(price) && !"".equals(address) && !"".equals(company)) {
			Stage dialog = DialogUtil.getLoadingDialog(null);
			dialog.show();
			(new Thread(new Runnable() {

				@Override
				public void run() {
					String spec = Params.URL_GOODSTYPE_ADDPOST;
					HashMap<String, String> map = new HashMap<>();
					map.put("name", name);
					map.put("price", price);
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
								Text text = new Text(msg);
								vbox_info.getChildren().add(text);
								if (code == 1) {
									// 清空填写的数据
									tf_name.setText("");
									tf_price.setText("");
									tf_address.setText("");
									tf_company.setText("");
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

	/**
	 * 业务逻辑
	 */
	public void start() {

	}

}
