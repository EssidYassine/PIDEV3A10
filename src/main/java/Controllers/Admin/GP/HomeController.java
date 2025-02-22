package Controllers.Admin.GP;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML
    private void handleGestionUsers(ActionEvent event) {
        navigateToScene(event, "/Views/Admin/GP/UserManagement.fxml");
    }

    @FXML
    private void handleUpdateClick(ActionEvent event) {
        navigateToScene(event, "/Views/Admin/GP/UpdatePack.fxml");
    }
    @FXML
    private void handleGestionPacks(ActionEvent event) {
        navigateToScene(event, "/Views/Admin/GP/Pack.fxml");
    }

    private void navigateToScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de chargement", "Erreur lors du chargement du fichier FXML : " + e.getMessage());
        }
    }
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
