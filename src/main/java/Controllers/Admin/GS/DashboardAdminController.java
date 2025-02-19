package Controllers.Admin.GS;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardAdminController {

    @FXML
    private Button btnDashboard;

    @FXML
    private Button clientid, artisteid, btn_Timetable; // Ajout du bouton Gestion Services

    @FXML
    public void initialize() {
        // Ajout d'un événement pour le bouton "Gestion Services"
        btn_Timetable.setOnAction(event -> afficherServices(event));
    }

    @FXML
    private void afficherServices(ActionEvent event) {
        try {
            // Charger l'interface GetService.fxml
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Admin/GS/GetService.fxml"));

            // Récupérer la scène actuelle et afficher la nouvelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de GetService.fxml !");
        }
    }
}
