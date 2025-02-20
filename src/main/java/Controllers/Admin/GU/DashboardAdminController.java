package Controllers.Admin.GU;

import Models.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Cursor;

import java.io.IOException;
import java.util.Optional;

public class DashboardAdminController {

    @FXML
    private Button btnDashboard;
    @FXML
    private Button clientid, artisteid;
    @FXML
    private ImageView logoutbtn; // ImageView pour le logout

    public void initialize() {

        logoutbtn.setCursor(Cursor.HAND);

        // Gestion du clic sur le bouton de déconnexion avec confirmation
        logoutbtn.setOnMouseClicked(event -> handleLogout());

        // Gestion du clic sur le bouton Dashboard
        btnDashboard.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Admin/GU/ListUsers.fxml"));
                Stage stage = (Stage) btnDashboard.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleLogout() {
        // Afficher une boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de déconnexion");
        alert.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
        alert.setContentText("Cliquez sur OK pour confirmer, ou Annuler pour rester connecté.");

        // Attendre la réponse de l'utilisateur
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // L'utilisateur a confirmé → vider la session et rediriger vers login
            Session.clear();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) logoutbtn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Connexion");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
