package com.gthncz.mycheckinclient.login;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.gthncz.mycheckinclient.helper.INIHelper;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

public class SettingController implements Initializable {
	
	@FXML private TextField tf_store_id;
	@FXML private TextField tf_terminal_id;
	
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
		HashMap<String, String> ini = INIHelper.getIniSet();
		if(ini != null) {
			tf_store_id.setText(ini.get("store_id"));
			tf_terminal_id.setText(ini.get("terminal_id"));
		}
	}
	
	/**
	 * 提交写入
	 * @return
	 */
	public boolean applySubmit() {
		String storeId = tf_store_id.getText();
		String terminalId = tf_terminal_id.getText();
		HashMap<String, String> map = new HashMap<>();
		map.put("store_id", storeId);
		map.put("terminal_id", terminalId);
		return INIHelper.writeIni(map);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	

}
