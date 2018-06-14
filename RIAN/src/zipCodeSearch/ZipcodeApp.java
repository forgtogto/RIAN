package zipCodeSearch;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ZipcodeApp extends Application{
 

 	public static void main(String[] args) {
		launch(args);
	}
 
	@Override
	public void start(Stage primaryStage) throws Exception {
 		Parent root = FXMLLoader.load(getClass().getResource("Zipcode.fxml"));
		primaryStage.setTitle("RIAN - ZipCode Search");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		
	}
}

