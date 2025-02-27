package Controllers.Admin.GL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class CrudLocauxContr {

    // Method to reload the current scene or load a new one if specified
    private void loadScene(ActionEvent actionEvent, String fxmlFile) {
        try {
            // Get the current stage
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Set the updated content to the existing scene
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de " + fxmlFile + " !");
        }
    }

    @FXML
    private void ajouterLocaux(ActionEvent actionEvent) {
        loadScene(actionEvent, "/Views/Admin/GL/Add_Local.fxml");  // Load Add_Local.fxml
    }

    public void consulterLocaux(ActionEvent actionEvent) {
        loadScene(actionEvent, "/Views/Admin/GL/Show_Local.fxml");  // Load Show_Local.fxml
    }

    public void revenirFenetreHome(ActionEvent actionEvent) {
        loadScene(actionEvent, "/Views/Admin/GL/Home.fxml");  // Load Home.fxml
    }

    public void consultermodifier(ActionEvent actionEvent) {
        loadScene(actionEvent, "/Views/Admin/GL/Show_Modify.fxml");  // Load Show_Modify.fxml
    }
}
