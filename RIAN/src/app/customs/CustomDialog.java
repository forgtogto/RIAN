package app.customs;

import java.io.IOException;

import app.controllers.InputDialogController;
import app.controllers.MessageDialogController;
import app.controllers.OkCancelDialogController;
import devices.Setting;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CustomDialog extends Stage{
	
	public static final int OK_OPTION = 1;
	public static final int CANCEL_OPTION = 2;
	
	Object controller = null;

	public CustomDialog(String uiName, String title, Stage owner, Modality modalType) {
		System.out.println(uiName);

		System.out.println(getClass().getResource(uiName));
		System.out.println(getClass().getResource(uiName).getPath());

		FXMLLoader loader = new FXMLLoader(getClass().getResource(uiName));
		this.setTitle(title);

		Parent p = null;

		try {
			p = loader.load();
			System.out.println(loader.getController() + "");
			this.setScene(new Scene(p));
			this.initModality(modalType);
			this.initOwner(owner);
			controller = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.setResizable(false);
	}
	
	public Object getController() {
		return controller;
	}

	public static CustomDialog showMessageDialog(String msg, Stage owner) {

		CustomDialog dlg = new CustomDialog(Setting.alert_dialog, Setting.alert_dialog_title, owner,
				Modality.WINDOW_MODAL);
		
		MessageDialogController con = (MessageDialogController) dlg.getController();
		con.setWindow(dlg);
		con.setMessage(msg);
		dlg.show();
		return dlg;
	}
 
	public static int showConfirmDialog(String msg, Stage owner) {
		CustomDialog dlg = new CustomDialog(Setting.ok_cancel_dialog, Setting.ok_cancel_dialog_title, owner,
				Modality.WINDOW_MODAL);
		OkCancelDialogController con = (OkCancelDialogController) dlg.getController();
		con.setWindow(dlg);
		con.setMessage(msg);
		dlg.showAndWait();
		return (int) dlg.getUserData();
	}

	public static String showInputDialog(String msg, Stage owner) {
		CustomDialog dlg = new CustomDialog(Setting.input_dialog, Setting.input_dialog_title, owner,
				Modality.WINDOW_MODAL);
		InputDialogController con = (InputDialogController) dlg.getController();
		con.setWindow(dlg);
		con.setMessage(msg);
		dlg.showAndWait();

		if ((int) dlg.getUserData() == OK_OPTION) {
			return con.input();
		} else {
			return null;
		}
	}

}
