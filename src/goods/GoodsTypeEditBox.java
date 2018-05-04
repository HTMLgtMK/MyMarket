package goods;

import java.io.IOException;
import java.net.URL;

import beans.GoodsTypeBean;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class GoodsTypeEditBox extends BorderPane {

	private TextField tf_name;
	private TextField tf_price;
	private TextField tf_address;
	private TextField tf_company;

	private GoodsTypeBean goodsTypeBean;

	public GoodsTypeEditBox(GoodsTypeBean goodsTypeBean) {
		this.goodsTypeBean = goodsTypeBean;
		// init view
		URL location = getClass().getResource("goodstype_edit.fxml");
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
		tf_name = (TextField) parent.lookup("#tf_name");
		tf_price = (TextField) parent.lookup("#tf_price");
		tf_address = (TextField) parent.lookup("#tf_address");
		tf_company = (TextField) parent.lookup("#tf_company");
	}

	/**
	 * 业务逻辑
	 */
	public void start() {
		// init data
		tf_name.setText(goodsTypeBean.getName());
		tf_price.setText(String.valueOf(goodsTypeBean.getPrice()));
		tf_address.setText(goodsTypeBean.getAddress());
		tf_company.setText(goodsTypeBean.getCompany());
	}
	
	/*点击了提交按钮*/
	public GoodsTypeBean applySubmit() {
		goodsTypeBean.setName(tf_name.getText().trim());
		goodsTypeBean.setPrice(Integer.valueOf(tf_price.getText().trim()));
		goodsTypeBean.setAddress(tf_address.getText().trim());
		goodsTypeBean.setCompany(tf_company.getText().trim());
		return goodsTypeBean;
	}
	
}
