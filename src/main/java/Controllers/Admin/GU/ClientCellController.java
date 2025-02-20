package Controllers.Admin.GU;

import Models.User;
import Services.ServiceUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;

public class ClientCellController {

    @FXML
    private ImageView userImageView;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label isActiveLabel;

    @FXML
    private Label numTelLabel;

    @FXML
    private Label dateNaissanceLabel;

    @FXML
    private Button deleteButton;
    @FXML
    private Button activebutton;

    private ServiceUser userService; // Add ServiceUser field
    private User currentUser; // Store the current user for this cell

    public void initialize() {
        userService = new ServiceUser(); // Initialize the ServiceUser

        deleteButton.setOnAction(event -> {
            if (currentUser != null) {  // Check if user data is set
                supprimerUtilisateur(currentUser.getId());
            } else {
                // Handle the case where user data is not set (e.g., log a warning)
                System.out.println("No user data available for deletion.");
            }
        });
    }

    public void setUserData(User user) {
        currentUser = user; // Store the User object for this cell
        usernameLabel.setText(user.getUsername());
        emailLabel.setText(user.getEmail());
        roleLabel.setText(user.getRole());
        isActiveLabel.setText(user.getIsActive());
        numTelLabel.setText(String.valueOf(user.getNumTel()));
        dateNaissanceLabel.setText(String.valueOf(user.getDateDeNaissance()));
    }


    private void supprimerUtilisateur(int id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.supprimer(id);

                // *** RECHARGEMENT DE LA PAGE ***
                // 1. Récupérer la scène et la fenêtre
                Stage stage = (Stage) deleteButton.getScene().getWindow();

                // 2. Créer un nouvel FXMLLoader pour la même page
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GU/ListUsers.fxml")); // Chemin vers votre fichier FXML
                Parent root = loader.load();

                // 3. Définir la nouvelle scène sur la fenêtre
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                // ... (gestion des erreurs)
            }
        }
    }

    // Callback interface for user deletion
    public interface OnUserDeleted {
        void onUserDeleted();
    }

    private OnUserDeleted onUserDeleted;

    public void setOnUserDeleted(OnUserDeleted listener) {
        this.onUserDeleted = listener;
    }
}