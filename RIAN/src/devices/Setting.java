package devices;

public class Setting {
	
	// Application ���� 
	public static final int COMMIT = 0;	
	public static final int ERROR = -2;
	
	//UI ���� 
		// UI FXML���� �̸� ����
		public static final String loginproc_fxml = "/app/ui/LoginProc.fxml";
		public static final String memberjoin_fxml = "/app/ui/MemberJoin.fxml";
		public static final String password_find_fxml = "/app/ui/PasswordFind.fxml";

		// UI DIALOG ���� �̸� ���� 
		public static final String alert_dialog = "/app/ui/AlertDialog.fxml";
		public static final String ok_cancel_dialog = "/app/ui/OkCancelDialog.fxml";
		public static final String input_dialog = "/app/ui/InputDialog.fxml";
		
		
		public static final String test_fxml = "/app/ui/test.fxml";
		public static final String test2_fxml = "/app/ui/test2.fxml";
	
		
		// UI ������â �̸� ���
		public static final String loginproc_title = "RAIN - �α���";
		public static final String memberjoin_title = "RAIN - ����� ���";
		public static final String alert_dialog_title = "RAIN - �˸�";
		public static final String password_find_title = "RAIN - ��й�ȣ ã��";
		public static final String ok_cancel_dialog_title = "RAIN - Ȯ��";
		public static final String input_dialog_title = "RAIN - �Է��ϼ���.";
		
		
		
	// ��Ʈ��ũ ����_��������/���ϼ�������
	public static String SERVER_IP_ADDRESS = "localhost";
	public static final int SERVER_PORT_NUMBER = 8080;
	
	public static String FILE_SERVER_IP_ADDRESS = "localhost";
	public static final int FILE_SERVER_PORT_NUMBER = 9090;
		// ��Ʈ��ũ ���� ǥ�� 
		public static final int CONNECT_ERROR = -1;							// ���� ���� ����
		public static final int UNKNOWN_HOST_ERROR = -3;
		
	/* Server */	
	public static String DEFAULT_LOG_BACKUP_PATH = "C:\\RIAN\\Backup\\";
	
	/* Database */
	public static final String DASEBASE_DRIVER = "org.gjt.mm.mysql.Driver";	// �⺻ �����ͺ��̽� ����̹� ��Ű��
	public static final String DEFAULT_USE_DATABASE = "����";				// �⺻ ��� �����ͺ��̽�
	public static final String DEFAULT_DATABASE_HOST_ID = "root";			// �⺻ �����ͺ��̽� ���� ���̵�
	public static final String DEFAULT_DATABASE_HOST_PASSWORD = "qwer";		// �⺻ �����ͺ��̽� ���� ��й�ȣ
	
	/* User Data */
	public static final String DEFAULT_USER_DATA_DIRECTORY = "C:\\\\RIAN\\\\Userdata\\\\";	// �������̹���
	public static final String DEFAULT_MOVIE_DATA_DIRECTORY = "C:\\\\RIAN\\\\Movie\\\\";	// ����
	public static final String DEFAULT_SUBMITTED_DATA_DIRECTORY = "C:\\\\RIAN\\\\SubmittedData\\\\";	// ���ⵥ����
	public static final String DEFAULT_RIAN_DIRECTORY = "C:\\\\RIAN\\\\";
	public static final String DEFAULT_DOWNLOAD_DIRECTORY = "C:\\RIAN Download\\";
}
