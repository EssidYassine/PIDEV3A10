package Controllers.Client.GS;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.IOException;





public class GestionnaireS1 {

    @FXML
    private void goToGestionnaireS(MouseEvent event) {
        try {
            // Charger le fichier FXML de la nouvelle fenêtre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/GestionnaireS1.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et remplacer son contenu
            Stage stage = (Stage) ((Label) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void revenirFenetreHome(ActionEvent actionEvent) {
        try {
            // Charger l'interface Service.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/Home1.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("gestionnaire");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de GestionnaireS.fxml !");
        }
    }

    public void afficherListeServices(ActionEvent actionEvent) {
        try {
            // Charger l'interface Service.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/GetService1.fxml"));
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