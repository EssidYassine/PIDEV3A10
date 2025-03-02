package Controllers.Admin.GU;
import Models.User;
import Services.ServiceUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import mailling.SendEmail;

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
    private ListUsers listUsersController;

    public void setListUsersController(ListUsers controller) {
        this.listUsersController = controller;
    }
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


    private void toggleUserStatus() {
        if (currentUser == null) return;

        String newStatus = "active".equalsIgnoreCase(currentUser.getIsActive()) ? "desactive" : "active";

        // Demander confirmation avant de désactiver un compte
        if ("desactive".equalsIgnoreCase(newStatus)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Voulez-vous vraiment désactiver ce compte ?");
            alert.setContentText("Une notification par email sera envoyée à l'utilisateur.");

            // Charger le fichier CSS pour l'Alert
            alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/styles/promptstyle.css").toExternalForm()
            );

            ButtonType yesButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(yesButton, noButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == yesButton) {
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Rédaction du mail");
                    dialog.setHeaderText("Ajoutez un message pour l'utilisateur (optionnel)");

                    // Charger le fichier CSS pour la boîte de dialogue personnalisée
                    dialog.getDialogPane().getStylesheets().add(
                            getClass().getResource("/styles/promptstyle.css").toExternalForm()
                    );

                    // Créer un TextArea pour le message
                    TextArea textArea = new TextArea();
                    textArea.setPromptText("Entrez votre message ici...");
                    textArea.setWrapText(true); // Activer le retour à la ligne
                    textArea.setPrefRowCount(5); // Définir la hauteur du TextArea

                    // Ajouter le TextArea à la boîte de dialogue
                    dialog.getDialogPane().setContent(textArea);

                    // Ajouter les boutons "Envoyer" et "Annuler"
                    ButtonType sendButton = new ButtonType("Envoyer", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().addAll(sendButton, ButtonType.CANCEL);

                    // Récupérer le texte saisi lorsque l'utilisateur clique sur "Envoyer"
                    dialog.setResultConverter(buttonType -> {
                        if (buttonType == sendButton) {
                            return textArea.getText();
                        }
                        return null;
                    });

                    // Afficher la boîte de dialogue et traiter la réponse
                    dialog.showAndWait().ifPresent(customMessage -> {
                        userService.updateIsActive(currentUser.getId(), newStatus);
                        currentUser.setIsActive(newStatus);
                        isActiveLabel.setText(newStatus);
                        updateActiveButtonText();
                        updateStatusColor();
                        if (listUsersController != null) {
                            listUsersController.refreshChart();
                        }

                        // Envoyer l'email avec le message personnalisé
                        SendEmail.sendAccountStatusEmail(currentUser.getEmail(), newStatus, customMessage);
                    });
                }
            });
        } else {
            // Activation directe sans confirmation
            userService.updateIsActive(currentUser.getId(), newStatus);
            currentUser.setIsActive(newStatus);
            isActiveLabel.setText(newStatus);
            updateActiveButtonText();
            updateStatusColor();
            if (listUsersController != null) {
                listUsersController.refreshChart();
            }
            SendEmail.sendAccountStatusEmail(currentUser.getEmail(), newStatus, "");
        }
    }




    private void updateActiveButtonText() {
        if (currentUser != null) {
            activebutton.setText("active".equalsIgnoreCase(currentUser.getIsActive()) ? "Désactiver" : "Activer");
        }
    }
    public User getUserData() {
        return currentUser; // Renvoie les données de l'utilisateur
    }

    /**
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
