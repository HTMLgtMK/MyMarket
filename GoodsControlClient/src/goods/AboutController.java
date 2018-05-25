package goods;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

/**
 * 关于该项目的说明
 * @author GT
 *
 */
public class AboutController implements Initializable {
	
	private Parent parent;
	
	public static AboutController getInstance() {
		URL location = AboutController.class.getResource("menu_about.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		AboutController instance = loader.getController();
		instance.parent = parent;
		return instance;
	}
	
	public Parent getRoot() {
		return parent;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

}
