package Controllers.Admin.GU;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXML;

import java.io.IOException;

public class DashboardAdminController {

    @FXML
    private Button btnDashboard;
    @FXML
    private Button clientid, artisteid;

    public void initialize() {
        btnDashboard.setOnAction(event -> {
            try {
                // Charger l'interface AfficherArtisteNV.fxml
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Admin/GU/ListUsers.fxml"));
                Stage stage = (Stage) btnDashboard.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }






}
