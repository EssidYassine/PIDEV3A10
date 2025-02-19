package Controllers.Admin.GL;

import Models.Locaux;
import Services.LocauxService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

public class AddLocal {

    @FXML
    private TextField TFAdresse, TFCapacite, TFType, TFEquipements, TFTarifs;

    @FXML
    private ImageView imageViewLocal;

    @FXML
    private Button btnChoisirImage;

    private String photoPath; // Store the path of the selected image

    private final LocauxService locauxService = new LocauxService();

    @FXML
    void ajouter(ActionEvent event) {
        try {
            String adresse = TFAdresse.getText();
            int capacite = Integer.parseInt(TFCapacite.getText());
            String type = TFType.getText();
            String equipements = TFEquipements.getText();
            BigDecimal tarifs = new BigDecimal(TFTarifs.getText());

            // Use the stored photoPath
            Locaux local = new Locaux(0, 1, adresse, capacite, type, photoPath, equipements, tarifs);
            locauxService.add(local);

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

            Stage stage = (Stage) TFAdresse.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des Locaux");
            stage.show();

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