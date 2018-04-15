package checkin;

import java.net.URL;
import java.util.ResourceBundle;

import checkin.CheckInControl.OnShowPageListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;

public class CheckInPayResultControl implements Initializable {
	
	@FXML
	private BorderPane checkin_pay_result_root;
	
	private OnShowPageListener showPageListener;
	
	public void setOnShowPageListener(OnShowPageListener listener) {
		this.showPageListener = listener;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getBounds();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		checkin_pay_result_root.setPrefSize(width, height);
	}

}
