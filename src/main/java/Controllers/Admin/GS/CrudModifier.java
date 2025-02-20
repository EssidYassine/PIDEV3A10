package Controllers.Admin.GS;

import Models.Service;
import Services.ServiceService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CrudModifier implements Initializable {

    @FXML
    private TextField nomField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField prixField;

    @FXML
    private ImageView serviceImage;

    @FXML
    private Button changeImageButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private Service currentService;
    private String imageUrl;  // Pour stocker le chemin de la nouvelle image

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Action pour changer l'image
        changeImageButton.setOnAction(event -> changeImage());

        // Action pour enregistrer les modifications
        saveButton.setOnAction(event -> saveChanges());

        // Action pour annuler les modifications
        cancelButton.setOnAction(event -> cancelChanges());
    }

    public void setDetails(String nom, String description, String prix, String imageUrl) {
        nomField.setText(nom);
        descriptionField.setText(description);
        prixField.setText(prix);
        this.imageUrl = imageUrl;

        // Affichage de l'image actuelle
        if (imageUrl != null && !imageUrl.isEmpty()) {
            serviceImage.setImage(new Image(imageUrl));
        } else {
            serviceImage.setImage(new Image(getClass().getResourceAsStream("/icons/default_service.png")));
        }
    }

    private void changeImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(serviceImage.getScene().getWindow());

        if (selectedFile != null) {
            imageUrl = selectedFile.toURI().toString();
            serviceImage.setImage(new Image(imageUrl));
        }
    }

    private void saveChanges() {
        try {
            currentService.setNom_service(nomField.getText());
            currentService.setDescription(descriptionField.getText());
            currentService.setPrix((int) Double.parseDouble(prixField.getText()));
            currentService.setImage_url(imageUrl);

            // Appelle la méthode de mise à jour de la base de données
            ServiceService serviceService = new ServiceService();
            serviceService.update(currentService);

            // Affiche une alerte pour confirmer la modification
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modification réussie");
            alert.setHeaderText(null);
            alert.setContentText("Les modifications ont été enregistrées avec succès !");
            alert.showAndWait();
        } catch (NumberFormatException | SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Le prix doit être un nombre !");
            alert.showAndWait();
        }
    }

    private void cancelChanges() {
        // Ferme la fenêtre de modification
        nomField.getScene().getWindow().hide();
    }

    public void setService(Service service) {
        this.currentService = service;
        setDetails(service.getNom_service(), service.getDescription(), String.valueOf(service.getPrix()), service.getImage_url());
    }
}
