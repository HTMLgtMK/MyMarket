package com.gthncz.safeguard.main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class SafeGuardController implements Initializable {
	private Stage mStage;
	
	@FXML protected MenuItem menuItem_setting;
	@FXML protected MenuItem menuItem_exit;
	@FXML protected MenuItem menuItem_welcome;
	@FXML protected MenuItem menuItem_controller;
	@FXML protected MenuItem menuItem_safeguard;
	@FXML protected MenuItem menuItem_about;
	
	@FXML protected TabPane tabPane_main;
	@FXML protected Tab tab_welcome;
	@FXML protected Tab tab_controller;
	@FXML protected Tab tab_safeguard;
	
	// TabPageController
	@FXML protected SafeGuardWelcomeController tabWelcomePageController;
	@FXML protected SafeGuardControlController tabControllerPageController;
	@FXML protected SafeGuardTabPageController tabSafeGuardPageController;
	
	private Parent parent;
	
	public static SafeGuardController getInstance() {
		URL location = SafeGuardController.class.getResource("safeguard_main.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SafeGuardController instance = loader.getController();
		instance.parent = parent;
		return instance;
	} 
	
	public Parent getRoot() {
		return parent;
	}
	
	public void start() {
		mStage = new Stage();
		Scene scene = new Scene(parent, 1000, 800);
		mStage.setScene(scene);
		Image logo16 = new Image("file:resource/drawable/logo16.png");
		Image logo32 = new Image("file:resource/drawable/logo32.png");
		mStage.getIcons().addAll(logo16, logo32);
		mStage.setTitle("无人超市安保系统");
		mStage.addEventHandler(WindowEvent.WINDOW_HIDING, new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if(tabSafeGuardPageController != null) {
					tabSafeGuardPageController.forceStopTimer();
				}
			};
		});
		mStage.show();
		
		showPage(TAB_PAGE_WELCOME);
		tabWelcomePageController.start();
	}

	public static final int TAB_PAGE_WELCOME = 0X00;
	public static final int TAB_PAGE_CONTROLLER = 0X01;
	public static final int TAB_PAGE_SAFEGUARD = 0X02;
	
	private  void showPage(int index) {
		tabPane_main.getSelectionModel().select(index);
	}
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tabPane_main.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
				if(newValue.equals(tab_welcome)) {
					tabWelcomePageController.start();
					tabSafeGuardPageController.forceStopTimer();
				}else if(newValue.equals(tab_controller)) {
					tabControllerPageController.start();
					tabSafeGuardPageController.forceStopTimer();
				}else if(newValue.equals(tab_safeguard)) {
					tabSafeGuardPageController.start();
				}
			}
		});
		initMenus();
	}

	private void initMenus() {
		menuItem_exit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});
		EventHandler<ActionEvent> pageEvent = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(event.getSource().equals(menuItem_welcome)) {
					showPage(TAB_PAGE_WELCOME);
				}else if(event.getSource().equals(menuItem_controller)) {
					showPage(TAB_PAGE_CONTROLLER);
				}else if(event.getSource().equals(menuItem_safeguard)) {
					showPage(TAB_PAGE_SAFEGUARD);
				}
			}
		}; 
		menuItem_welcome.setOnAction(pageEvent);
		menuItem_controller.setOnAction(pageEvent);
		menuItem_safeguard.setOnAction(pageEvent);

		menuItem_about.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Dialog<Void> dialog = new Dialog<>();
				String msg = "该软件仅用于学习使用!请勿作商业使用!交流请联系 GT_GameEmail@163.com";
				dialog.setTitle("关于");
				dialog.setContentText(msg);
				dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
				dialog.initModality(Modality.APPLICATION_MODAL);
				dialog.initOwner(mStage);
				dialog.show();
			}
		});
		
		menuItem_setting.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showSetting();
			}
		});
	}

	protected void showSetting() {
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("设置");
		final SettingController controller = SettingController.getInstance();
		Parent parent = controller.getRoot();
		dialog.getDialogPane().setContent(parent);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);
		dialog.setResultConverter(new Callback<ButtonType, Void>() {
			
			@Override
			public Void call(ButtonType param) {
				if(param.equals(ButtonType.APPLY)) {
					controller.applySubmit();
				}
				return null;
			}
		});
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(mStage);
		dialog.show();
		controller.start();
	}

}
