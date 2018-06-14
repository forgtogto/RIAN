package app.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import app.customs.CustomDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class MessageDialogController implements Initializable{

	private CustomDialog window;
	@FXML private Label msg_area;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {		
	}
	
	public void setWindow(CustomDialog window) {
		this.window = window;
	}
	
	public void setMessage(String msg) {
		msg_area.setText(msg);
	}
	
	@FXML
	public void onOK() {
		window.close();
	}
	

}
