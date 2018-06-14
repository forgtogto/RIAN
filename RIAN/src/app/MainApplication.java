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
			Setting.SERVER_IP_ADDRESS = CustomDialog.showInputDialog("서버 IP주소를 확인해주세요.", sManager.getStage());
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
				retryDlg = CustomDialog.showMessageDialog("서버접속실패, 자동재접속을 하시려면 창을 종료하지 마세요", primaryStage);
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
			System.out.println("서버 재접속 시도를 시작합니다.");
			int cnt = 0;
			while (true) {
				System.out.println("재접속 시도횟수 : " + cnt++);
				System.out.print("결과                 : ");
				int c = m.connectToServer();
				if (c != Setting.COMMIT) {
					System.out.println("실패..");
					synchronized (this) {
						try {
							this.wait(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else {
					System.out.println("성공! 프로그램을 시작하고 재접속스레드를 종료합니다.");
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
