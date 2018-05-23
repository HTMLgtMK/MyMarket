package goods;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import beans.GoodsTypeBean;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

public class GoodsTypeEditController implements Initializable {

	@FXML private TextField tf_name;
	@FXML private TextField tf_price;
	@FXML private TextField tf_address;
	@FXML private TextField tf_company;

	private Parent parent;
	
	private GoodsTypeBean goodsTypeBean;

	public GoodsTypeEditController() {
		// !important don't use this construct to create instance
	}
	
	public static GoodsTypeEditController getInstance() {
		URL location = GoodsTypeEditController.class.getResource("goodstype_edit.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GoodsTypeEditController instance = loader.getController();
		instance.parent = parent;
		return instance;
	}
	
	public Parent getRoot() {
		return parent;
	}

	/**
	 * 业务逻辑
	 */
	public void start(GoodsTypeBean goodsTypeBean) {
		this.goodsTypeBean = goodsTypeBean;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
}
