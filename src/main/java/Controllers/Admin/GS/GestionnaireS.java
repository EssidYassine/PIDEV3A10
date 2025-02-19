package Controllers.Admin.GS;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionnaireS {

    @FXML
    public void afficherGestionnaire(ActionEvent actionEvent) {
        try {
            // Charger l'interface GestionnaireS.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/GestionnaireS.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire des Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de GestionnaireS.fxml !");
        }
    }

    @FXML
    private void afficherService(ActionEvent actionEvent) {
        try {
            // Charger l'interface Service.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/Serviice.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Service");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de Service.fxml !");
        }
    }
}
