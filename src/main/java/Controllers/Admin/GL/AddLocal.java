package Controllers.Admin.GL;

import Models.Locaux;
import Services.LocauxService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddLocal implements Initializable {

    @FXML
    private ComboBox<String> CBType;
    @FXML
    private TextField TFAdresse, TFCapacite, TFTarifs;
    @FXML
    private ImageView imageViewLocal;
    @FXML
    private Button btnChoisirImage;

    // Declare checkboxes for equipment
    @FXML
    private CheckBox chkWifi, chkCameras, chkEspaceTravail, chkCuisine, chkParking;

    private String photoPath; // Store the path of the selected image
    private final LocauxService locauxService = new LocauxService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize ComboBox with types
        CBType.getItems().addAll("Salle de réunion", "Bureau", "Espace coworking", "Salle de conférence");
        CBType.setValue("Type");
    }

    @FXML
    void ajouter(ActionEvent event) {
        try {
            String adresse = TFAdresse.getText();
            int capacite = Integer.parseInt(TFCapacite.getText());
            String type = CBType.getValue();

            // Collect selected equipment as a string
            StringBuilder equipements = new StringBuilder();
            if (chkWifi.isSelected()) equipements.append("Wifi, ");
            if (chkCameras.isSelected()) equipements.append("Caméras de surveillance extérieures, ");
            if (chkEspaceTravail.isSelected()) equipements.append("Espace de travail dédié, ");
            if (chkCuisine.isSelected()) equipements.append("Cuisine, ");
            if (chkParking.isSelected()) equipements.append("Parking, ");

            // Remove the last comma and space if there are any selected
            if (equipements.length() > 0) {
                equipements.delete(equipements.length() - 2, equipements.length());
            }

            BigDecimal tarifs = new BigDecimal(TFTarifs.getText());

            // Create the Locaux object with selected equipment
            Locaux local = new Locaux(0, 1, adresse, capacite, type, photoPath, equipements.toString(), tarifs);
            locauxService.add(local);

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Local ajouté avec succès !");
            alert.showAndWait();

            chargerShowLocal();

        } catch (SQLException e) {
            showError("Erreur SQL", e.getMessage());
        } catch (NumberFormatException e) {
            showError("Format invalide", "Veuillez entrer des valeurs valides !");
        }
    }

    @FXML
    void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            photoPath = selectedFile.toURI().toString(); // Store the photo path
            Image image = new Image(photoPath);
            imageViewLocal.setImage(image);
        }
    }

    private void chargerShowLocal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/Show_Local.fxml"));
            Scene scene = new Scene(loader.load());

            Stage newStage = new Stage(); // Create a new window
            newStage.setScene(scene);
            newStage.setTitle("Liste des Locaux");
            newStage.show();

            // Optional: Close the current window
            Stage currentStage = (Stage) TFAdresse.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showError("Erreur de chargement", "Impossible d'ouvrir Show_Local.fxml");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
