package Controllers;

import Models.Session;
import javafx.scene.Node;
import Models.User;
import Services.ServiceUser;
import Tools.DataBaseConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.regex.Pattern;

public class LoginController {

    @FXML
    private TextField mailid;
    @FXML
    private PasswordField passeid;
    @FXML
    private Button connecterid;
    @FXML
    private Button inscrireid;
    @FXML
    private Label errorLabel;
    @FXML
    private Label errorEmailLabel;
    @FXML
    private Label errorPasswordLabel;

    private DataBaseConnection dbConnection;
    private ServiceUser userService;
    private boolean isPasswordVisible = false; // Champ pour suivre l'état de visibilité du mot de passe

    public void initialize() {
        dbConnection = DataBaseConnection.getDatabaseConnection();
        userService = new ServiceUser();
        inscrireid.setOnAction(this::handleInscription);

        mailid.textProperty().addListener((observable, oldValue, newValue) -> validateEmail());
        passeid.textProperty().addListener((observable, oldValue, newValue) -> validatePassword());
    }

    /**
     * Vérifie la validité de l'email en temps réel.
     */
    private void validateEmail() {
        String email = mailid.getText();
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (email.isEmpty()) {
            errorEmailLabel.setText("L'email ne peut pas être vide.");
        } else if (!Pattern.matches(regex, email)) {
            errorEmailLabel.setText("Format d'email invalide.");
        } else {
            errorEmailLabel.setText("");
        }
    }

    private void validatePassword() {
        String password = passeid.getText();
        if (password.isEmpty()) {
            errorPasswordLabel.setText("Le mot de passe ne peut pas être vide.");
        } else if (password.length() < 8) {
            errorPasswordLabel.setText("Le mot de passe doit contenir au moins 8 caractères.");
        } else {
            errorPasswordLabel.setText("");
        }
    }

    @FXML
    private void seConnecter(ActionEvent event) {
        validateEmail();
        validatePassword();

        // Vérification des erreurs de validation
        if (!errorEmailLabel.getText().isEmpty() || !errorPasswordLabel.getText().isEmpty()) {
            showAlert("Erreur", "Corrigez les erreurs avant de vous connecter.");
            // Vider les champs et les labels en cas d'erreur
            mailid.clear();
            passeid.clear();
            errorEmailLabel.setText("");
            errorPasswordLabel.setText("");
            return;
        }

        String email = mailid.getText();
        String motDePasse = passeid.getText();

        Platform.runLater(() -> errorLabel.setText(""));

        User user = userService.findUserByEmailAndPassword2(email, motDePasse);

        if (user != null) {
            user.setPassword(motDePasse);
            Session.setUser(user);
            Session.afficherSession();

            System.out.println("Login effectué en tant que " + user.getRole());

            try {
                String fxmlFile = determineFxmlFile(user.getRole());
                if (fxmlFile != null) {
                    Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
                    Scene scene = new Scene(root);
                    Stage currentStage = (Stage) mailid.getScene().getWindow();
                    currentStage.setScene(scene);
                    currentStage.show();
                } else {
                    showAlert("Erreur", "Rôle non reconnu.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors du chargement de la vue.");
            }
        } else {
            // Connexion échouée → afficher une alerte et vider les labels/champs
            showAlert("Erreur", "Email ou mot de passe invalide.");
            mailid.clear(); // Vider le champ email
            passeid.clear(); // Vider le champ mot de passe
            errorEmailLabel.setText(""); // Vider le label d'erreur d'email
            errorPasswordLabel.setText(""); // Vider le label d'erreur de mot de passe
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String determineFxmlFile(String role) {
        switch (role) {
            case "admin":
                return "/Views/Admin/GU/Home.fxml";
            case "user":
                return "/Views/Client/GU/Home1.fxml";
            default:
                return null;
        }
    }

    private void handleInscription(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Client/GU/inscription.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
