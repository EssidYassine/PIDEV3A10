package Controllers.Admin.GL;

import Models.Locaux;
import Services.LocauxService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CrudModify implements Initializable {

    @FXML
    private TextField TFAdresse, TFCapacite, TFTarifs;

    @FXML
    private ComboBox<String> CBType, CBEquipements;

    @FXML
    private ImageView imageViewLocal;

    @FXML
    private Button btnChoisirImage, btnSave, btnCancel;

    private Locaux currentLocal;
    private String photoPath;
    private final LocauxService locauxService = new LocauxService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CBEquipements.getItems().addAll("projecteur", "matériel Sono", "climatiseur", "cuisine");
        CBType.getItems().addAll("Salle de réunion", "Bureau", "Espace coworking", "Salle de conférence");

        btnChoisirImage.setOnAction(event -> choisirImage());
        btnSave.setOnAction(event -> saveChanges());
        btnCancel.setOnAction(event -> cancelChanges());
    }

    public void setLocal(Locaux local) {
        this.currentLocal = local;
        TFAdresse.setText(local.getAdresse());
        TFCapacite.setText(String.valueOf(local.getCapacite()));
        TFTarifs.setText(local.getTarifs().toString());
        CBType.setValue(local.getType());
        CBEquipements.setValue(local.getEquipement());
        this.photoPath = local.getPhoto();

        if (photoPath != null && !photoPath.isEmpty()) {
            imageViewLocal.setImage(new Image(photoPath));
        } else {
            imageViewLocal.setImage(new Image(getClass().getResourceAsStream("/icons/default_image.png")));
        }
    }

    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(imageViewLocal.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Sauvegarde l'image dans le projet
                File destination = new File("src/main/resources/images/" + selectedFile.getName());
                Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                photoPath = destination.toURI().toString();
                imageViewLocal.setImage(new Image(photoPath));
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'enregistrer l'image.");
            }
        }
    }

    private void saveChanges() {
        if (!validateInputs()) return;

        try {
            currentLocal.setAdresse(TFAdresse.getText());
            currentLocal.setCapacite(Integer.parseInt(TFCapacite.getText()));
            currentLocal.setType(CBType.getValue());
            currentLocal.setEquipement(CBEquipements.getValue());
            currentLocal.setTarifs(new BigDecimal(TFTarifs.getText()));
            currentLocal.setPhoto(photoPath);

            locauxService.update(currentLocal);

            showAlert("Modification réussie", "Les modifications ont été enregistrées avec succès !");
        } catch (SQLException e) {
            showAlert("Erreur SQL", "Impossible d'enregistrer les modifications.");
        }
    }

    private boolean validateInputs() {
        if (TFAdresse.getText().isEmpty() || TFCapacite.getText().isEmpty() || TFTarifs.getText().isEmpty()
                || CBType.getValue() == null || CBEquipements.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return false;
        }
        try {
            Integer.parseInt(TFCapacite.getText());
            new BigDecimal(TFTarifs.getText());
        } catch (NumberFormatException e) {
            showAlert("Erreur de format", "Capacité et tarifs doivent être des nombres valides.");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void cancelChanges() {
        TFAdresse.getScene().getWindow().hide();
    }
}
