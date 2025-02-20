package event.aura.Eventus.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import event.aura.Eventus.Models.User;
import event.aura.Eventus.Services.UserService;


public class SidebarController {

    @FXML
    private VBox activityMenu;
    @FXML
    private VBox herbergementMenu;
    @FXML
    private VBox menuContainer;

    @FXML
    private Button getAllActivityButton;

    @FXML
    private Button MyActivityButton;
    @FXML
    private Button addActivityButton;

    @FXML
    private Label userStatusLabel;


    private boolean isActivityMenuVisible = false;
    private boolean isHerbergementMenuVisible = false;

    @FXML
    public void initialize() {
        activityMenu.setManaged(false);
        herbergementMenu.setManaged(false);
    }


    // Méthode pour gérer l'ouverture et la fermeture du menu Activity
    @FXML
    private void toggleActivityMenu() {
        // Toggle activity menu
        isActivityMenuVisible = !isActivityMenuVisible;
        activityMenu.setVisible(isActivityMenuVisible);
        activityMenu.setManaged(isActivityMenuVisible); // This is key for proper layout

        // Close herbergement menu if it's open
        if (isHerbergementMenuVisible) {
            herbergementMenu.setVisible(false);
            herbergementMenu.setManaged(false);
            isHerbergementMenuVisible = false;
        }

        // Enable/disable buttons based on menu visibility
        disableActivityButtons(!isActivityMenuVisible);
    }

    // Méthode pour gérer l'ouverture et la fermeture du menu Herbergement
    @FXML
    private void toggleHerbergementMenu() {
        // Toggle herbergement menu
        isHerbergementMenuVisible = !isHerbergementMenuVisible;
        herbergementMenu.setVisible(isHerbergementMenuVisible);
        herbergementMenu.setManaged(isHerbergementMenuVisible); // This is key for proper layout

        // Close activity menu if it's open
        if (isActivityMenuVisible) {
            activityMenu.setVisible(false);
            activityMenu.setManaged(false);
            isActivityMenuVisible = false;
            disableActivityButtons(true);
        }
    }

    // Méthode pour désactiver ou activer les boutons du menu Activity
    private void disableActivityButtons(boolean disable) {
        getAllActivityButton.setDisable(disable);
        MyActivityButton.setDisable(disable);
        addActivityButton.setDisable(disable);
    }

    // Méthode pour la navigation vers "Add Actuality"
    @FXML
    private void navigateToaddActuality(ActionEvent event) {
        try {
            // Charger la nouvelle scène pour "My Activity"
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/actuality.fxml"));
            AnchorPane root = loader.load();

            // Créer une nouvelle scène et l'appliquer à la scène actuelle
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour la navigation vers "Get All Activity"
    @FXML
    private void navigateToGetAllActivity(ActionEvent event) {
        try {
            // Charger la nouvelle scène pour "Get All Activity"
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/available.fxml"));
            AnchorPane root = loader.load();

            // Créer une nouvelle scène et l'appliquer à la scène actuelle
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour la navigation vers "My Activity"
    @FXML
    private void navigateToMyActivity(ActionEvent event) {
        try {
            // Charger la nouvelle scène pour "My Activity"
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/posts.fxml"));
            AnchorPane root = loader.load();

            // Créer une nouvelle scène et l'appliquer à la scène actuelle
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void viewHerbergement() {
        System.out.println("View Service clicked");
    }

    @FXML
    private void addHerbergement() {
        // Logique pour ajouter un hébergement
        System.out.println("Add Service clicked");
    }
}
