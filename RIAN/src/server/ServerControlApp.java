package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
 

public class ServerControlApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("./ServerMainGUI.fxml"));
		Parent root = loader.load();
		
		ServerMainController con = (ServerMainController)loader.getController();
		
		primaryStage.setTitle("RIAN ���� ��Ʈ�� ���ø����̼�");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		Application.launch(ServerControlApp.class);
	}

}
