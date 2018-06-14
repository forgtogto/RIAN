package devices;

public class NetProtocols {

	public static final String LOGIN_REQUEST = "logreq";
	public static final String LOGIN_ACCEPT = "logaccept";
	public static final String LOGIN_DENY = "logdeny";
	
	public static final String ID_DUP_CHECK_REQUEST = "iddupcheckreq";
	public static final String ID_DUP_RESPOND_OK = "dupok";
	public static final String ID_DUP_RESPOND_DENY = "dupdeny";
	
	public static final String EXIT_REQUEST = "exitreq";
	public static final String EXIT_RESPOND = "exitres";
	
	public static final String RECIEVE_READY = "isready?";
	public static final String RECIEVE_READY_OK = "readyok";
	
	public static final String INVALID_REQUEST_ERROR = "invalidreqerr";
	
	
	
	
	// 비밀번호찾기 - 질문목록
	public static final String PASSWORD_FIND_QUESTION_LIST = "pfql";
			
	// 비밀번호찾기 - 본인인증
	public static final String PASSWORD_FIND_IDENTIFY_REQUEST = "pfireq";
	public static final String PASSWORD_FIND_IDENTIFY_RESPOND = "pfires";		
			
	// 비밀번호찾기 - 새 비밀번호 설정
	public static final String PASSWORD_FIND_MODIFY_REQUEST = "pfmreq";
	public static final String PASSWORD_FIND_MODIFY_RESPOND = "pfmres";
	
	public static final String MEMBER_JOIN_REQUEST = "mjreq";
	public static final String MEMBER_JOIN_RESPOND = "mjres";
 
	 
}
