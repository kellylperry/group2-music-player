
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
    
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("player.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Music Player");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()	{

		@Override
		public void handle(WindowEvent arg0) {
			Platform.exit();
			System.exit(0);
		}
		});
	}
    
	public static void main(String[] args) {
		launch(args);
	}
}
