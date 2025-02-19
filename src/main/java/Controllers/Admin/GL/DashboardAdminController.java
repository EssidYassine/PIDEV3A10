package Controllers.Admin.GL;


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
    private Button clientid, artisteid, btn_Timetable; // Ajout du bouton Gestion locaux

    @FXML
    public void initialize() {
        // Ajout d'un événement pour le bouton "Gestion locaux "
        btn_Timetable.setOnAction(event -> ajouterLocal(event));
    }


    @FXML
    private void afficherLocaux(ActionEvent event) {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/Views/Admin/GL/Show_Local.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de show_Local.fxml !");
 }
}
    @FXML
    private void ajouterLocal(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Admin/GL/Add_Local.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Local");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AddLocal.fxml !");
        }
    }
}
