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
	// 아이디 / 비밀번호 / 비번확인/ 비번 질문/ 비번정답/ 이름/ 성별/ 생년월일
	// 이메일 / 우편번호 // 주소 
	//버튼 중복확인 등록 취소 
	
	@FXML TextField idField;		//아이디
	@FXML TextField pwdField;		//비번
	@FXML TextField rePwdField;		//비번확인
	@FXML TextField qAnswerField;	//비번질문
	@FXML TextField nameField;		//이름	
	@FXML TextField birthField;		//생년월일
	@FXML TextField emailField;		//이메일
	@FXML TextField phone1Field;	//연락처 중간
	@FXML TextField phone2Field;	//연락처 뒤
	@FXML TextField postField;		//우편번호
	@FXML TextArea addressArea;		//주소
	
	@FXML ComboBox<String> questionComboBox;	//비번 질문
	@FXML ComboBox<String> phoneComboBox;		//연락처 앞
	@FXML ComboBox<String> sexComboBox;			//성별	
	
	ObservableList<String> qusoption = FXCollections.observableArrayList("태어난 곳은 어디입니까?", "어머니의 성함은 무엇입니까?",
			"아버지의 성함은 무엇입니까?", "가장 재미있었던 여행지는 어디입니까?", "출신 고등학교는 어디입니까?", "좋아하는 음식은 무었입니까?");
	ObservableList<String> phoneoption = FXCollections.observableArrayList("010", "011", "016", "017", "019");
	ObservableList<String> sexoption = FXCollections.observableArrayList("남", "여");
	
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
		
		//콤보박스에 넣어 실행하는 형태 
		questionComboBox.setItems(qusoption);
		phoneComboBox.setItems(phoneoption);
		sexComboBox.setItems(sexoption);
	}
	
	//중복검사
	@SuppressWarnings("unchecked")
	@FXML
	private void onDuplicate() {
		if(!idField.getText().equals("")) {		//쓴 아이디가 비어있지 않으면 
			
			if(idField.getText().length() <= 5 || idField.getText().length() > 20) { //아이디가 5자아래거나 20자 초과이면
				CustomDialog.showConfirmDialog("아이디는 6~20 자로 가능합니다.", sManager.getStage());
			
			}else {
				JSONObject request = new JSONObject();
				request.put("type", NetProtocols.ID_DUP_CHECK_REQUEST);
				request.put("id", idField.getText());
				sendProtocol(request);
			}
			
		}else {		//쓴 아이디가 공란이면 
			CustomDialog.showConfirmDialog("아이디를 입력해주세요", sManager.getStage());
		}
	}
	
 
	//취소버튼시 로그인 화면으로 넘어가기
	@FXML
	private void onCancle() {
		System.out.println("취소버튼 클릭");
		sManager.changeListenController("MEMBER_JOIN");
		sManager.changeScene(Setting.loginproc_fxml);
	}
	
	//확인버튼시 
	@SuppressWarnings("unchecked")
	@FXML
	private void onSuccess() {
		 
		while(true) {
			if(!ID_CHECK) { 		//아이디 중복체크를 안했으면 
				CustomDialog.showConfirmDialog("아이디 중복확인을 해주세요", sManager.getStage());
				break;	
			}
			String id = idField.getText();
			String password = pwdField.getText();
			String repassword = rePwdField.getText();
			
			if(password.equals("")) {
				CustomDialog.showConfirmDialog("비밀번호를 입력 해주세요.", sManager.getStage());
				break;
			
			}else if(!password.equals(repassword) || repassword.equals("")) {
				CustomDialog.showConfirmDialog("비밀번호 확인이 맞지 않습니다.", sManager.getStage());
				break;
			}
			
			int selectqus = questionComboBox.getSelectionModel().getSelectedIndex()+1;
			if(selectqus == 0) {
				CustomDialog.showConfirmDialog("질문양식을 선택하세요.", sManager.getStage());
				break;
			}
			
			String qusanswer = qAnswerField.getText();
			if(qusanswer.equals("")) {
				CustomDialog.showConfirmDialog("질문에 답변하세요", sManager.getStage());
				break;
			}
			
			String name = nameField.getText();
			if (name.equals("")) {
				CustomDialog.showConfirmDialog("성함을 입력 해 주세요.", sManager.getStage());
				break;
			}
			
			int sex = sexComboBox.getSelectionModel().getSelectedIndex() + 1;
			if (sex == 0) {
				CustomDialog.showConfirmDialog("성별을 선택하여 주세요.", sManager.getStage());
				break;
			}
			
			String birthday = birthField.getText();
			if(birthday.equals("")) {
				CustomDialog.showConfirmDialog("생년월일를 입력하세요", sManager.getStage());
				break;
			}else if(!(birthday.length() == 8)) {
				CustomDialog.showConfirmDialog("공백없이 생년월일을 넣으세요. 8자리 ex)19800102", sManager.getStage());
				break;
			}
			
			String email = emailField.getText();
			if(email.equals("")) {
				CustomDialog.showConfirmDialog("이메일을 입력 해 주세요.", sManager.getStage());
				break;
			}
			
			int phonecheck = phoneComboBox.getSelectionModel().getSelectedIndex()+1;
			String phone1 = phone1Field.getText();
			String phone2 = phone2Field.getText();
			if(phonecheck == 0 || phone1.equals("") || phone2.equals("")) {
				CustomDialog.showConfirmDialog("연락처를 정확히 입력 해 주세요.", sManager.getStage());
				break;
			}
			String phonenum = phoneComboBox.getSelectionModel().getSelectedItem()+"-"+phone1+"-"+phone2;
			
			String post = postField.getText();
			if(post.equals("")) {
				CustomDialog.showConfirmDialog("우편번호 검색을 누르세요.", sManager.getStage());
				break;
			}
			
			String address = addressArea.getText();
			if(address.equals("")) {
				CustomDialog.showConfirmDialog("우편번호 검색을 누르세요.", sManager.getStage());
				break;
			}
			
			System.out.println("----------회원가입 겸색결과");
			JSONObject json = Toolbox.createJSONProtocol(NetProtocols.MEMBER_JOIN_REQUEST);
			json.put("아이디", id); json.put("비밀번호", password);
			json.put("질문", selectqus); json.put("답변", qusanswer);
			json.put("이름", name); json.put("성별", sex);
			json.put("생년월일", birthday); json.put("이메일", email);
			json.put("연락처", phonenum); json.put("우편번호", post);
			json.put("주소", address);
			
			sendProtocol(json);
			break;
		}
	}
	 
	@FXML
	//우편번호 검색후 확인을 누르면 db에 저장을 하여 불러온다.
	//서버 디비에 얹히지 않고 별로도 쿼리문 작성후 입력하였다.
	private void oninputaddress() {
		Connection conn;
		Statement stmt;
		ResultSet rs;
		try {

			Class.forName("org.gjt.mm.mysql.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/리안", "root", "qwer");
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
	// 우편번호 검색 창 띄우기 
	private void onPostAddress() {
		ZipcodeApp zi = new ZipcodeApp();
		Stage primaryStage = new Stage();
		try {
			zi.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
 
	}
	
	
	//JSon에 담긴것을 보냄 서버로 
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
								CustomDialog.showConfirmDialog("생성가능 아이디 입니다", sManager.getStage());
								ID_CHECK = true;
							}
						});
						
					} else if(type.equals(NetProtocols.ID_DUP_RESPOND_DENY)) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								CustomDialog.showConfirmDialog("이미 등록된  아이디 입니다", sManager.getStage());
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
								CustomDialog.showConfirmDialog("등록이 완료 되었습니다.", sManager.getStage());
								sManager.changeListenController("MEMBER_JOIN");
								sManager.changeScene(Setting.loginproc_fxml);
							}
						});
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}			
			System.out.println("MemberJoin 리스너 스레드 종료");
		}
	}
	
 









}
