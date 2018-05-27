package com.gthncz.helper;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DialogHelper {

	public static Stage getDialog(String msg) {
		Stage dialog = new Stage(StageStyle.TRANSPARENT);
		ProgressBar pb = new ProgressBar();
		Text text = new Text(msg);
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(10);
		vbox.setPadding(new Insets(10,10,10,10));
		text.setFill(Color.WHITE);
		vbox.getChildren().addAll(pb, text);
		vbox.setStyle("-fx-background-color:#000000;-fx-border-radiu:10px;-fx-background-radiu:10px;");
		Scene scene = new Scene(vbox);
		dialog.setScene(scene);
		dialog.setOpacity(0.5);
		dialog.initModality(Modality.APPLICATION_MODAL);
		return dialog;
	}
	
	/* 获取加载数据对话框 */
	public static Stage getLoadingDialog(EventHandler<Event> cancelHandler) {
		Stage stage = new Stage(StageStyle.UNDECORATED);
		VBox vbox = new VBox();
		ProgressBar progressBar = new ProgressBar();
		Button btn_cancel = new Button();
		btn_cancel.setText("取消");
		btn_cancel.setDefaultButton(true);
		vbox.getChildren().addAll(progressBar, btn_cancel);
		vbox.setSpacing(10);
		vbox.setPadding(new Insets(10));
		vbox.setAlignment(Pos.CENTER);
		vbox.setStyle("-fx-background-color:#000000;-fx-border-radiu:10px;-fx-background-radiu:10px;");
		Scene scene = new Scene(vbox);
		stage.setScene(scene);
		stage.setOpacity(0.5f);
		stage.initModality(Modality.APPLICATION_MODAL);
		btn_cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (stage.isShowing()) {
					if (cancelHandler != null) {
						cancelHandler.handle(event);
					}
					stage.hide();
				}
			}
		});
		return stage;
	}
	
}
