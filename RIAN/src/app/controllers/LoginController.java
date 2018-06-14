package app.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONObject;

import app.SceneManager;
import app.customs.CustomDialog;
import devices.NetProtocols;
import devices.Setting;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class LoginController implements Initializable {

	private SceneManager sManager;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;

	@FXML
	AnchorPane root;
	@FXML
	TextField idField;
	@FXML
	PasswordField pwField;

	public void initialize(URL location, ResourceBundle resources) {
		pwField.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@SuppressWarnings("unchecked")
			@Override
			public void handle(KeyEvent event) {
				// ���ġ�� ���� ġ�� id/pw�� ���� ��� �͵���
				// ���̽��� �Ǵ�Ƽ� �������ݷ� ��������
				if (event.getCode() == KeyCode.ENTER) {
					String id = idField.getText();
					String pw = pwField.getText();

					JSONObject request = new JSONObject();
					request.put("type", NetProtocols.LOGIN_REQUEST);
					request.put("id", id);
					request.put("pw", pw);

					sendProtocol(request);
				}
			}
		});
	}

	public void INIT_CONTROLLER(SceneManager sceneManager, ObjectInputStream fromServer, ObjectOutputStream toServer) {
		this.sManager = sceneManager;
		this.fromServer = fromServer;
		this.toServer = toServer;
	}

	public void startListener() {
		new Listener().start();
	}

	class Listener extends Thread {

		@SuppressWarnings("unchecked")
		public void run() {
			System.out.println("LoginController ������ ������ ����");
			try {
				while (true) {
					JSONObject line = null;
					try {
						line = (JSONObject) fromServer.readObject();
						if (line == null)
							break;

					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

					if (line.get("type").equals(NetProtocols.LOGIN_ACCEPT)) {

						if (line.get("user_level").equals("������")) {
							sManager.doFullscreen(true);
							sManager.changeListenController("LOGIN_CONTROLLER");
							sManager.changeScene(Setting.test_fxml); // ���߿� ���������� ��ü�ϱ�

						} else {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									sManager.doFullscreen(true);
									sManager.changeListenController("LOGIN_CONTROLLER");
									sManager.changeScene(Setting.test2_fxml);
								}
							});
						}

					} else if (line.get("type").equals(NetProtocols.LOGIN_DENY)) {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								CustomDialog.showMessageDialog("���̵� ��й�ȣ�� Ȯ���ϼ���", sManager.getStage());
							}
						});

					} else if (line.get("type").equals(NetProtocols.EXIT_RESPOND)) {
						break;

					} else if (line.get("type").equals(NetProtocols.RECIEVE_READY)) {
						JSONObject protocol = new JSONObject();
						protocol.put("type", NetProtocols.RECIEVE_READY_OK);
						sendProtocol(protocol);
					}
				}
			} catch (IOException e) {
				System.out.println("�α׾ƿ� ó���� ������");
			}
			System.out.println("LoginController ������ ����");
		}
	}

	protected void sendProtocol(JSONObject protocol) {

		try {
			toServer.writeObject(protocol);
			toServer.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@FXML
	private void onLogin(ActionEvent e) {
		String id = idField.getText();
		String pw = pwField.getText();

		JSONObject request = new JSONObject();
		request.put("type", NetProtocols.LOGIN_REQUEST);
		request.put("id", id);
		request.put("pw", pw);

		sendProtocol(request);
	}

	@FXML
	private void onFindPassword(ActionEvent e) {
		sManager.changeListenController("LOGIN_CONTROLLER");
		sManager.changeScene(Setting.password_find_fxml);
	}

	@FXML
	private void onMemberJoin() {
		sManager.changeListenController("LOGIN_CONTROLLER");
		sManager.changeScene(Setting.memberjoin_fxml);
	}

}
