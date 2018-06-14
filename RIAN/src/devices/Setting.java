package devices;

public class Setting {
	
	// Application 설정 
	public static final int COMMIT = 0;	
	public static final int ERROR = -2;
	
	//UI 설정 
		// UI FXML파일 이름 정리
		public static final String loginproc_fxml = "/app/ui/LoginProc.fxml";
		public static final String memberjoin_fxml = "/app/ui/MemberJoin.fxml";
		public static final String password_find_fxml = "/app/ui/PasswordFind.fxml";

		// UI DIALOG 파일 이름 정리 
		public static final String alert_dialog = "/app/ui/AlertDialog.fxml";
		public static final String ok_cancel_dialog = "/app/ui/OkCancelDialog.fxml";
		public static final String input_dialog = "/app/ui/InputDialog.fxml";
		
		
		public static final String test_fxml = "/app/ui/test.fxml";
		public static final String test2_fxml = "/app/ui/test2.fxml";
	
		
		// UI 윈도우창 이름 목록
		public static final String loginproc_title = "RAIN - 로그인";
		public static final String memberjoin_title = "RAIN - 사용자 등록";
		public static final String alert_dialog_title = "RAIN - 알림";
		public static final String password_find_title = "RAIN - 비밀번호 찾기";
		public static final String ok_cancel_dialog_title = "RAIN - 확인";
		public static final String input_dialog_title = "RAIN - 입력하세요.";
		
		
		
	// 네트워크 설정_서버연결/파일서버연결
	public static String SERVER_IP_ADDRESS = "localhost";
	public static final int SERVER_PORT_NUMBER = 8080;
	
	public static String FILE_SERVER_IP_ADDRESS = "localhost";
	public static final int FILE_SERVER_PORT_NUMBER = 9090;
		// 네트워크 에러 표시 
		public static final int CONNECT_ERROR = -1;							// 서버 접속 실패
		public static final int UNKNOWN_HOST_ERROR = -3;
		
	/* Server */	
	public static String DEFAULT_LOG_BACKUP_PATH = "C:\\RIAN\\Backup\\";
	
	/* Database */
	public static final String DASEBASE_DRIVER = "org.gjt.mm.mysql.Driver";	// 기본 데이터베이스 드라이버 패키지
	public static final String DEFAULT_USE_DATABASE = "리안";				// 기본 사용 데이터베이스
	public static final String DEFAULT_DATABASE_HOST_ID = "root";			// 기본 데이터베이스 접속 아이디
	public static final String DEFAULT_DATABASE_HOST_PASSWORD = "qwer";		// 기본 데이터베이스 접속 비밀번호
	
	/* User Data */
	public static final String DEFAULT_USER_DATA_DIRECTORY = "C:\\\\RIAN\\\\Userdata\\\\";	// 프로필이미지
	public static final String DEFAULT_MOVIE_DATA_DIRECTORY = "C:\\\\RIAN\\\\Movie\\\\";	// 영상
	public static final String DEFAULT_SUBMITTED_DATA_DIRECTORY = "C:\\\\RIAN\\\\SubmittedData\\\\";	// 제출데이터
	public static final String DEFAULT_RIAN_DIRECTORY = "C:\\\\RIAN\\\\";
	public static final String DEFAULT_DOWNLOAD_DIRECTORY = "C:\\RIAN Download\\";
}
