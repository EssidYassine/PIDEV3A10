package Controllers.Admin.GS;

import Models.Service;
import Models.User;
import Services.ServiceService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ServiceController {

    @FXML
    private TextField nomServiceField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField prixField;

    @FXML
    private ComboBox<Service.TypeService> typeServiceCombo;

    @FXML
    private TextField quantiteMaterielField;

    @FXML
    private TextField roleStaffField;

    @FXML
    private TextField experienceField;

    @FXML
    private ImageView imageView;

    @FXML
    private GridPane gridPaneServices;  // GridPane pour afficher les services

    private String imageUrl = null;

    private final ServiceService serviceService = new ServiceService();

    @FXML
    public void initialize() {
        typeServiceCombo.getItems().setAll(Service.TypeService.values());
        try {
            chargerServices();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode pour choisir une image depuis le système de fichiers
     */
    @FXML
    private void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imageUrl = file.toURI().toString();
            imageView.setImage(new Image(imageUrl));
        }
    }

    /**
     * Méthode pour ajouter un nouveau service
     */
    @FXML
    private void ajouterService(ActionEvent event) {
        try {
            String nomService = nomServiceField.getText().trim();
            String description = descriptionField.getText().trim();
            String prixText = prixField.getText().trim();
            Service.TypeService typeService = typeServiceCombo.getValue();
            String quantiteMaterielText = quantiteMaterielField.getText().trim();
            String roleStaff = roleStaffField.getText().trim();
            String experience = experienceField.getText().trim();

            User utilisateur = new User(1, "Dupont", "Jean", "jean.dupont@example.com", "emna");

            if (nomService.isEmpty() || description.isEmpty() || typeService == null) {
                showErrorAlert("Champs obligatoires", "Veuillez remplir tous les champs requis.");
                return;
            }

            int prix = Integer.parseInt(prixText);
            int quantiteMateriel = Integer.parseInt(quantiteMaterielText);

            int disponibilite = 1;

            Service service = new Service(0, nomService, description, prix, typeService, disponibilite, utilisateur, imageUrl, quantiteMateriel, roleStaff, experience);

            serviceService.add(service);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Service ajouté avec succès !");
            alert.showAndWait();

            chargerServices();
            resetForm();

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur SQL", "Impossible d'ajouter le service : " + e.getMessage());
        }
    }

    /**
     * Méthode pour charger et afficher les services dans le GridPane
     */
    private void chargerServices() throws SQLException {
        gridPaneServices.getChildren().clear();
        List<Service> services = serviceService.getAll();
        int row = 0;
        int column = 0;

        for (Service service : services) {
            VBox card = new VBox();
            card.setSpacing(10);
            card.setStyle("-fx-background-color: #1f2c50; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;");

            // Image du service
            ImageView imgView = new ImageView();
            imgView.setFitWidth(150);
            imgView.setFitHeight(100);
            imgView.setPreserveRatio(true);

            try {
                if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
                    Image image = new Image((String) service.getImageUrl(), true);
                    if (image.getException() == null) {
                        imgView.setImage(image);
                    } else {
                        throw image.getException();
                    }
                } else {
                    imgView.setImage(new Image("default-image-url.png", true));
                }
            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'image : " + e.getMessage());
                imgView.setImage(new Image("default-image-url.png", true));
            }

            card.getChildren().add(imgView);

            // Nom du service
            Label nomLabel = new Label(service.getNom());
            nomLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            card.getChildren().add(nomLabel);

            // Prix du service
            Label prixLabel = new Label("Prix : " + service.getPrix() + " €");
            card.getChildren().add(prixLabel);

            gridPaneServices.add(card, column, row);
            column++;
            if (column == 2) {
                column = 0;
                row++;
            }
        }
    }

    /**
     * Réinitialisation du formulaire
     */
    private void resetForm() {
        nomServiceField.clear();
        descriptionField.clear();
        prixField.clear();
        typeServiceCombo.setValue(null);
        quantiteMaterielField.clear();
        roleStaffField.clear();
        experienceField.clear();
        imageView.setImage(null);
        imageUrl = null;
    }

    /**
     * Affichage d'une alerte d'erreur
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void revenirFenetreHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/Home.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }
}
