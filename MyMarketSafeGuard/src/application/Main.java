package application;
	
import com.gthncz.beans.UHFReaderBean;
import com.gthncz.safeguard.login.LoginController;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
	
	/**
	 * 用户登陆token
	 */
	private static String token;
	/**
	 * 用户信息
	 */
	private static com.gthncz.beans.AdminstratorInfo adminInfo;
	/**
	 * UHF读写器
	 */
	private static UHFReaderBean uhfBean;
	/**
	 * 是否已经连接
	 */
	private static boolean uhfConnected;
	
	public static String getToken() {
		return token;
	}

	public static void setToken(String token) {
		Main.token = token;
	}

	public static com.gthncz.beans.AdminstratorInfo getAdminInfo() {
		return adminInfo;
	}

	public static void setAdminInfo(com.gthncz.beans.AdminstratorInfo adminInfo) {
		Main.adminInfo = adminInfo;
	}

	public static UHFReaderBean getUhfBean() {
		return uhfBean;
	}

	public static void setUhfBean(UHFReaderBean uhfBean) {
		Main.uhfBean = uhfBean;
	}

	public static boolean isUhfConnected() {
		return uhfConnected;
	}

	public static void setUhfConnected(boolean uhfConnected) {
		Main.uhfConnected = uhfConnected;
	}

	@Override
	public void start(Stage primaryStage) {
		LoginController loginController = LoginController.getInstance();
		loginController.start(primaryStage);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
