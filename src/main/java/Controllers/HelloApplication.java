package Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override


        public void start(Stage stage) throws Exception {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/Home.fxml"));
           AnchorPane root = (AnchorPane) loader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("home sweet home");
            stage.show();
        }


    public static void main(String[] args) {
        launch();
    }
}

