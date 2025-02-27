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
    @FXML
    private CheckBox chkWifi, chkCameras, chkEspaceTravail, chkCuisine, chkParking;

    private String photoPath;
    private final LocauxService locauxService = new LocauxService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CBType.getItems().addAll("Salle de réunion", "Bureau", "Espace coworking", "Salle de conférence");
        CBType.setValue("Type");
    }

    @FXML
    void ajouter(ActionEvent event) {
        try {
            String adresse = TFAdresse.getText();
            int capacite = Integer.parseInt(TFCapacite.getText());
            String type = CBType.getValue();

            StringBuilder equipements = new StringBuilder();
            if (chkWifi.isSelected()) equipements.append("Wifi, ");
            if (chkCameras.isSelected()) equipements.append("Caméras de surveillance extérieures, ");
            if (chkEspaceTravail.isSelected()) equipements.append("Espace de travail dédié, ");
            if (chkCuisine.isSelected()) equipements.append("Cuisine, ");
            if (chkParking.isSelected()) equipements.append("Parking, ");

            if (capacite < 0) {
                showError("Valeur invalide", "La capacité ne peut pas être négative !");
                return;
            }

            if (equipements.length() > 0) {
                equipements.delete(equipements.length() - 2, equipements.length());
            }

            BigDecimal tarifs = new BigDecimal(TFTarifs.getText());

            Locaux local = new Locaux(0, 1, adresse, capacite, type, photoPath, equipements.toString(), tarifs);
            locauxService.add(local);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Local ajouté avec succès !");
            alert.showAndWait();

            rafraichirShowLocal();

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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            photoPath = selectedFile.toURI().toString();
            Image image = new Image(photoPath);
            imageViewLocal.setImage(image);
        }
    }

    private void rafraichirShowLocal() {
        try {
            // Get the current window's stage
            Stage stage = (Stage) TFAdresse.getScene().getWindow();

            // Load the Show_Local.fxml into the same window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/Show_Local.fxml"));
            Parent root = loader.load();

            stage.setScene(new Scene(root));
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

    public void retour1(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/CrudLocaux.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("DashBoard ");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }




}
