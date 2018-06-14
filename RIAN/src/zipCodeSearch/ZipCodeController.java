package zipCodeSearch;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
 

import app.SceneManager;
import app.customs.CustomDialog;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ZipCodeController implements Initializable {
	
	ZipcodeApp zip;
	SceneManager sManager;
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private PreparedStatement ps = null;

	private String postcode;
	private String address;

	private String zipcode;
	private String sido;
	private String sigungu;
	private String dong;
	private String ri;
	private String roadname;
	private String buildname;

	@FXML Button closebutton;
	@FXML TextField roadnameField;
	@FXML TableView<ZipCodeTableViewModel> addressView;
	@FXML TextField extraAddressField;
	@FXML TextField selectedField;
 
	@FXML TableColumn<ZipCodeTableViewModel, String> zipcodeCol;
	@FXML TableColumn<ZipCodeTableViewModel, String> sidoCol;
	@FXML TableColumn<ZipCodeTableViewModel, String> sigunguCol;
	@FXML TableColumn<ZipCodeTableViewModel, String> dongCol;
	@FXML TableColumn<ZipCodeTableViewModel, String> riCol;
	@FXML TableColumn<ZipCodeTableViewModel, String> roadnameCol;
	@FXML TableColumn<ZipCodeTableViewModel, String> buildingnameCol;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
 
	//���콺 Ŭ���� �� �������� 
	@FXML
	private void handleClickTableView(MouseEvent click) {
		//���̺� ���� Ŭ���Ǵ� ���� ������������Ƽ�� �ҷ���
		addressView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ZipCodeTableViewModel>() {

			@Override
			public void changed(ObservableValue<? extends ZipCodeTableViewModel> observableValue, ZipCodeTableViewModel oldValue,
					ZipCodeTableViewModel newValue) {
				ZipCodeTableViewModel model = addressView.getSelectionModel().getSelectedItem();
				zipcode = model.zipcodeProperty().getValue();
				sido = model.siDoProperty().getValue();
				sigungu = model.siGunGuProperty().getValue();
				dong = model.dongProperty().getValue();
				ri = model.riProperty().getValue();
				roadname = model.roadNameProperty().getValue();
				buildname = model.buildingNameProperty().getValue();
				
				//Ŭ���� ȭ�鿡 �������� �ߵ��� 
				selectedField.setText(sido +" "+sigungu +" "+dong +" "+ri +" "+roadname +" "+buildname);
				
				//���߿� ������ �����ȣ�� �ּ� 
				 postcode = zipcode;
				 address = sido +" "+sigungu +" "+dong +" "+ri +" "+roadname +" "+buildname;
				 
			}
		});
	}
	
 
	//Ȯ�� ��ư�� 
	@FXML
	private void onSuccess() {
	
		String add = extraAddressField.getText();
		String addfull = address+" "+add;
		int check = 0; 
		conn = getConnection();
		try {
			String query = 	" UPDATE selectdata SET post=?, address=? WHERE code=1;";
			ps = (PreparedStatement) conn.prepareStatement(query);
			ps.setString(1, postcode);
			ps.setString(2, addfull);
			check = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResource();
		}
		
		if(check != 0){
			System.out.println("�Է� �Ϸ�");
			Stage stage = (Stage) closebutton.getScene().getWindow();
			stage.close();  
		} else {
			System.out.println("�ٽ� �Է��ϼ���");
		}
		

	}
 
 
	
	
	//��ҹ�ư��  �����ȣ ã�� ȭ�� ���� 
	@FXML
	private void onCancle() {
		//��ҹ�ư �� ��â�� ���� 
		Stage stage = (Stage) closebutton.getScene().getWindow();
		stage.close(); 
	}
 

	//�˻���ư�� 
	@FXML 
	private void onSearch() {
		String roadName = roadnameField.getText().trim();
 
		if(roadName.length() > 1) {
			 conn = getConnection();
			try {
				stmt = conn.createStatement();
 				String query = "select ZipCode, SiDo, SiGunGu, YubMyunDong, Ri, RoadNam, BuildingName from tblzipcode where RoadNam like '%"+roadName+"%'";
				rs = stmt.executeQuery(query);
				ObservableList<ZipCodeTableViewModel> data = FXCollections.observableArrayList();
			
				while(rs.next()) {
					ZipCodeTableViewModel zctv  = new ZipCodeTableViewModel();
					zctv.setZipcode(rs.getString("ZipCode"));
					zctv.setSiDo(rs.getString("Sido"));
					zctv.setSiGunGu(rs.getString("SiGunGu"));
					zctv.setDong(rs.getString("YubMyunDong"));
					zctv.setRi(rs.getString("Ri"));
					zctv.setRoadName(rs.getString("RoadNam"));
					zctv.setBuildingName(rs.getString("BuildingName"));	
					data.add(zctv);
				}
				
				zipcodeCol.setCellValueFactory(CellDataFeatures ->CellDataFeatures.getValue().zipcodeProperty());
				sidoCol.setCellValueFactory(CellDataFeatures ->CellDataFeatures.getValue().siDoProperty());
				sigunguCol.setCellValueFactory(CellDataFeatures ->CellDataFeatures.getValue().siGunGuProperty());
				dongCol.setCellValueFactory(CellDataFeatures ->CellDataFeatures.getValue().dongProperty());
				riCol.setCellValueFactory(CellDataFeatures ->CellDataFeatures.getValue().riProperty());
				roadnameCol.setCellValueFactory(CellDataFeatures ->CellDataFeatures.getValue().roadNameProperty());
				buildingnameCol.setCellValueFactory(CellDataFeatures ->CellDataFeatures.getValue().buildingNameProperty());
				addressView.setItems(data); 

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				closeResource();
			}
			
		} else {
			CustomDialog.showConfirmDialog("2�� �̻� �Է����ּ���", sManager.getStage());
		}
		
	}
	
	//��ü �����ȣ ������ db
	private Connection getConnection() {
		try {
			String strUrl = "jdbc:mysql://localhost:3306/����";
			String strId = "root";
			String strPw = "qwer";
			Class.forName("org.gjt.mm.mysql.Driver");
			conn = DriverManager.getConnection(strUrl, strId, strPw);
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	//��� ���� 
	private void closeResource(){
		if(rs != null) try { rs.close(); conn = null; } catch (SQLException e) {}
		if(stmt != null) try { stmt.close(); conn = null; } catch (SQLException e) {}
		if(conn != null) try { conn.close(); conn = null; } catch (SQLException e) {}
	}
	
 

}
