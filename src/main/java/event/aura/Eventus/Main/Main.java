package event.aura.Eventus.Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
    try {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/actuality.fxml"));
//        Parent root = loader.load();
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/posts.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Actuality");
        primaryStage.setFullScreen(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
    catch (Exception e) {
        System.err.println(e.getMessage());
    }
        // Ensure controller is retrieved properly
    }


    public static void main(String[] args) {
        launch(args);
    }

}