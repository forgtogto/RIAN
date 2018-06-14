package app.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONObject;

import app.MainApplication;
import app.SceneManager;
import app.customs.CustomDialog;
import devices.NetProtocols;
import devices.Setting;
import devices.Toolbox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import zipCodeSearch.SendingData;
import zipCodeSearch.ZipCodeController;
import zipCodeSearch.ZipCodeTableViewModel;
import zipCodeSearch.ZipcodeApp;

public class MemberJoinController implements Initializable{
	// ���̵� / ��й�ȣ / ���Ȯ��/ ��� ����/ �������/ �̸�/ ����/ �������
	// �̸��� / �����ȣ // �ּ� 
	//��ư �ߺ�Ȯ�� ��� ��� 
	
	@FXML TextField idField;		//���̵�
	@FXML TextField pwdField;		//���
	@FXML TextField rePwdField;		//���Ȯ��
	@FXML TextField qAnswerField;	//�������
	@FXML TextField nameField;		//�̸�	
	@FXML TextField birthField;		//�������
	@FXML TextField emailField;		//�̸���
	@FXML TextField phone1Field;	//����ó �߰�
	@FXML TextField phone2Field;	//����ó ��
	@FXML TextField postField;		//�����ȣ
	@FXML TextArea addressArea;		//�ּ�
	
	@FXML ComboBox<String> questionComboBox;	//��� ����
	@FXML ComboBox<String> phoneComboBox;		//����ó ��
	@FXML ComboBox<String> sexComboBox;			//����	
	
	ObservableList<String> qusoption = FXCollections.observableArrayList("�¾ ���� ����Դϱ�?", "��Ӵ��� ������ �����Դϱ�?",
			"�ƹ����� ������ �����Դϱ�?", "���� ����־��� �������� ����Դϱ�?", "��� ����б��� ����Դϱ�?", "�����ϴ� ������ �����Դϱ�?");
	ObservableList<String> phoneoption = FXCollections.observableArrayList("010", "011", "016", "017", "019");
	ObservableList<String> sexoption = FXCollections.observableArrayList("��", "��");
	
	boolean ID_CHECK = false;
	private SceneManager sManager;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
 
 
 
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}
	
	public void startListener() {
		 new Listener().start();
	}
	
	public void INIT_CONTROLLER(SceneManager manager, ObjectInputStream fromServer, ObjectOutputStream toServer) {
		this.sManager = manager;
		this.fromServer = fromServer;
		this.toServer = toServer;
		
		//�޺��ڽ��� �־� �����ϴ� ���� 
		questionComboBox.setItems(qusoption);
		phoneComboBox.setItems(phoneoption);
		sexComboBox.setItems(sexoption);
	}
	
	//�ߺ��˻�
	@SuppressWarnings("unchecked")
	@FXML
	private void onDuplicate() {
		if(!idField.getText().equals("")) {		//�� ���̵� ������� ������ 
			
			if(idField.getText().length() <= 5 || idField.getText().length() > 20) { //���̵� 5�ھƷ��ų� 20�� �ʰ��̸�
				CustomDialog.showConfirmDialog("���̵�� 6~20 �ڷ� �����մϴ�.", sManager.getStage());
			
			}else {
				JSONObject request = new JSONObject();
				request.put("type", NetProtocols.ID_DUP_CHECK_REQUEST);
				request.put("id", idField.getText());
				sendProtocol(request);
			}
			
		}else {		//�� ���̵� �����̸� 
			CustomDialog.showConfirmDialog("���̵� �Է����ּ���", sManager.getStage());
		}
	}
	
 
	//��ҹ�ư�� �α��� ȭ������ �Ѿ��
	@FXML
	private void onCancle() {
		System.out.println("��ҹ�ư Ŭ��");
		sManager.changeListenController("MEMBER_JOIN");
		sManager.changeScene(Setting.loginproc_fxml);
	}
	
	//Ȯ�ι�ư�� 
	@SuppressWarnings("unchecked")
	@FXML
	private void onSuccess() {
		 
		while(true) {
			if(!ID_CHECK) { 		//���̵� �ߺ�üũ�� �������� 
				CustomDialog.showConfirmDialog("���̵� �ߺ�Ȯ���� ���ּ���", sManager.getStage());
				break;	
			}
			String id = idField.getText();
			String password = pwdField.getText();
			String repassword = rePwdField.getText();
			
			if(password.equals("")) {
				CustomDialog.showConfirmDialog("��й�ȣ�� �Է� ���ּ���.", sManager.getStage());
				break;
			
			}else if(!password.equals(repassword) || repassword.equals("")) {
				CustomDialog.showConfirmDialog("��й�ȣ Ȯ���� ���� �ʽ��ϴ�.", sManager.getStage());
				break;
			}
			
			int selectqus = questionComboBox.getSelectionModel().getSelectedIndex()+1;
			if(selectqus == 0) {
				CustomDialog.showConfirmDialog("��������� �����ϼ���.", sManager.getStage());
				break;
			}
			
			String qusanswer = qAnswerField.getText();
			if(qusanswer.equals("")) {
				CustomDialog.showConfirmDialog("������ �亯�ϼ���", sManager.getStage());
				break;
			}
			
			String name = nameField.getText();
			if (name.equals("")) {
				CustomDialog.showConfirmDialog("������ �Է� �� �ּ���.", sManager.getStage());
				break;
			}
			
			int sex = sexComboBox.getSelectionModel().getSelectedIndex() + 1;
			if (sex == 0) {
				CustomDialog.showConfirmDialog("������ �����Ͽ� �ּ���.", sManager.getStage());
				break;
			}
			
			String birthday = birthField.getText();
			if(birthday.equals("")) {
				CustomDialog.showConfirmDialog("������ϸ� �Է��ϼ���", sManager.getStage());
				break;
			}else if(!(birthday.length() == 8)) {
				CustomDialog.showConfirmDialog("������� ��������� ��������. 8�ڸ� ex)19800102", sManager.getStage());
				break;
			}
			
			String email = emailField.getText();
			if(email.equals("")) {
				CustomDialog.showConfirmDialog("�̸����� �Է� �� �ּ���.", sManager.getStage());
				break;
			}
			
			int phonecheck = phoneComboBox.getSelectionModel().getSelectedIndex()+1;
			String phone1 = phone1Field.getText();
			String phone2 = phone2Field.getText();
			if(phonecheck == 0 || phone1.equals("") || phone2.equals("")) {
				CustomDialog.showConfirmDialog("����ó�� ��Ȯ�� �Է� �� �ּ���.", sManager.getStage());
				break;
			}
			String phonenum = phoneComboBox.getSelectionModel().getSelectedItem()+"-"+phone1+"-"+phone2;
			
			String post = postField.getText();
			if(post.equals("")) {
				CustomDialog.showConfirmDialog("�����ȣ �˻��� ��������.", sManager.getStage());
				break;
			}
			
			String address = addressArea.getText();
			if(address.equals("")) {
				CustomDialog.showConfirmDialog("�����ȣ �˻��� ��������.", sManager.getStage());
				break;
			}
			
			System.out.println("----------ȸ������ ������");
			JSONObject json = Toolbox.createJSONProtocol(NetProtocols.MEMBER_JOIN_REQUEST);
			json.put("���̵�", id); json.put("��й�ȣ", password);
			json.put("����", selectqus); json.put("�亯", qusanswer);
			json.put("�̸�", name); json.put("����", sex);
			json.put("�������", birthday); json.put("�̸���", email);
			json.put("����ó", phonenum); json.put("�����ȣ", post);
			json.put("�ּ�", address);
			
			sendProtocol(json);
			break;
		}
	}
	 
	@FXML
	//�����ȣ �˻��� Ȯ���� ������ db�� ������ �Ͽ� �ҷ��´�.
	//���� ��� ������ �ʰ� ���ε� ������ �ۼ��� �Է��Ͽ���.
	private void oninputaddress() {
		Connection conn;
		Statement stmt;
		ResultSet rs;
		try {

			Class.forName("org.gjt.mm.mysql.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/����", "root", "qwer");
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select post, address from selectdata where code=1");

			while (rs.next()) {
				postField.setText(rs.getString("post"));
				addressArea.setText(rs.getString("address"));
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
 
	
	@FXML
	// �����ȣ �˻� â ���� 
	private void onPostAddress() {
		ZipcodeApp zi = new ZipcodeApp();
		Stage primaryStage = new Stage();
		try {
			zi.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
 
	}
	
	
	//JSon�� ������ ���� ������ 
	private void sendProtocol(JSONObject protocol) {
		try {
			toServer.writeObject(protocol);
			toServer.flush();	
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	class Listener extends Thread {
		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			try {
				while(true) {
					JSONObject respond = null;
					try {
						respond = (JSONObject) fromServer.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
					String type = respond.get("type").toString();
					if(type.equals(NetProtocols.ID_DUP_RESPOND_OK)) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								CustomDialog.showConfirmDialog("�������� ���̵� �Դϴ�", sManager.getStage());
								ID_CHECK = true;
							}
						});
						
					} else if(type.equals(NetProtocols.ID_DUP_RESPOND_DENY)) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								CustomDialog.showConfirmDialog("�̹� ��ϵ�  ���̵� �Դϴ�", sManager.getStage());
							}
						});
						
					} else if(type.equals(NetProtocols.EXIT_RESPOND)) {
						break;
					
					} else if(type.equals(NetProtocols.RECIEVE_READY)) {
						JSONObject protocol = new JSONObject();
						protocol.put("type", NetProtocols.RECIEVE_READY_OK);
						sendProtocol(protocol);
						
					} else if(type.equals(NetProtocols.MEMBER_JOIN_RESPOND)) {
						Platform.runLater(new Runnable() {						
							@Override
							public void run() {
								CustomDialog.showConfirmDialog("����� �Ϸ� �Ǿ����ϴ�.", sManager.getStage());
								sManager.changeListenController("MEMBER_JOIN");
								sManager.changeScene(Setting.loginproc_fxml);
							}
						});
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}			
			System.out.println("MemberJoin ������ ������ ����");
		}
	}
	
 









}
