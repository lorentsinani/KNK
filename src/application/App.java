package application;

import java.util.Locale;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilities.I18N;

public class App extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		Parent root = FXMLLoader.load(getClass().getResource("../views/home-view.fxml"));

		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("LAMALE Hotel");
		primaryStage.show();

		I18N.setLocale(Locale.ENGLISH);

	}

	public static void main(String[] args) {
		launch(args);
	}
}
