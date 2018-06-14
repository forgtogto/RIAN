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
				// 비번치고 엔터 치면 id/pw에 담기고 담긴 것들이
				// 제이슨에 또담아서 프로토콜로 보내버림
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
			System.out.println("LoginController 리스너 스레드 시작");
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

						if (line.get("user_level").equals("관리자")) {
							sManager.doFullscreen(true);
							sManager.changeListenController("LOGIN_CONTROLLER");
							sManager.changeScene(Setting.test_fxml); // 나중에 메인페이지 교체하기

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
								CustomDialog.showMessageDialog("아이디나 비밀번호를 확인하세요", sManager.getStage());
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
				System.out.println("로그아웃 처리된 스레드");
			}
			System.out.println("LoginController 스레드 종료");
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
