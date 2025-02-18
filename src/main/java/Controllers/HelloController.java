package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText; // Must match the fx:id in FXML

    @FXML
    private void onHelloButtonClick() {
        welcomeText.setText("Hello, JavaFX!"); // Ensure the label exists
    }
}
