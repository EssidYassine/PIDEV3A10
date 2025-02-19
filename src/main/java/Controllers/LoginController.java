package Controllers;

import Models.User;
import Services.ServiceUser;
import Tools.DataBaseConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;


import java.io.IOException;
import java.sql.*;
import java.util.Locale;

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
    static User loggedInUser;
    private DataBaseConnection dbConnection; // Instance of your connection class
    private ServiceUser userService; // Instance of your user service class




    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void initialize() {
        showid.setOnAction(e -> handleShowPassword());
        dbConnection = DataBaseConnection.getDatabaseConnection();
        userService = new ServiceUser();

        Platform.runLater(() -> {
            Stage stage = (Stage) mailid.getScene().getWindow();
            if (stage != null) {
                stage.setWidth(1200); // Set initial width
                stage.setHeight(680); // Set initial height
                stage.setResizable(true); // Allow resizing (or false for fixed size)
            }
        });
    }


    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    @FXML
    private void seConnecter(ActionEvent event) {  // Remove ServiceUser parameter
        String email = mailid.getText();
        String motDePasse = passeid.getText();

        Platform.runLater(() -> errorLabel.setText("")); // Clear error label

        if (email.isEmpty() || motDePasse.isEmpty()) {
            Platform.runLater(() -> errorLabel.setText("L'email et le mot de passe ne peuvent pas être vides"));
            return;
        }

        String role = userService.findUserByEmailAndPassword(email, motDePasse); // Use the service

        if (role != null) {
            loggedInUser = userService.findUserByEmailAndPassword2(email, motDePasse); // Get the user object
            System.out.println(loggedInUser);
            if (loggedInUser != null) {
                System.out.println("Login effectué en tant que " + role);
                try {
                    String fxmlFile = determineFxmlFile(role);
                    if (fxmlFile != null) {
                        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
                        Scene scene = new Scene(root);
                        Stage currentStage = (Stage) mailid.getScene().getWindow();
                        currentStage.setScene(scene);
                        currentStage.show();
                    } else {
                        Platform.runLater(() -> errorLabel.setText("Role non reconnu."));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> errorLabel.setText("Erreur lors du chargement de la vue."));
                }
            } else {
                Platform.runLater(() -> errorLabel.setText("Erreur lors de la récupération des informations d'utilisateur."));
            }

        } else {
            Platform.runLater(() -> errorLabel.setText("Email ou mot de passe invalide."));
        }
    }
    @FXML
    private void forgotPasswordButtonAction(ActionEvent event) {
        System.out.println("Mot de passe oublié cliqué");
    }

    private String determineFxmlFile(String role) {
        switch (role) {
            case "admin":
                return "/admin/AccueilAdmin.fxml";
            case "user":
                return "/AccueilUser.fxml";
            default:
                return null; // Or handle the default case appropriately
        }
    }
    private void handleShowPassword() {
        if (showid.isSelected()) {
            passeid.setPromptText(passeid.getText());
            passeid.setText(null);
            passeid.setStyle("-fx-prompt-text-fill: black;"); // Change la couleur du texte d'invite en noir
        } else {
            passeid.setText(passeid.getPromptText());
            passeid.setPromptText(null);
            passeid.setStyle(""); // Réinitialise le style
        }
    }
}