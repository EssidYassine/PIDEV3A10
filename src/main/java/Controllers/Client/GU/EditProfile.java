package Controllers.Client.GU;

import Services.ServiceUser;
import javafx.event.ActionEvent;
import Models.Session;
import Models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.regex.Pattern;

public class EditProfile {

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
    private Label erusername;
    @FXML
    private Label eremail;
    @FXML
    private Label ermotdepasse;
    @FXML
    private Label ertel;
    @FXML
    private Label erdate;
    @FXML
    private Button validerid;
    private final ServiceUser serviceUser = new ServiceUser();

    @FXML
    public void initialize() {
        backflech.setOnMouseClicked(event -> gotodetails());
        User currentUser = Session.getUser();

        if (currentUser != null) {
            prenomid.setText(currentUser.getUsername());
            mailid.setText(currentUser.getEmail());
            passeid.setText(currentUser.getPassword());
            telid.setText(String.valueOf(currentUser.getNumTel()));

            if (currentUser.getDateDeNaissance() != null) {
                LocalDate localDate = convertDateToLocalDate(currentUser.getDateDeNaissance());
                dateid.setValue(localDate);
            }

            prenomid.textProperty().addListener((obs, oldVal, newVal) -> validateUsername());
            mailid.textProperty().addListener((obs, oldVal, newVal) -> validateEmail());
            passeid.textProperty().addListener((obs, oldVal, newVal) -> validatePassword());
            telid.textProperty().addListener((obs, oldVal, newVal) -> validatePhone());
            dateid.valueProperty().addListener((obs, oldVal, newVal) -> validateDate());
        } else {
            showAlert(AlertType.WARNING, "Avertissement", "Aucun utilisateur connecté.");
        }
    }

    private void gotodetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GU/UserDetails.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backflech.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de l'utilisateur");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleEditUser(ActionEvent event) {
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

            User currentUser = Session.getUser();
            String currentEmail = currentUser.getEmail();

            if (!email.equals(currentEmail) && serviceUser.emailExists(email)) {
                eremail.setText("Cet email est déjà utilisé.");
                eremail.setTextFill(javafx.scene.paint.Color.RED);
                return;
            }

            User updatedUser = new User(username, email, hashedPassword, "user", "active", numTel, sqlDate);
            updatedUser.setId(currentUser.getId());
            serviceUser.modifier(updatedUser);
            updatedUser.setPassword(password);
            Session.setUser(updatedUser);
            showAlert(AlertType.INFORMATION, "Succès", "Utilisateur modifié avec succès !");
            redirectToUserDetails();
            clearFields();

        } catch (NumberFormatException e) {
            ertel.setText("Numéro de téléphone invalide.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible de modifier l'utilisateur : " + e.getMessage());
        }
    }

    private LocalDate convertDateToLocalDate(Date date) {
        return date != null ? date.toLocalDate() : null;
    }

    private void validateUsername() {
        String username = prenomid.getText().trim();
        if (username.isEmpty()) {
            erusername.setText("Le nom d'utilisateur ne peut pas être vide.");
        } else if (username.length() < 4) {
            erusername.setText("Le nom d'utilisateur doit contenir au moins 4 caractères.");
        } else {
            erusername.setText("");
        }
    }

    private void validateEmail() {
        String email = mailid.getText().trim();
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (email.isEmpty()) {
            eremail.setText("L'email ne peut pas être vide.");
        } else if (!Pattern.matches(regex, email)) {
            eremail.setText("Format d'email invalide.");
        } else {
            eremail.setText("");
        }
    }

    private void validatePassword() {
        String password = passeid.getText().trim();
        if (password.isEmpty()) {
            ermotdepasse.setText("Le mot de passe ne peut pas être vide.");
        } else if (password.length() < 8) {
            ermotdepasse.setText("Le mot de passe doit contenir au moins 8 caractères.");
        } else {
            ermotdepasse.setText("");
        }
    }

    private void validatePhone() {
        String telStr = telid.getText().trim();
        if (telStr.isEmpty()) {
            ertel.setText("Le numéro de téléphone ne peut pas être vide.");
        } else if (!telStr.matches("\\d{8,}")) {
            ertel.setText("Numéro de téléphone invalide.");
        } else {
            ertel.setText("");
        }
    }

    private void validateDate() {
        LocalDate selectedDate = dateid.getValue();

        if (selectedDate == null) {
            erdate.setText("La date de naissance est obligatoire.");
            erdate.setTextFill(javafx.scene.paint.Color.RED);
        } else {
            LocalDate today = LocalDate.now();
            int age = Period.between(selectedDate, today).getYears();

            if (age < 10) {
                erdate.setText("L'âge minimum doit être de 10 ans.");
                erdate.setTextFill(javafx.scene.paint.Color.RED);
            } else {
                erdate.setText("");
                erdate.setTextFill(javafx.scene.paint.Color.BLACK);
            }
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

    private void redirectToUserDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GU/UserDetails.fxml"));
            Parent root = loader.load();
            UserDetails controller = loader.getController(); // Récupérer le contrôleur de la page de détails
            controller.loadUserDetails(Session.getUser()); // Passer l'utilisateur mis à jour au contrôleur

            Stage stage = (Stage) validerid.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de l'utilisateur");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
