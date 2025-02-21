package Controllers.Client.GU;
import javafx.scene.Node;
import Models.User;
import Services.ServiceUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class inscription {
    @FXML
    private ImageView backflech;

    @FXML
    private TextField prenomid;

    @FXML
    private TextField mailid;

    @FXML
    private PasswordField passeid;

    @FXML
    private TextField telid;

    @FXML
    private DatePicker dateid;

    @FXML
    private Button validerid;

    @FXML
    private Button retourid;

    @FXML
    private Label erusername;

    @FXML
    private Label eremail;

    @FXML
    private Label ermotdepasse;

    @FXML
    private Label ertel;

    @FXML
    private Label erdate;

    private final ServiceUser serviceUser = new ServiceUser();

    @FXML
    public void initialize() {
        backflech.setOnMouseClicked(event -> gotodetails());

        prenomid.textProperty().addListener((obs, oldVal, newVal) -> validateUsername());
        mailid.textProperty().addListener((obs, oldVal, newVal) -> validateEmail());
        passeid.textProperty().addListener((obs, oldVal, newVal) -> validatePassword());
        telid.textProperty().addListener((obs, oldVal, newVal) -> validatePhone());
        dateid.valueProperty().addListener((obs, oldVal, newVal) -> validateDate());
    }

    private void gotodetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backflech.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de l'utilisateur");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void validateUsername() {
        String username = prenomid.getText().trim();
        if (username.isEmpty()) {
            erusername.setText("Le nom d'utilisateur ne peut pas être vide.");
            erusername.setTextFill(javafx.scene.paint.Color.RED); // Définit la couleur du texte en rouge
        } else if (username.length() < 4) {
            erusername.setText("Le nom d'utilisateur doit contenir au moins 4 caractères.");
            erusername.setTextFill(javafx.scene.paint.Color.RED);
        } else {
            erusername.setText("");
            erusername.setTextFill(javafx.scene.paint.Color.BLACK); // Réinitialise à la couleur par défaut
        }
    }

    private void validateEmail() {
        String email = mailid.getText().trim();
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (email.isEmpty()) {
            eremail.setText("L'email ne peut pas être vide.");
            eremail.setTextFill(javafx.scene.paint.Color.RED);
        } else if (!Pattern.matches(regex, email)) {
            eremail.setText("Format d'email invalide.");
            eremail.setTextFill(javafx.scene.paint.Color.RED);
        } else {
            eremail.setText("");
            eremail.setTextFill(javafx.scene.paint.Color.BLACK);
        }
    }

    private void validatePassword() {
        String password = passeid.getText().trim();
        if (password.isEmpty()) {
            ermotdepasse.setText("Le mot de passe ne peut pas être vide.");
            ermotdepasse.setTextFill(javafx.scene.paint.Color.RED);
        } else if (password.length() < 8) {
            ermotdepasse.setText("Le mot de passe doit contenir au moins 8 caractères.");
            ermotdepasse.setTextFill(javafx.scene.paint.Color.RED);
        } else {
            ermotdepasse.setText("");
            ermotdepasse.setTextFill(javafx.scene.paint.Color.BLACK);
        }
    }

    private void validatePhone() {
        String telStr = telid.getText().trim();
        if (telStr.isEmpty()) {
            ertel.setText("Le numéro de téléphone ne peut pas être vide.");
            ertel.setTextFill(javafx.scene.paint.Color.RED);
        } else if (!telStr.matches("\\d{8,}")) {
            ertel.setText("Numéro de téléphone invalide.");
            ertel.setTextFill(javafx.scene.paint.Color.RED);
        } else {
            ertel.setText("");
            ertel.setTextFill(javafx.scene.paint.Color.BLACK);
        }
    }

    private void validateDate() {
        if (dateid.getValue() == null) {
            erdate.setText("La date de naissance est obligatoire.");
            erdate.setTextFill(javafx.scene.paint.Color.RED);
        } else {
            erdate.setText("");
            erdate.setTextFill(javafx.scene.paint.Color.BLACK);
        }
    }


    public void handleInscription(ActionEvent event) {
        validateUsername();
        validateEmail();
        validatePassword();
        validatePhone();
        validateDate();

        if (!erusername.getText().isEmpty() || !eremail.getText().isEmpty() ||
                !ermotdepasse.getText().isEmpty() || !ertel.getText().isEmpty() ||
                !erdate.getText().isEmpty()) {

            showAlert(AlertType.ERROR, "Erreur", "Veuillez corriger les erreurs avant de continuer.");
            return;
        }

        String username = prenomid.getText().trim();
        String email = mailid.getText().trim();
        String password = passeid.getText().trim();
        String telStr = telid.getText().trim();
        LocalDate dateNaissance = dateid.getValue();

        try {
            int numTel = Integer.parseInt(telStr);
            Date sqlDate = Date.valueOf(dateNaissance);
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            User newUser = new User(username, email, hashedPassword, "user", "nok", numTel, sqlDate);

            serviceUser.ajouter(newUser);
            showAlert(AlertType.INFORMATION, "Succès", "Utilisateur ajouté avec succès !");
            clearFields(); // Effacer les champs après une inscription réussie

            // Redirection vers la page de connexion
            redirectToLogin(event);

        } catch (NumberFormatException e) {
            ertel.setText("Numéro de téléphone invalide.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible d'ajouter l'utilisateur : " + e.getMessage());
        }
    }

    private void redirectToLogin(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Login.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion"); // Titre de la nouvelle scène
            stage.show(); // Afficher la nouvelle scène

        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger la page de connexion : " + e.getMessage());
        }
    }
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        prenomid.clear();
        mailid.clear();
        passeid.clear();
        telid.clear();
        dateid.setValue(null);

        erusername.setText("");
        eremail.setText("");
        ermotdepasse.setText("");
        ertel.setText("");
        erdate.setText("");
    }
}
