package com.gthncz.safeguard.main;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import com.gthncz.beans.AdminstratorInfo;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * 商品管理 欢迎页
 * @author GT
 *
 */
public class SafeGuardWelcomeController implements Initializable {
	
	@FXML protected Label label_user_login;
	@FXML protected Label label_name;
	@FXML protected Label label_mobile;
	@FXML protected Label label_user_status;
	@FXML protected Label label_post_name;
	@FXML protected Label label_create_time;
	@FXML protected Button btn_logout;
	
	private Parent parent;

	public SafeGuardWelcomeController() {
		// !important 不要使用该构造器创建实例
	}
	
	public static SafeGuardWelcomeController getInstance() {
		URL location = SafeGuardWelcomeController.class.getResource("safeguard_welcome.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			// continue?
		}
		SafeGuardWelcomeController instance = loader.getController();
		instance.parent = parent;
		return instance;
	}

	/** 该界面的根 */
	public Parent getRoot() {
		return parent;
	}
	
	public void start() {
		AdminstratorInfo info = Main.getAdminInfo();
		if(info != null) {
			label_user_login.setText(info.getUser_login());
			label_name.setText(info.getName());
			label_mobile.setText(info.getMobile());
			switch(info.getUser_status()) {
			case 0:{
				label_user_status.setText("已离职");
				break;
			}
			case 1:{
				label_user_status.setText("正常");
				break;
			}
			case 2:{
				label_user_status.setText("未验证");
				break;
			}
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String c_date = format.format(new Date(info.getCreate_time()));
		label_create_time.setText(c_date);
		label_post_name.setText(info.getPost_name());
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

}
