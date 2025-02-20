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
    private CheckBox showid;
    @FXML
    private Label errorLabel;
    @FXML
    private Label errorEmailLabel;
    @FXML
    private Label errorPasswordLabel;

    private DataBaseConnection dbConnection;
    private ServiceUser userService;

    public void initialize() {
        showid.setOnAction(e -> handleShowPassword());
        dbConnection = DataBaseConnection.getDatabaseConnection();
        userService = new ServiceUser();
        inscrireid.setOnAction(this::handleInscription);

        // Ajout des Listeners pour la validation en temps réel
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
            errorPasswordLabel.setText(""); // Efface l'erreur si valide
        }
    }


    @FXML
    private void seConnecter(ActionEvent event) {
        validateEmail();
        validatePassword();

        if (!errorEmailLabel.getText().isEmpty() || !errorPasswordLabel.getText().isEmpty()) {
            showAlert("Erreur", "Corrigez les erreurs avant de vous connecter.");
            errorPasswordLabel.setText("");
            errorEmailLabel.setText("");
            mailid.clear();
            passeid.clear();
            return;
        }

        String email = mailid.getText();
        String motDePasse = passeid.getText();

        Platform.runLater(() -> errorLabel.setText(""));

        User user = userService.findUserByEmailAndPassword2(email, motDePasse);

        if (user != null) {

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
                    errorPasswordLabel.setText("");
                    errorEmailLabel.setText("");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors du chargement de la vue.");
                errorPasswordLabel.setText("");
                errorEmailLabel.setText("");
            }
        } else {
            showAlert("Erreur", "Email ou mot de passe invalide.");
            errorPasswordLabel.setText("");
            errorEmailLabel.setText("");
            mailid.clear();
            passeid.clear();
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
                return "/AccueilUser.fxml";
            default:
                return null; // Or handle the default case appropriately
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

    private void handleShowPassword() {
        if (showid.isSelected()) {
            passeid.setPromptText(passeid.getText());
            passeid.setText("");
        } else {
            passeid.setText(passeid.getPromptText());
            passeid.setPromptText("");
        }
    }
}
