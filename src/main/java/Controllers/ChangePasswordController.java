package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import Services.ServiceUser; // Assurez-vous que ce service existe
import org.mindrot.jbcrypt.BCrypt;

public class ChangePasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label errorPasswordLabel;

    @FXML
    private Button changePasswordButton; // Assurez-vous que ce bouton est défini dans votre FXML

    private final ServiceUser userService = new ServiceUser(); // Service pour gérer les utilisateurs

    @FXML
    public void initialize() {
        changePasswordButton.setDisable(true);

        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isPasswordValid = newValue.length() > 8;
            changePasswordButton.setDisable(!isPasswordValid); // Active ou désactive le bouton

            // Affichage du message d'erreur si le mot de passe est invalide
            if (!isPasswordValid) {
                errorPasswordLabel.setText("Le mot de passe doit comporter plus de 8 caractères.");
            } else {
                errorPasswordLabel.setText(""); // Efface le message d'erreur lorsque le mot de passe est valide
            }
        });
    }

    @FXML
    private void handleChangePassword() {
        String newPassword = newPasswordField.getText().trim();
        // Vérification de la longueur du mot de passe avant de procéder
        if (newPassword.length() <= 8) {
            errorPasswordLabel.setText("Le mot de passe doit comporter plus de 8 caractères.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        boolean isUpdated = userService.updatePassword(emailField.getText(), hashedPassword); // Assurez-vous que cette méthode existe

        if (isUpdated) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Mot de passe changé avec succès.");
            redirectToLogin(); // Redirection vers la page de connexion
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec du changement de mot de passe. Veuillez réessayer.");
        }
    }

    // Affichage d'une alerte
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setEmail(String email) {
        emailField.setText(email);
        emailField.setDisable(true); // Désactivez le champ pour éviter que l'utilisateur ne le modifie
    }

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Login.fxml")); // Remplacez par le chemin vers votre FXML de connexion
            Parent root = loader.load();
            Stage stage = (Stage) changePasswordButton.getScene().getWindow(); // Obtient la fenêtre actuelle
            stage.setScene(new Scene(root)); // Définit la nouvelle scène
            stage.setTitle("Connexion"); // Titre de la nouvelle fenêtre
            stage.show(); // Affiche la nouvelle scène
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la redirection vers la page de connexion.");
        }
    }
}
