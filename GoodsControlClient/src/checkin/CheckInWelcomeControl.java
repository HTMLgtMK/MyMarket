package checkin;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import checkin.CheckInControl.OnShowPageListener;
import checkin.CheckInControl.Page;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Screen;

public class CheckInWelcomeControl implements Initializable {
	@FXML
	private BorderPane checkin_welcome_root;
	@FXML
	private Button btn_vip;
	@FXML
	private Button btn_unvip;
	@FXML
	private MediaView mediaView_welcome;
	
	private MediaPlayer mediaPlayer;
	
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
				stopMedia();
				showPageListener.showPage(Page.PAGE_USERLOGIN);
			};
		});
		btn_unvip.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				stopMedia();
				showPageListener.showPage(Page.PAGE_CART);
			};
		});
		
		File welcomeFile = new File("./resource/video/welcome.mp4");
		URI uri = welcomeFile.toURI();
		Media media = new Media(uri.toString());
		mediaPlayer = new MediaPlayer(media);
		mediaView_welcome.setMediaPlayer(mediaPlayer);
	}
	
	/**
	 * 开始业务逻辑
	 */
	public void start() {
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		mediaPlayer.play();
	}
	
	/**
	 * 停止播放视频,但是不释放资源
	 */
	private void stopMedia() {
		try {
			mediaPlayer.stop();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 释放资源
	 */
	public void releaseMedia() {
		stopMedia();
		mediaPlayer.dispose();
		mediaPlayer = null;
	}

	@Override
	protected void finalize() throws Throwable {
		Logger.getLogger(getClass().getSimpleName()).log(Level.INFO	, "welcomeController finalize!");
		super.finalize();
	}
	
	
}
