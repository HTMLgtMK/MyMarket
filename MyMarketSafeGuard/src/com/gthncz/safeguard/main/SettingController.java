package com.gthncz.safeguard.main;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.gthncz.beans.Params;
import com.gthncz.helper.INIHelper;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SettingController implements Initializable {

	@FXML protected TextField tf_db_port;
	@FXML protected TextField tf_db_user;
	@FXML protected PasswordField pwdfield_db_pwd;
	@FXML protected TextField tf_db_name;
	@FXML protected TextField tf_server_port;
	
	private Parent parent;
	
	public static SettingController getInstance() {
		URL location = SettingController.class.getResource("setting.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SettingController instance = loader.getController();
		instance.parent = parent;
		return instance;
	} 
	
	public Parent getRoot() {
		return parent;
	}
	
	public void start() {
		HashMap<String, String> ini = INIHelper.getIniSet(Params.INI_NAME);
		if(ini != null) {
			String db_port = ini.get("db_port");
			String db_user = ini.get("db_user");
			String db_pwd = ini.get("db_pwd");
			String db_name = ini.get("db_name");
			String server_port = ini.get("server_port");
			
			tf_db_port.setText(db_port);
			tf_db_user.setText(db_user);
			pwdfield_db_pwd.setText(db_pwd);
			tf_db_name.setText(db_name);
			tf_server_port.setText(server_port);
		}
	}
	
	public void applySubmit() {
		String db_port = tf_db_port.getText().trim();
		String db_user = tf_db_user.getText().trim();
		String db_pwd = pwdfield_db_pwd.getText().trim();
		String db_name = tf_db_name.getText().trim();
		String server_port = tf_server_port.getText().trim();
		
		HashMap<String, String> map = new HashMap<>();
		map.put("db_port", db_port);
		map.put("db_user", db_user);
		map.put("db_pwd", db_pwd);
		map.put("db_name", db_name);
		map.put("server_port", server_port);
		
		INIHelper.writeIni(Params.INI_NAME, map);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

}
