package Controllers.Admin.GL;

import Models.Locaux;
import Services.LocauxService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifierLocal implements Initializable {

    @FXML
    private TextField TFAdresse, TFCapacite, TFTarifs;

    @FXML
    private ComboBox<String> CBType;

    @FXML
    private CheckBox chkWifi, chkCameras, chkEspaceTravail, chkCuisine, chkParking;

    @FXML
    private ImageView imageViewLocal;

    @FXML
    private Button btnChoisirImage, btnSave, btnCancel;

    private Locaux currentLocal;
    private String photoPath;
    private final LocauxService locauxService = new LocauxService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CBType.getItems().addAll("Salle de réunion", "Bureau", "Espace coworking", "Salle de conférence");

        if (btnChoisirImage != null) {
            btnChoisirImage.setOnAction(event -> choisirImage());
        } else {
            System.err.println("btnChoisirImage is NULL! Check FXML binding.");
        }


    }

    public void setLocal(Locaux local) {
        this.currentLocal = local;
        TFAdresse.setText(local.getAdresse());
        TFCapacite.setText(String.valueOf(local.getCapacite()));
        TFTarifs.setText(local.getTarifs().toString());
        CBType.setValue(local.getType());
        this.photoPath = local.getPhoto();

        if (photoPath != null && !photoPath.isEmpty()) {
            imageViewLocal.setImage(new Image(photoPath));
        } else {
            imageViewLocal.setImage(new Image(getClass().getResourceAsStream("/icons/default_image.png")));
        }

        // Set equipment checkboxes based on stored values
        String equipements = local.getEquipement();
        if (equipements != null) {
            chkWifi.setSelected(equipements.contains("Wifi"));
            chkCameras.setSelected(equipements.contains("Caméras de surveillance extérieures"));
            chkEspaceTravail.setSelected(equipements.contains("Espace de travail dédié"));
            chkCuisine.setSelected(equipements.contains("Cuisine"));
            chkParking.setSelected(equipements.contains("Parking"));
        }
    }
    @FXML

    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(imageViewLocal.getScene().getWindow());

        if (selectedFile != null) {
            try {
                File destination = new File("src/main/resources/images/" + selectedFile.getName());
                Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                photoPath = destination.toURI().toString();
                imageViewLocal.setImage(new Image(photoPath));
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'enregistrer l'image.");
            }
        }
    }
    @FXML


    private void saveChanges() {
        if (!validateInputs()) return;

        try {
            currentLocal.setAdresse(TFAdresse.getText());
            currentLocal.setCapacite(Integer.parseInt(TFCapacite.getText()));
            currentLocal.setType(CBType.getValue());

            // Collect selected equipment
            StringBuilder equipements = new StringBuilder();
            if (chkWifi.isSelected()) equipements.append("Wifi, ");
            if (chkCameras.isSelected()) equipements.append("Caméras de surveillance extérieures, ");
            if (chkEspaceTravail.isSelected()) equipements.append("Espace de travail dédié, ");
            if (chkCuisine.isSelected()) equipements.append("Cuisine, ");
            if (chkParking.isSelected()) equipements.append("Parking, ");
            if (equipements.length() > 0) {
                equipements.delete(equipements.length() - 2, equipements.length());
            }

            currentLocal.setEquipement(equipements.toString());
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
                || CBType.getValue() == null) {
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
    @FXML

    private void cancelChanges() {
        TFAdresse.getScene().getWindow().hide();
    }

    public void gotohome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/Home.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Locaux");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de Home.fxml !");
        }
    }

    public void gotocrudLocaux(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/CrudLocaux.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Locaux");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de CrudLocaux.fxml !");
        }
    }

    public void gotoafficherLocaux(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/AfficherLocaux.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Locaux");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de ModifierLocaux.fxml !");
        }
    }
}
