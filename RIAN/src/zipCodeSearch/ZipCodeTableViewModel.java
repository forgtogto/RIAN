package zipCodeSearch;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ZipCodeTableViewModel {

	private StringProperty zipcode;
	private StringProperty siDo;
	private StringProperty siGunGu;
	private StringProperty dong;
	private StringProperty ri;
	private StringProperty roadName;
	private StringProperty buildingName;

	public ZipCodeTableViewModel() {
		this.zipcode = new SimpleStringProperty();
		this.siDo = new SimpleStringProperty();
		this.siGunGu = new SimpleStringProperty();
		this.dong = new SimpleStringProperty();
		this.ri = new SimpleStringProperty();
		this.roadName = new SimpleStringProperty();
		this.buildingName = new SimpleStringProperty();
	}

	//�����ȣ
	public String getZipcode() { return zipcode.get(); }
	public void setZipcode(String zipcode) { this.zipcode.set(zipcode); }
	public StringProperty zipcodeProperty() { return zipcode; }

	//�õ�
	public String getSiDo() { return siDo.get(); }
	public void setSiDo(String siDo) { this.siDo.set(siDo); }
	public StringProperty siDoProperty() { return siDo; }

	//�ñ���
	public String getSiGunGu() { return siGunGu.get(); }
	public void setSiGunGu(String siGunGu) { this.siGunGu.set(siGunGu); }
	public StringProperty siGunGuProperty() { return siGunGu; }

	//���鵿
	public String getDong() { return dong.get(); }
	public void setDong(String dong) { this.dong.set(dong); }
	public StringProperty dongProperty() { return dong; }

	//��
	public String getRi() { return ri.get(); }
	public void setRi(String ri) { this.ri.set(ri); }
	public StringProperty riProperty() { return ri; }

	//���θ�
	public String getRoadName() { return roadName.get(); }
	public void setRoadName(String roadName) { this.roadName.set(roadName); }
	public StringProperty roadNameProperty() { return roadName; }

	//�����̸�
	public String getBuildingName() { return buildingName.get(); }
	public void setBuildingName(String buildingName) { this.buildingName.set(buildingName); }
	public StringProperty buildingNameProperty() { return buildingName; }
	
}
