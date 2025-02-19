package Controllers.Client.GU;

import Models.User;
import Services.ServiceUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Date;
import java.time.LocalDate;

public class inscription {

    @FXML
    private TextField prenomid; // Correspond maintenant à "username"

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



    private final ServiceUser serviceUser = new ServiceUser(); // Service pour gérer les utilisateurs

    @FXML
    private void handleInscription(ActionEvent event) {
        String username = prenomid.getText().trim();
        String email = mailid.getText().trim();
        String password = passeid.getText().trim();
        String telStr = telid.getText().trim();
        LocalDate dateNaissance = dateid.getValue();

        // 2️⃣ Vérification des champs obligatoires
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || telStr.isEmpty() || dateNaissance == null) {
            showAlert(AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        // 3️⃣ Validation du format de l'email
        if (!isValidEmail(email)) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez entrer une adresse email valide.");
            return;
        }

        // 4️⃣ Vérification du numéro de téléphone
        int numTel;
        try {
            numTel = Integer.parseInt(telStr);
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur", "Le numéro de téléphone doit être un nombre valide.");
            return;
        }

        // 5️⃣ Conversion de la date
        Date sqlDate = Date.valueOf(dateNaissance);

        // 🔐 6️⃣ Hashage du mot de passe
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // 7️⃣ Création de l'objet utilisateur avec le mot de passe hashé
        User newUser = new User(username, email, hashedPassword, "user", "nok", numTel, sqlDate);

        // 8️⃣ Ajout dans la base de données
        try {
            serviceUser.ajouter(newUser);
            showAlert(AlertType.INFORMATION, "Succès", "Utilisateur ajouté avec succès !");
            clearFields();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible d'ajouter l'utilisateur : " + e.getMessage());
        }
    }
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
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
    }
}
