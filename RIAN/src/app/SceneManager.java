package app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.orsoncharts.util.json.JSONObject;

import app.controllers.LoginController;
import app.controllers.MemberJoinController;
import app.controllers.PasswordFindController;
import devices.NetProtocols;
import devices.Setting;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

	// 스테이지와 스트림 지정
	private Stage stage;
	private Socket socket;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
	private boolean FULLSCREEN_MODE;
	private Application clientHost;
	
	public SceneManager(Stage stage) {
		 this.stage = stage;
		 this.FULLSCREEN_MODE = false;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public void setHost(Application host) {
		this.clientHost = host;
	}
	
	public Application getHost() {
		return clientHost;
	}
	
	//소켓을 이용하여 서버프로그램 연결
	public int connectToServer() {
		try {
			socket = new Socket(Setting.SERVER_IP_ADDRESS, Setting.SERVER_PORT_NUMBER);
			toServer = new ObjectOutputStream(socket.getOutputStream());
			fromServer = new ObjectInputStream(socket.getInputStream());		
		} catch (UnknownHostException e) {
			return Setting.UNKNOWN_HOST_ERROR;	
		} catch (Exception e) {
			return Setting.UNKNOWN_HOST_ERROR;
		}
		return Setting.COMMIT;
	}
	
	public void changeScene(String scene) {		// String scene 뭐하는 씬인지 받아옴

		FXMLLoader loader = new FXMLLoader(getClass().getResource(scene)); 
		System.out.println(scene);
		System.out.println();
		try {
			final Parent root = loader.load();
			if(!Platform.isFxApplicationThread()) {		//프로그램이 동작되지 않으면!
				Platform.runLater(new Runnable() {		// 실행
					
					@Override
					public void run() {
						stage.setResizable(true);
						stage.setScene(new Scene(root));
						stage.setFullScreen(FULLSCREEN_MODE);
						stage.setResizable(false);
					}
				});
			}else {
				stage.setResizable(true);
				stage.setScene(new Scene(root));
				stage.setFullScreen(FULLSCREEN_MODE);
				stage.setResizable(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 화면 별로 처리할 작업을 정의 해둔다 //
		//////////////////////////////
		if(scene.equals(Setting.loginproc_fxml)) {
			toLoginProcWindow(loader); //1번
			
		}else if(scene.equals(Setting.memberjoin_fxml)) {
			toMemberJoinWindow(loader); //2번
		}
		else if (scene.equals(Setting.password_find_fxml)) {
			toPassFindWindow(loader); //3번 
		}
		//향후 페이지를 추가해 나간다
		
	}
	
	public void doFullscreen(boolean FULLSCREEN_MODE) {
		this.FULLSCREEN_MODE = FULLSCREEN_MODE;
	}
	

	//1번 
	private void toLoginProcWindow(FXMLLoader loader) {
		stage.setTitle(Setting.loginproc_title);			//타이틀지정
		LoginController control = loader.getController();	//컨트롤러탑재
		control.INIT_CONTROLLER(this, fromServer, toServer);
		control.startListener();							//리스너 스레드 동작
	}
	
	//2번
	private void toMemberJoinWindow(FXMLLoader loader) {
		stage.setTitle(Setting.memberjoin_title);
		MemberJoinController control = loader.getController();
		control.INIT_CONTROLLER(this, fromServer, toServer);
		control.startListener();		
	}
	
	//3번
	private void toPassFindWindow(FXMLLoader loader)
	{
		stage.setTitle(Setting.password_find_title);
		PasswordFindController control = loader.getController();
		control.INIT_CONTROLLER(this, fromServer, toServer);
		control.startListener();
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public void changeListenController(String name) {
		JSONObject request = new JSONObject();
		request.put("type", NetProtocols.EXIT_REQUEST);
		request.put("name", name);
		try {
			toServer.writeObject(request);		//담아서 서버로 보내기
			toServer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
