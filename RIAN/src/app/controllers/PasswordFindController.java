package app.controllers;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;

import app.SceneManager;
import app.customs.CustomDialog;
import devices.NetProtocols;
import devices.Setting;
import devices.Toolbox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class PasswordFindController implements Initializable {

	SceneManager sManager;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;

	@FXML TextField IDFind;	@FXML TextField AnswerFind;	@FXML TextField PasswordNew; @FXML TextField PasswordNewComfirm;
	@FXML ComboBox<String> QuestionFind;
	@FXML AnchorPane PasswordAnchor;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void INIT_CONTROLLER(SceneManager manager, ObjectInputStream fromServer, ObjectOutputStream toServer) {
		this.sManager = manager;
		this.fromServer = fromServer;
		this.toServer = toServer;
	}

	public void startListener() {
		new Listener().start();
	}

	class Listener extends Thread {
		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			System.out.println("PasswordFindController 리스너 스레드 시작");
			try {
				while (true) {
					JSONObject respond = null;
					try {
						respond = (JSONObject) fromServer.readObject();
						System.out.println(respond.toJSONString());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

					String type = respond.get("type").toString();

					if (type.equals(NetProtocols.EXIT_RESPOND)) {
						break;
					} else if (type.equals(NetProtocols.RECIEVE_READY)) {
						JSONObject protocol = new JSONObject();
						protocol.put("type", NetProtocols.RECIEVE_READY_OK);
						protocol.put("request-view", "passwordFind");
						sendProtocol(protocol);
					} else if (type.equals(NetProtocols.PASSWORD_FIND_QUESTION_LIST)) {
						JSONArray data = (JSONArray) respond.get("data");
						Platform.runLater(() -> {
							for (Object o : data) {
								QuestionFind.getItems().add(o.toString());
							}
						});
					} else if (type.equals(NetProtocols.PASSWORD_FIND_IDENTIFY_RESPOND)) {
						// respond.put("identify-result", "commit");respond.put("identify-result",
						// "fault");
						if (respond.get("identify-result").equals("commit")) {
							Platform.runLater(() -> {
								IDFind.setEditable(false);
								CustomDialog.showMessageDialog("인증됐습니다. 새 비밀번호를 설정하세요.", sManager.getStage());
								PasswordAnchor.setDisable(false);
							});
						} else {
							Platform.runLater(() -> {
								CustomDialog.showMessageDialog("인증정보를 확인하세요", sManager.getStage());
							});
						}
					} else if (type.equals(NetProtocols.PASSWORD_FIND_MODIFY_RESPOND)) {
						Platform.runLater(() -> {
							CustomDialog.showMessageDialog("변경완료!", sManager.getStage());
							sManager.changeListenController("PASS_FIND_CONTROLLER");
							sManager.changeScene(Setting.loginproc_fxml);
						});
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("PasswordFindController 리스너 스레드 종료");
		}
	}

	@SuppressWarnings("unchecked")
	@FXML
	public void onApply() {
		if (PasswordNew.getText().length() == 0 || PasswordNewComfirm.getText().length() == 0) {
			CustomDialog.showMessageDialog("한 글자 이상 입력하세요.", sManager.getStage());
			return;
		}

		if (!PasswordNew.getText().equals(PasswordNewComfirm.getText())) {
			CustomDialog.showMessageDialog("입력한 비밀번호가 서로 다릅니다.", sManager.getStage());
			return;
		} else {
			JSONObject request = Toolbox.createJSONProtocol(NetProtocols.PASSWORD_FIND_MODIFY_REQUEST);
			request.put("request-pw", PasswordNew.getText());
			request.put("request-id", IDFind.getText());
			sendProtocol(request);
		}
	}

	@FXML
	private void onIdentify() {
		if (IDFind.getText().length() == 0 || QuestionFind.getValue() == null || AnswerFind.getText().length() == 0) {
			CustomDialog.showMessageDialog("인증정보를 정확히 입력하세요!", sManager.getStage());
			return;
		}

		String[] keys = { "학번", "질문", "답변" };
		Object[] values = { IDFind.getText(), QuestionFind.getSelectionModel().getSelectedIndex() + 1, AnswerFind.getText() };

		sendProtocol(Toolbox.createJSONProtocol(NetProtocols.PASSWORD_FIND_IDENTIFY_REQUEST, keys, values));

	}

	@FXML
	private void onCancel() {
		sManager.changeListenController("PASS_FIND_CONTROLLER");
		sManager.changeScene(Setting.loginproc_fxml);
	}

	public void sendProtocol(JSONObject protocol) {
		try {
			toServer.writeObject(protocol);
			toServer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}