package Controllers.Client.GU;

import Models.Session;
import Models.User;
import Services.ServiceUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color; // Importer Color pour changer la couleur des textes
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import Tools.AppUtils;

public class editpassword {

    @FXML
    private ImageView backflech;
    @FXML
    private PasswordField passeid11; // Ancien mot de passe
    @FXML
    private PasswordField nouvellemdp; // Nouveau mot de passe
    @FXML
    private PasswordField confirmernvmotdepasse; // Confirmation du nouveau mot de passe
    @FXML
    private Label errencientmdp;
    @FXML
    private Label errnvmdp;
    @FXML
    private Label errconfnvmdp;
    @FXML
    private Button confirmerledit;

    private final ServiceUser serviceUser = new ServiceUser();

    @FXML
    public void initialize() {
        User a = Session.getUser();
        System.out.println(a.getPassword());
        backflech.setOnMouseClicked(event -> gotodetails());
        passeid11.textProperty().addListener((observable, oldValue, newValue) -> validateOldPassword(newValue));
        nouvellemdp.textProperty().addListener((observable, oldValue, newValue) -> validateNewPassword(newValue));
        confirmernvmotdepasse.textProperty().addListener((observable, oldValue, newValue) -> validateConfirmPassword(newValue));

        confirmerledit.setOnAction(event -> changerMotDePasse());
    }

    private void changerMotDePasse() {
        User user = Session.getUser();
        System.out.println(user.getPassword());
        if (user == null) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Aucun utilisateur connecté !");
            return;
        }

        String ancienMdp = passeid11.getText();
        String nouveauMdp = nouvellemdp.getText();
        String confirmationMdp = confirmernvmotdepasse.getText();

        if (ancienMdp.isEmpty() || nouveauMdp.isEmpty() || confirmationMdp.isEmpty()) {
            afficherMessageErreur("Tous les champs sont obligatoires !");
            return;
        }

        if (!ancienMdp.equals(user.getPassword())) {
            errencientmdp.setText("Ancien mot de passe incorrect !");
            errencientmdp.setTextFill(Color.RED); // Changer la couleur en rouge
            return;
        } else {
            errencientmdp.setText("");
        }

        if (nouveauMdp.length() < 8) {
            errnvmdp.setText("Le mot de passe doit contenir au moins 8 caractères !");
            errnvmdp.setTextFill(Color.RED); // Changer la couleur en rouge
            return;
        } else {
            errnvmdp.setText("");
        }

        // Vérification de la confirmation
        if (!nouveauMdp.equals(confirmationMdp)) {
            errconfnvmdp.setText("Les mots de passe ne correspondent pas !");
            errconfnvmdp.setTextFill(Color.RED); // Changer la couleur en rouge
            return;
        } else {
            errconfnvmdp.setText("");
        }

        String hashedPassword = BCrypt.hashpw(nouveauMdp, BCrypt.gensalt());

        user.setPassword(nouveauMdp);
        serviceUser.updatePassword(user.getEmail(), hashedPassword);

        // Message de confirmation
        afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Mot de passe changé avec succès !");

        // Redirection vers la page de détails
        gotodetails();
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherMessageErreur(String message) {
        errencientmdp.setText(message);
        errnvmdp.setText(message);
        errconfnvmdp.setText(message);
    }

    private void gotodetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GU/UserDetails.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backflech.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page !");
        }
    }
    private void validateOldPassword(String oldPassword) {
        User user = Session.getUser();
        if (user != null && !oldPassword.equals(user.getPassword())) {
            errencientmdp.setText("Ancien mot de passe incorrect !");
            errencientmdp.setTextFill(Color.RED); // Changer la couleur en rouge
        } else {
            errencientmdp.setText("");
        }
    }

    private void validateNewPassword(String newPassword) {
        if (newPassword.length() < 8) {
            errnvmdp.setText("Le mot de passe doit contenir au moins 8 caractères !");
            errnvmdp.setTextFill(Color.RED); // Changer la couleur en rouge
        } else {
            errnvmdp.setText("");
        }
        // Vérification de la confirmation si elle est déjà saisie
        validateConfirmPassword(confirmernvmotdepasse.getText());
    }

    private void validateConfirmPassword(String confirmPassword) {
        String newPassword = nouvellemdp.getText();
        if (!confirmPassword.equals(newPassword)) {
            errconfnvmdp.setText("Les mots de passe ne correspondent pas !");
            errconfnvmdp.setTextFill(Color.RED); // Changer la couleur en rouge
        } else {
            errconfnvmdp.setText("");
        }
    }
}
