package server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.orsoncharts.util.json.JSONObject;

import database.DatabaseHandler;
import devices.NetProtocols;
import devices.Setting;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;


public class RIAN_Server {

	private ServerSocket server;
	private static boolean SERVER_RUN = false;
	private boolean PRINT_LOG = true;
	private DatabaseHandler handler;
	private ArrayList<ConnectedClient> clients;
	// FileServer fileServer;
	ServerMainController controlApp;
	ListView<HBox> logs, requests, connected;

	public RIAN_Server(ServerMainController controlApp) {
		clients = new ArrayList<ConnectedClient>();
		this.controlApp = controlApp;
	}

	public void serverClose() {
		try {
			// fileServer.serverClose();
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SERVER_RUN = false;
	}

	public void serverOpen() {
		try {
			server = new ServerSocket(8080);
			if (PRINT_LOG) {
				System.out.println("[Server] 서버 오픈");
				controlApp.Log("M", "Start server." + InetAddress.getLocalHost().getHostAddress() + "::" + 8080);

				if (new File(Setting.DEFAULT_RIAN_DIRECTORY).exists() == false) {

					// 서버 앱에 로그 찍히게 해줍니다.
					System.out.println("[Server] 디렉토리를 자동으로 생성합니다.");
					controlApp.Log("M", "Create required directorys");
					System.out.println("[Server] 생성 : " + Setting.DEFAULT_RIAN_DIRECTORY);
					controlApp.Log("M", "Path::" + Setting.DEFAULT_RIAN_DIRECTORY);
					new File(Setting.DEFAULT_RIAN_DIRECTORY).mkdir();
					System.out.println("[Server] 생성 : " + Setting.DEFAULT_MOVIE_DATA_DIRECTORY);
					controlApp.Log("M", "Path::" + Setting.DEFAULT_MOVIE_DATA_DIRECTORY);
					new File(Setting.DEFAULT_MOVIE_DATA_DIRECTORY).mkdir();
					System.out.println("[Server] 생성 : " + Setting.DEFAULT_USER_DATA_DIRECTORY);
					controlApp.Log("M", "Path::" + Setting.DEFAULT_USER_DATA_DIRECTORY);
					new File(Setting.DEFAULT_USER_DATA_DIRECTORY).mkdir();
					System.out.println("[Server] 생성 : " + Setting.DEFAULT_SUBMITTED_DATA_DIRECTORY);
					controlApp.Log("M", "Path::" + Setting.DEFAULT_SUBMITTED_DATA_DIRECTORY);
					new File(Setting.DEFAULT_SUBMITTED_DATA_DIRECTORY).mkdir();
					controlApp.Log("M", "Path::" + Setting.DEFAULT_LOG_BACKUP_PATH);
					new File(Setting.DEFAULT_LOG_BACKUP_PATH).mkdir();
				}
			}
			handler = new DatabaseHandler();

			if (PRINT_LOG) {
				System.out.println("[Server] 데이터베이스와 연결 시도...");
			}
			controlApp.Log("M", "Try to connect database. Use::" + Setting.DASEBASE_DRIVER);
			int result = handler.connect();

			// 설정 에러시 나올 문구 스위치문
			switch (result) {
			case DatabaseHandler.DRIVER_INIT_ERROR:
				if (PRINT_LOG)
					System.out.println("[Server] JDBC드라이버 설정이 잘못됐습니다.");
				controlApp.Log("E", "Fail to set up JDBC Driver.");
				controlApp.off();
				return;
			case DatabaseHandler.LOGIN_FAIL_ERROR:
				if (PRINT_LOG)
					System.out.println("[Server] 데이터베이스에 로그인하지 못했습니다. 아이디 또는 비밀번호를 확인하세요");
				controlApp.Log("E", "Cannot login to database");
				controlApp.off();
				return;
			case DatabaseHandler.COMPLETE:
				if (PRINT_LOG)
					System.out.println("[Server] 연결 성공");
				SERVER_RUN = true;
				controlApp.Log("M", "Connection commit.");
				controlApp.on();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (PRINT_LOG)
			System.out.println("[Server] Waiter 스레드 시작");
		new Thread(() -> {
			controlApp.Log("M", "Start waiting clients.");
		}).start();
		new Waiter().start();
		//fileServer = new FileServer(9090, controlApp);
		controlApp.Log("F", "Start file handling server.");
	}
	
	class Waiter extends Thread {
		@Override
		public void run() {
			try {
				while (SERVER_RUN) {
					if (PRINT_LOG)
						System.out.println("\t[Waiter] 클라이언트를 기다리는 중입니다...");
					controlApp.Log("W", "Waiting client...");
					Socket newClient = server.accept();
					controlApp.Log("W", "New client has connected, start connector::"
							+ newClient.getInetAddress().getHostAddress());
					if (PRINT_LOG)
						System.out.println("\t[Waiter] 새로운 클라이언트 접속, Connector 스레드 실행");
					new Connector(newClient).start();
				}
			} catch (IOException e) {
				controlApp.Log("E", e.getMessage());
			} finally {
				SERVER_RUN = false;
			}
		}
	}
	
	class Connector extends Thread {
		
		Socket client;
		String userName = "unknown";
		String userIdentify = "";
		String userGrade = "";
		ObjectInputStream fromClient;
		ObjectOutputStream toClient;
		String clientIP = "";
 
		public Connector(Socket client) {
			this.client = client;
			clientIP = client.getInetAddress().getHostAddress();
			controlApp.Log("C", "Client IP Adress::"+clientIP);
			System.out.println(clientIP);
			
			try {
				fromClient = new ObjectInputStream(client.getInputStream());
				toClient = new ObjectOutputStream(client.getOutputStream());
				if(PRINT_LOG)System.out.println("\t\t[Connector-"+userName+"] 스트림 연결 완료");
				controlApp.Log("C", "Connect complete to "+userName+"-"+clientIP);
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			try {
				while(SERVER_RUN) {
					
					JSONObject request = null;
					try {
						request = (JSONObject) fromClient.readObject();
						System.out.println("요청 : " + request);
						if (request == null) {
							if (PRINT_LOG)
								System.out.println("\t\t[" + userName + "] 사용자 종료, 스레드 종료");
							break;
						}

					} catch (ClassNotFoundException e) {
						JSONObject respond = new JSONObject();
						respond.put("type", NetProtocols.INVALID_REQUEST_ERROR);
						sendProtocol(respond);
						continue;
					}
					
					String type = request.get("type").toString();
					if(PRINT_LOG)System.out.println("\t\t["+userName+"] request type : "+type);
				
					////////////////////////////////////////////////////////////////////		
					//쿼리문 작성의 시간입니다. 
					
					if (type.equals(NetProtocols.LOGIN_REQUEST)) {
						String reqID = request.get("id").toString();
						String reqPassword = request.get("password").toString();

						if (PRINT_LOG)
							System.out.println("\t\t\t[request-" + userName + "] LOGIN_REQUEST, ID : " + reqID
									+ ", Password : " + reqPassword);
						controlApp.request("LOGIN_REQUEST", reqID + "-" + clientIP, "-----", request);
						ResultSet s = handler.excuteQuery("select * from 사용자 where 학번='" + reqID + "'");

						if (s.next()) {
							String realPassword = s.getString("비밀번호");
							if (realPassword.equals(reqPassword)) {
								userName = s.getString("이름");
								userIdentify = reqID;
								userGrade = s.getString("사용자등급");
								if (PRINT_LOG)
									System.out.println("\t\t\t[Server] LOGIN_ACCEPT");
								JSONObject respond = new JSONObject();
								respond.put("type", NetProtocols.LOGIN_ACCEPT);
								respond.put("user_level", s.getString("사용자등급"));
								sendProtocol(respond);
								controlApp.respond("LOGIN_RESPOND", userIdentify + "-" + clientIP, "COMMIT", respond);
								/* 이 시점에서 HashMap에 넣어줘야함 */
								clients.add(new ConnectedClient(userIdentify, userName, s.getString("사용자등급").toString(),
										toClient));
								controlApp.connect(clientIP, userName, userGrade, "login");
							} else {
								if (PRINT_LOG)
									System.out.println("\t\t\t[Server] LOGIN_DENY, Incorrect Password, requset : "
											+ reqPassword + ", in database : " + realPassword);
								JSONObject respond = new JSONObject();
								respond.put("type", NetProtocols.LOGIN_DENY);
								controlApp.respond("LOGIN_RESPOND", userIdentify + "-" + clientIP, "DENY", respond);
								sendProtocol(respond);
							}
						} else {
							if (PRINT_LOG)
								System.out.println("\t\t\t[Server] LOGIN_DENY, Not Exist User requset : " + reqID);
							JSONObject respond = new JSONObject();
							respond.put("type", NetProtocols.LOGIN_DENY);
							controlApp.respond("LOGIN_RESPOND", userIdentify + "-" + clientIP, "DENY", respond);
							sendProtocol(respond);
						}
						s.close();
					}
				
				
				
			////////////////////////////////////////////////////////////////////			
				}				
			} catch (IOException|SQLException e) {
				e.printStackTrace();
				if(PRINT_LOG)System.out.println("\t\t[Connector-"+userName+"] 사용자 접속 종료, 스레드 종료");
				controlApp.Log("C", "Disconnect a client::"+clientIP);
			} finally {
				try {
					fromClient.close();
					toClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				controlApp.disconnect(clientIP);
				System.out.println("클라이언트 스레드 종료");
			}
		}
		
		public void sendProtocol(JSONObject protocol) throws IOException {
			synchronized (toClient) {
				toClient.writeObject(protocol);
				toClient.flush();
			}
		}
	}
	
	class Controller extends Thread {
		@Override
		public void run() {
			
			@SuppressWarnings("resource")
			java.util.Scanner sc = new java.util.Scanner(System.in);
			String command = "";

			System.out.println("RIAN Server Controller");
			System.out.println("/? or help - show command list, start server - Run server, exit - exit program");
			while (true) {
				System.out.print("Input >> ");
				command = sc.nextLine();

				if (command.equals("exit")) {
					System.exit(0);
				} else if (command.equals("/?") || command.equals("help")) {
					System.out.println("print help list");
				} else if (command.equals("set PRINT_LOG = true")) {
					PRINT_LOG = true;
				} else if (command.equals("set PRINT_LOG = false")) {
					PRINT_LOG = false;
				} else if (command.equals("show clients")) {
					System.out.println("Current connected clients");
					System.out.println("Clients count : " + clients.size());
					for (ConnectedClient c : clients) {
						System.out.println("ClientID   : " + c.getClientID());
						System.out.println("ClientName : " + c.getClientName());
					}
				} else if (command.equals("start server")) {
					serverOpen();
				} else {
					System.out.println("Invalid Command");
				}
			}
		}
	}

	public static void main(String[] args) {
		
	}
}
