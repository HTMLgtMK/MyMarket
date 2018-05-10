package checkin;

import java.net.URL;
import java.util.ResourceBundle;

import checkin.CheckInControl.OnShowPageListener;
import checkin.CheckInControl.Page;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

public class CheckInWelcomeControl implements Initializable {
	@FXML
	private StackPane checkin_welcome_root;
	@FXML
	private Button btn_vip;
	@FXML
	private Button btn_unvip;
	
	private OnShowPageListener showPageListener;
	
	public void setOnShowPageListener(OnShowPageListener listener) {
		this.showPageListener = listener;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getBounds();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		checkin_welcome_root.setPrefSize(width, height);
		
		btn_vip.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				showPageListener.showPage(Page.PAGE_USERLOGIN);
			};
		});
		btn_unvip.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				showPageListener.showPage(Page.PAGE_CART);
			};
		});
	}

}
