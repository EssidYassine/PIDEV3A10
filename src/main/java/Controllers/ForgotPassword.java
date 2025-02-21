package Controllers;

import Models.Session;
import Models.User;
import Services.ServiceUser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mailling.SendEmail;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

public class ForgotPassword {

    @FXML
    private TextField mailid;

    @FXML
    private Button envoyermail;

    @FXML
    private Label emailErrorLabel;

    private final ServiceUser userService = new ServiceUser();

    @FXML
    public void initialize() {
        mailid.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                validateEmail(newValue);
            }
        });

        envoyermail.setDisable(true);
    }

    @FXML
    private void handleSendEmail(ActionEvent event) {
        String email = mailid.getText().trim();

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir une adresse e-mail valide.");
            mailid.clear();
            return;
        }

        // Vérifier si l'email existe en base
        User user = userService.getUserByEmail(email);
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun compte trouvé avec cet e-mail.");
            return;
        }

        String verificationCode = generateCode();
        Session.setUser(user);  // On met à jour l'utilisateur dans la session
        Session.getInstance().setcodeUser(verificationCode); // Mettez à jour le code de vérification dans la session

        try {
            SendEmail.send(email, Integer.parseInt(verificationCode));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'envoi de l'e-mail a échoué. Vérifiez votre connexion internet.");
            return;
        }

        // Demander à l'utilisateur d'entrer le code
        validateCode(email);
    }

    private void validateCode(String email) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Vérification");
        dialog.setHeaderText("Entrez le code de vérification envoyé à votre e-mail :");
        dialog.setContentText("Code :");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String enteredCode = result.get();
            if (enteredCode.equals(Session.getInstance().getcodeUser())) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Vérification réussie. Vous pouvez réinitialiser votre mot de passe.");
                redirectToChangePasswordPage(email); // Redirection vers la page de changement de mot de passe
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Code de vérification invalide. Veuillez réessayer.");
            }
        }
    }

    private void redirectToChangePasswordPage(String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/changepwd.fxml"));
            Parent root = loader.load();
            ChangePasswordController controller = loader.getController();
            controller.setEmail(email);

            Stage stage = (Stage) envoyermail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Ajoutez cette ligne pour imprimer la trace de l'erreur
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page de changement de mot de passe.");
        }
    }

    public String generateCode() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    private void validateEmail(String email) {
        if (email.isEmpty()) {
            emailErrorLabel.setText("Le champ e-mail ne peut pas être vide.");
            emailErrorLabel.setTextFill(Color.RED);
            envoyermail.setDisable(true);
        } else if (!isValidEmail(email)) {
            emailErrorLabel.setText("Adresse e-mail invalide.");
            emailErrorLabel.setTextFill(Color.RED);
            envoyermail.setDisable(true);
        } else {
            emailErrorLabel.setText("✔ Email valide");
            emailErrorLabel.setTextFill(Color.GREEN);
            envoyermail.setDisable(false);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
