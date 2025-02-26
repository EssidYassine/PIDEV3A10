package Controllers.GA;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;  // Import MouseEvent for onMouseClicked

import java.io.IOException;

public class Home {

    // This method will be called when the "ACTUALITE" label is clicked
    public void gotoActuality(MouseEvent event) {  // Use MouseEvent here
        try {
            // Load the actuality.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/actuality.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene with actuality.fxml
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add other methods like gotoAcceuilService if necessary for other labels
    public void gotoAcceuilService(MouseEvent mouseEvent) {
        try {
            // Charger l'interface AcceuilService.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/AcceuilService.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }}

}
