import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


import java.io.IOException;

public class HelloApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage){

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/Views/Admin/GP/AdminCalendar.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 1086, 700);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        StackPane root = new StackPane();

    }
}