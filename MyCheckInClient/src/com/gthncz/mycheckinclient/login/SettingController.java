package com.gthncz.mycheckinclient.login;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.gthncz.mycheckinclient.beans.Params;
import com.gthncz.mycheckinclient.helper.INIHelper;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

public class SettingController implements Initializable {
	
	@FXML private TextField tf_store_id;
	@FXML private TextField tf_terminal_id;
	@FXML private TextField tf_safeguard_addr;
	@FXML private TextField tf_safeguard_port;
	
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
			tf_store_id.setText(ini.get("store_id"));
			tf_terminal_id.setText(ini.get("terminal_id"));
			tf_safeguard_addr.setText(ini.get("safeguard_addr"));
			tf_safeguard_port.setText(ini.get("safeguard_port"));
		}
	}
	
	/**
	 * 提交写入
	 * @return
	 */
	public boolean applySubmit() {
		String storeId = tf_store_id.getText();
		String terminalId = tf_terminal_id.getText();
		String safeguard_addr = tf_safeguard_addr.getText();
		String safeguard_port = tf_safeguard_port.getText();
		HashMap<String, String> map = new HashMap<>();
		map.put("store_id", storeId);
		map.put("terminal_id", terminalId);
		map.put("safeguard_addr", safeguard_addr);
		map.put("safeguard_port", safeguard_port);
		return INIHelper.writeIni(Params.INI_NAME, map);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	

}
