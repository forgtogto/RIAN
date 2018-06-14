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
				System.out.println("[Server] ���� ����");
				controlApp.Log("M", "Start server." + InetAddress.getLocalHost().getHostAddress() + "::" + 8080);

				if (new File(Setting.DEFAULT_RIAN_DIRECTORY).exists() == false) {

					// ���� �ۿ� �α� ������ ���ݴϴ�.
					System.out.println("[Server] ���丮�� �ڵ����� �����մϴ�.");
					controlApp.Log("M", "Create required directorys");
					System.out.println("[Server] ���� : " + Setting.DEFAULT_RIAN_DIRECTORY);
					controlApp.Log("M", "Path::" + Setting.DEFAULT_RIAN_DIRECTORY);
					new File(Setting.DEFAULT_RIAN_DIRECTORY).mkdir();
					System.out.println("[Server] ���� : " + Setting.DEFAULT_MOVIE_DATA_DIRECTORY);
					controlApp.Log("M", "Path::" + Setting.DEFAULT_MOVIE_DATA_DIRECTORY);
					new File(Setting.DEFAULT_MOVIE_DATA_DIRECTORY).mkdir();
					System.out.println("[Server] ���� : " + Setting.DEFAULT_USER_DATA_DIRECTORY);
					controlApp.Log("M", "Path::" + Setting.DEFAULT_USER_DATA_DIRECTORY);
					new File(Setting.DEFAULT_USER_DATA_DIRECTORY).mkdir();
					System.out.println("[Server] ���� : " + Setting.DEFAULT_SUBMITTED_DATA_DIRECTORY);
					controlApp.Log("M", "Path::" + Setting.DEFAULT_SUBMITTED_DATA_DIRECTORY);
					new File(Setting.DEFAULT_SUBMITTED_DATA_DIRECTORY).mkdir();
					controlApp.Log("M", "Path::" + Setting.DEFAULT_LOG_BACKUP_PATH);
					new File(Setting.DEFAULT_LOG_BACKUP_PATH).mkdir();
				}
			}
			handler = new DatabaseHandler();

			if (PRINT_LOG) {
				System.out.println("[Server] �����ͺ��̽��� ���� �õ�...");
			}
			controlApp.Log("M", "Try to connect database. Use::" + Setting.DASEBASE_DRIVER);
			int result = handler.connect();

			// ���� ������ ���� ���� ����ġ��
			switch (result) {
			case DatabaseHandler.DRIVER_INIT_ERROR:
				if (PRINT_LOG)
					System.out.println("[Server] JDBC����̹� ������ �߸��ƽ��ϴ�.");
				controlApp.Log("E", "Fail to set up JDBC Driver.");
				controlApp.off();
				return;
			case DatabaseHandler.LOGIN_FAIL_ERROR:
				if (PRINT_LOG)
					System.out.println("[Server] �����ͺ��̽��� �α������� ���߽��ϴ�. ���̵� �Ǵ� ��й�ȣ�� Ȯ���ϼ���");
				controlApp.Log("E", "Cannot login to database");
				controlApp.off();
				return;
			case DatabaseHandler.COMPLETE:
				if (PRINT_LOG)
					System.out.println("[Server] ���� ����");
				SERVER_RUN = true;
				controlApp.Log("M", "Connection commit.");
				controlApp.on();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (PRINT_LOG)
			System.out.println("[Server] Waiter ������ ����");
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
						System.out.println("\t[Waiter] Ŭ���̾�Ʈ�� ��ٸ��� ���Դϴ�...");
					controlApp.Log("W", "Waiting client...");
					Socket newClient = server.accept();
					controlApp.Log("W", "New client has connected, start connector::"
							+ newClient.getInetAddress().getHostAddress());
					if (PRINT_LOG)
						System.out.println("\t[Waiter] ���ο� Ŭ���̾�Ʈ ����, Connector ������ ����");
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
				if(PRINT_LOG)System.out.println("\t\t[Connector-"+userName+"] ��Ʈ�� ���� �Ϸ�");
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
						System.out.println("��û : " + request);
						if (request == null) {
							if (PRINT_LOG)
								System.out.println("\t\t[" + userName + "] ����� ����, ������ ����");
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
					//������ �ۼ��� �ð��Դϴ�. 
					
					if (type.equals(NetProtocols.LOGIN_REQUEST)) {
						String reqID = request.get("id").toString();
						String reqPassword = request.get("password").toString();

						if (PRINT_LOG)
							System.out.println("\t\t\t[request-" + userName + "] LOGIN_REQUEST, ID : " + reqID
									+ ", Password : " + reqPassword);
						controlApp.request("LOGIN_REQUEST", reqID + "-" + clientIP, "-----", request);
						ResultSet s = handler.excuteQuery("select * from ����� where �й�='" + reqID + "'");

						if (s.next()) {
							String realPassword = s.getString("��й�ȣ");
							if (realPassword.equals(reqPassword)) {
								userName = s.getString("�̸�");
								userIdentify = reqID;
								userGrade = s.getString("����ڵ��");
								if (PRINT_LOG)
									System.out.println("\t\t\t[Server] LOGIN_ACCEPT");
								JSONObject respond = new JSONObject();
								respond.put("type", NetProtocols.LOGIN_ACCEPT);
								respond.put("user_level", s.getString("����ڵ��"));
								sendProtocol(respond);
								controlApp.respond("LOGIN_RESPOND", userIdentify + "-" + clientIP, "COMMIT", respond);
								/* �� �������� HashMap�� �־������ */
								clients.add(new ConnectedClient(userIdentify, userName, s.getString("����ڵ��").toString(),
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
				if(PRINT_LOG)System.out.println("\t\t[Connector-"+userName+"] ����� ���� ����, ������ ����");
				controlApp.Log("C", "Disconnect a client::"+clientIP);
			} finally {
				try {
					fromClient.close();
					toClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				controlApp.disconnect(clientIP);
				System.out.println("Ŭ���̾�Ʈ ������ ����");
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
