package goods;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ResourceBundle;

import beans.GoodsBean;
import beans.GoodsTypeBean;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class GoodsEditController implements Initializable {

	@FXML private TextField tf_name;
	@FXML private Button btn_select_goodsType;
	@FXML private DatePicker datepicker_manufacture_date;
	@FXML private TextField tf_batch_number;

	private Parent parent;
	private GoodsBean goodsBean;

	public GoodsEditController() {
		// !important Don't use this construct create instance
	}
	
	public static GoodsEditController getInstance() {
		URL location = GoodsEditController.class.getResource("goods_edit.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GoodsEditController instance = loader.getController();
		instance.parent = parent;
		return instance;
	}
	
	public Parent getRoot() {
		return parent;
	}
	
	public void start(GoodsBean goodsBean) {
		this.goodsBean = goodsBean;
		initViews();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	private void initViews() {
		if (goodsBean != null) {
			LocalDate localDate = (new Date(goodsBean.getManufacture_date() * 1000)).toLocalDate();
			datepicker_manufacture_date.setValue(localDate);
			tf_batch_number.setText(goodsBean.getBatch_number());
		}

		tf_name.setText(goodsBean.getName());
		btn_select_goodsType.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				Dialog<GoodsTypeBean> dialog = new Dialog<>();
				GoodsTypeSelectController controller = GoodsTypeSelectController.getInstance();
				Parent parent = controller.getRoot();
				controller.setSelectItem(goodsBean);
				dialog.getDialogPane().setContent(parent);
				dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.NO);
				dialog.setResultConverter(new Callback<ButtonType, GoodsTypeBean>() {

					@Override
					public GoodsTypeBean call(ButtonType param) {
						if (param.equals(ButtonType.OK)) {
							GoodsTypeBean typeBean = controller.getSelectedItem();
							if (typeBean != null) {
								goodsBean.setName(typeBean.getName());
								goodsBean.setId(typeBean.getId());
								goodsBean.setType_id(typeBean.getId());
								goodsBean.setAddress(typeBean.getAddress());
								goodsBean.setCompany(typeBean.getCompany());
								goodsBean.setImages(typeBean.getImages());
								goodsBean.setPrice(typeBean.getPrice());

								tf_name.setText(typeBean.getName());
							}
							return typeBean;
						}
						return null;
					}
				});
				dialog.show();
				controller.start();
			}
		});
	}

	public GoodsBean applySubmit() {
		Timestamp timestamp = Timestamp.valueOf(datepicker_manufacture_date.getValue().atStartOfDay());
		goodsBean.setType_id(goodsBean.getType_id());
		goodsBean.setManufacture_date(timestamp.getTime() / 1000);
		goodsBean.setBatch_number(tf_batch_number.getText().trim());
		return goodsBean;
	}

}
