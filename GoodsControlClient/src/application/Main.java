package application;
	
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.AdminstratorInfo;
import beans.UHFReaderBean;
import javafx.application.Application;
import javafx.stage.Stage;
import login.LoginControl;


public class Main extends Application {
	
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
		LoginControl loginControl = LoginControl.getInstance();
		loginControl.start(primaryStage);
//		String arch = System.getProperty("sun.arch.data.model"); // 查看操作系统位数
//		System.out.println(arch);
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
