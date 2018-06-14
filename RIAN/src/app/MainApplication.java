package app;

import app.customs.CustomDialog;
import devices.Setting;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainApplication extends Application {

	private SceneManager sManager;
	private CustomDialog retryDlg;

	@Override
	public void start(Stage primaryStage) throws Exception {

		sManager = new SceneManager(primaryStage);
		sManager.setHost(this);
		int c;
		while (true) {
			Setting.SERVER_IP_ADDRESS = CustomDialog.showInputDialog("���� IP�ּҸ� Ȯ�����ּ���.", sManager.getStage());
			Setting.FILE_SERVER_IP_ADDRESS = Setting.SERVER_IP_ADDRESS;

			if (Setting.SERVER_IP_ADDRESS == null) {
				System.exit(-1);
			}

			c = sManager.connectToServer();

			if (c == Setting.COMMIT) {
				restart(primaryStage);
				break;
			}

			switch (c) {
			case Setting.CONNECT_ERROR:
				retryDlg = CustomDialog.showMessageDialog("�������ӽ���, �ڵ��������� �Ͻ÷��� â�� �������� ������", primaryStage);
				break;
			}
		}

		if (c == Setting.CONNECT_ERROR) {
			new Retry(sManager).start();
		}
	}

	public void restart(Stage primaryStage) {
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				System.exit(1);
			}
		});

		sManager.changeScene(Setting.loginproc_fxml);

		primaryStage.setResizable(false);
		primaryStage.show();
	}

	class Retry extends Thread {
		SceneManager m;

		Retry(SceneManager m) {
			this.m = m;
		}

		@Override
		public void run() {
			System.out.println("���� ������ �õ��� �����մϴ�.");
			int cnt = 0;
			while (true) {
				System.out.println("������ �õ�Ƚ�� : " + cnt++);
				System.out.print("���                 : ");
				int c = m.connectToServer();
				if (c != Setting.COMMIT) {
					System.out.println("����..");
					synchronized (this) {
						try {
							this.wait(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else {
					System.out.println("����! ���α׷��� �����ϰ� �����ӽ����带 �����մϴ�.");
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							restart(sManager.getStage());
							retryDlg.close();
						}
					});
					break;
				}
			}
		}
	}
}
