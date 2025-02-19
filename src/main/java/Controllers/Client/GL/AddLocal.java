package Controllers.Client.GL;


import Models.Locaux;
import Services.LocauxService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

public class AddLocal {

    private final LocauxService locauxService = new LocauxService();

    @FXML
    private TextField TFAdresse;
    @FXML
    private TextField TFCapacite;
    @FXML
    private TextField TFType;
    @FXML
    private TextField TFPhoto;
    @FXML
    private TextField TFEquipements;
    @FXML
    private TextField TFTarifs;

    @FXML
    void ajouter(ActionEvent event) {
        try {
            String adresse = TFAdresse.getText();
            int capacite = Integer.parseInt(TFCapacite.getText());
            String type = TFType.getText();
            String photo = TFPhoto.getText();
            String equipements = TFEquipements.getText();
            BigDecimal tarifs = new BigDecimal(TFTarifs.getText());

            Locaux local = new Locaux(0, 1, adresse, capacite, type, photo, equipements, tarifs);

            locauxService.add(local);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText(" Local ajouté avec succès !");
            alert.showAndWait();

        } catch (SQLException e) {
            showError("Erreur SQL", e.getMessage());
        } catch (NumberFormatException e) {
            showError("Format invalide", " Veuillez entrer des valeurs valides !");
        }
    }

    @FXML
    /*void afficher(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AfficherLocaux.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Liste des Locaux");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }*/

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

