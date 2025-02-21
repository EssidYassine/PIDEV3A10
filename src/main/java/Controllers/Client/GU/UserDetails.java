package Controllers.Client.GU;

import Models.Session;
import Models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class UserDetails {
    @FXML
    private ImageView backflech;
    @FXML
    private ImageView editpage;
    @FXML
    private ImageView logout;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label etatCompteLabel;

    @FXML
    private Label numeroTelLabel;

    @FXML
    private Label dateNaissanceLabel;

    @FXML
    public void initialize() {
        backflech.setOnMouseClicked(event -> gotodetails());
        editpage.setOnMouseClicked(event -> gotoedit());
        loadUserDetails(Session.getUser());
        logout.setOnMouseClicked(event -> confirmLogout());
        loadUserDetails(Session.getUser());


        User currentUser = Session.getUser();
        System.out.println(currentUser);

        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
            emailLabel.setText(currentUser.getEmail());
            etatCompteLabel.setText(currentUser.getIsActive());
            numeroTelLabel.setText(String.valueOf(currentUser.getNumTel()));
            dateNaissanceLabel.setText(currentUser.getDateDeNaissance().toString());
        } else {
            System.out.println("Aucun utilisateur connecté.");
        }
    }

    private void gotodetails() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GU/Home1.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backflech.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur de chargement FXML !");
        }
    }

    private void gotoedit() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GU/EditProfile.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) editpage.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit User");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur de chargement FXML !");
        }
    }

    public void loadUserDetails(User user) {
        if (user != null) {
            usernameLabel.setText(user.getUsername());
            emailLabel.setText(user.getEmail());
            numeroTelLabel.setText(String.valueOf(user.getNumTel()));
            dateNaissanceLabel.setText(user.getDateDeNaissance().toString());
            etatCompteLabel.setText(user.getIsActive());

        }


    }

    private void confirmLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir vous déconnecter ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Session.setUser(null);
            goToLogin();
        }
    }

    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur de chargement de la page de connexion !");
        }
    }
}
