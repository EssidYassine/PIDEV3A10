package Controllers.Client.GU;

import Models.Session;
import Models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class Home1 {
    @FXML
    private ImageView userIcon;
    private Session session;
    @FXML
    void initialize() {
        userIcon.setOnMouseClicked(event -> gotodetails());
        session.afficherSession();
    }
    private void gotodetails() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GU/UserDetails.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) userIcon.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur de chargement FXML !");
        }
    }


    @FXML
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
