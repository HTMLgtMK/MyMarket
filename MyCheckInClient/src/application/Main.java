package application;
	
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gthncz.mycheckinclient.beans.AdminstratorInfo;
import com.gthncz.mycheckinclient.beans.UHFReaderBean;
import com.gthncz.mycheckinclient.login.LoginController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * 自助收银系统项目
 * @author GT
 *
 */
public class Main extends Application {
	
	/** debug标识 */
	public static final boolean DEBUG = true;
	
	/**
	 * 用户登陆token
	 */
	private static String token;
	/**
	 * 用户信息
	 */
	private static AdminstratorInfo adminInfo;
	/**
	 * UHF读写器
	 */
	private static UHFReaderBean uhfBean;
	/**
	 * 是否已经连接
	 */
	private static boolean uhfConnected;
	
	@Override
	public void start(Stage primaryStage) {
		LoginController loginController = LoginController.getInstance();
		try {
			loginController.start(primaryStage);
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit(); // 程序退出
		}
	}
	
	public static void main(String[] args) {
		Charset charset = Charset.defaultCharset();
		Logger.getLogger(Main.class.getSimpleName()).log(Level.INFO, "default charset :" +charset);
		launch(args);
	}

	public static String getToken() {
		return token;
	}

	public static void setToken(String token) {
		Main.token = token;
	}

	public static AdminstratorInfo getAdminInfo() {
		return adminInfo;
	}

	public static void setAdminInfo(AdminstratorInfo adminInfo) {
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
	
}
