package goods;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

import beans.GoodsBean;
import beans.GoodsTypeBean;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public class GoodsEditBox extends BorderPane {

	private TextField tf_name;
	private Button btn_select_goodsType;
	private DatePicker datepicker_manufacture_date;
	private TextField tf_batch_number;

	private GoodsBean goodsBean;

	public GoodsEditBox(GoodsBean goodsBean) {
		this.goodsBean = goodsBean;

		URL location = getClass().getResource("goods_edit.fxml");
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
		btn_select_goodsType = (Button) parent.lookup("#btn_select_goodsType");
		datepicker_manufacture_date = (DatePicker) parent.lookup("#datepicker_manufacture_date");
		tf_batch_number = (TextField) parent.lookup("#tf_batch_number");
		
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
				GoodsTypeSelectBox box = new GoodsTypeSelectBox();
				box.setSelectItem(goodsBean);
				dialog.getDialogPane().setContent(box);
				dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.NO);
				dialog.setResultConverter(new Callback<ButtonType, GoodsTypeBean>() {
					
					@Override
					public GoodsTypeBean call(ButtonType param) {
						if(param.equals(ButtonType.OK)) {
							GoodsTypeBean typeBean = box.getSelectedItem();
							if(typeBean != null) {
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
				box.start();
			}
		});
	}

	public GoodsBean applySubmit() {
		Timestamp timestamp = Timestamp.valueOf(datepicker_manufacture_date.getValue().atStartOfDay());
		goodsBean.setType_id(goodsBean.getType_id());
		goodsBean.setManufacture_date(timestamp.getTime()/1000);
		goodsBean.setBatch_number(tf_batch_number.getText().trim());
		return goodsBean;
	}

	public void start() {
		
	}
}
