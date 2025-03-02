package Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/Views/Login.fxml"));
            Parent root = fxmlLoader.load();

            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(root);

            Scene scene = new Scene(borderPane, 1086, 700);

            stage.setTitle("TEST!");
            stage.setScene(scene);

            // Centrer la fenêtre sur l'écran
            stage.centerOnScreen();

            // Empêcher le redimensionnement de la fenêtre
            stage.setResizable(false);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
