package zipCodeSearch;

public class SendingData {
	
	private String postNum;
	private String fullAddress;
	
	public String getPostNum() {
		return postNum;
	}
	public void setPostNum(String postNum) {
		this.postNum = postNum;
	}
	public String getFullAddress() {
		return fullAddress;
	}
	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}
	
	public SendingData(String postNum, String fullAddress) {
		this.postNum = postNum;
		this.fullAddress = fullAddress;
	}
	
	 
 

}
