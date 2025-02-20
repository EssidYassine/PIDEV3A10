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

    private ServiceUser userService;
    private User currentUser;

    public void initialize() {
        userService = new ServiceUser();

        deleteButton.setOnAction(event -> {
            if (currentUser != null) {
                supprimerUtilisateur(currentUser.getId());
            } else {
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
        System.out.println("voila l id "+id);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.supprimer(id);

                Stage stage = (Stage) deleteButton.getScene().getWindow();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GU/ListUsers.fxml")); // Chemin vers votre fichier FXML
                Parent root = loader.load();

                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                // ... (gestion des erreurs)
            }
        }
    }

    public interface OnUserDeleted {
        void onUserDeleted();
    }

    private OnUserDeleted onUserDeleted;

    public void setOnUserDeleted(OnUserDeleted listener) {
        this.onUserDeleted = listener;
    }
}