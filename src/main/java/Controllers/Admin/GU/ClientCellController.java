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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Optional;

public class ClientCellController {

    @FXML
    private Label usernameLabel, emailLabel, roleLabel, isActiveLabel, numTelLabel, dateNaissanceLabel;

    @FXML
    private Button deleteButton, activebutton;

    @FXML
    private Circle circle;
    private ServiceUser userService;
    private User currentUser;

    public void initialize() {
        userService = new ServiceUser();

        deleteButton.setOnAction(event -> {
            if (currentUser != null) {
                supprimerUtilisateur(currentUser.getId());
            }
        });

        activebutton.setOnAction(event -> {
            if (currentUser != null) {
                toggleUserStatus();
            }
        });
    }

    /**
     * Change le statut de l'utilisateur entre "active" et "desactive"
     */
    private void toggleUserStatus() {
        if (currentUser == null) return;

        String newStatus = "active".equalsIgnoreCase(currentUser.getIsActive()) ? "desactive" : "active";

        userService.updateIsActive(currentUser.getId(), newStatus);

        // Mettre à jour le statut localement
        currentUser.setIsActive(newStatus);
        isActiveLabel.setText(newStatus);

        // Mettre à jour l'affichage
        updateActiveButtonText();
        updateStatusColor();
    }

    /**
     * Met à jour le texte du bouton selon le statut de l'utilisateur
     */
    private void updateActiveButtonText() {
        if (currentUser != null) {
            activebutton.setText("active".equalsIgnoreCase(currentUser.getIsActive()) ? "Désactiver" : "Activer");
        }
    }

    /**
     * Met à jour la couleur du Pane en fonction du statut de l'utilisateur
     */
    private void updateStatusColor() {
        if (currentUser != null) {
            String color = "active".equalsIgnoreCase(currentUser.getIsActive()) ? "green" : "red";
            circle.setFill(Paint.valueOf(color)); // Mise à jour de la couleur du cercle
        }
    }

    /**
     * Définit les données de l'utilisateur et met à jour l'affichage
     * @param user L'utilisateur associé à cet élément de la liste
     */
    public void setUserData(User user) {
        currentUser = user;
        usernameLabel.setText(user.getUsername());
        emailLabel.setText(user.getEmail());
        roleLabel.setText(user.getRole());
        isActiveLabel.setText(user.getIsActive());
        numTelLabel.setText(String.valueOf(user.getNumTel()));
        dateNaissanceLabel.setText(String.valueOf(user.getDateDeNaissance()));

        // Mettre à jour l'affichage
        updateActiveButtonText();
        updateStatusColor();
    }

    /**
     * Supprime un utilisateur après confirmation
     * @param id ID de l'utilisateur à supprimer
     */
    private void supprimerUtilisateur(int id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.supprimer(id);
                Stage stage = (Stage) deleteButton.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GU/ListUsers.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                System.out.println("Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }
}
