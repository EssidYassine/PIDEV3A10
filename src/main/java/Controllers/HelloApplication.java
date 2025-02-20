package Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

            Scene scene = new Scene(root, 1086, 700);

            stage.setTitle("TEST!");
            stage.setScene(scene);

            // ✅ Center the window on the screen
            stage.centerOnScreen();

            // ✅ Prevent resizing if necessary
            stage.setResizable(false);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}