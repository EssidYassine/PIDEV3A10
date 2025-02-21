package Controllers.Admin.GL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.EventObject;

public class CrudLocauxContr {

    // Reusable method to switch scenes
    private void switchScene(ActionEvent actionEvent, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de " + fxmlFile + " !");
        }
    }

/*
    @FXML
    public void afficherGestionnaire(ActionEvent actionEvent) {
        switchScene(actionEvent, "/Views/Admin/GL/CrudLocaux.fxml", "Gestionnaire des Locaux");
    }*/

    @FXML
    private void ajouterLocaux(ActionEvent actionEvent) {
        switchScene(actionEvent, "/Views/Admin/GL/Add_Local.fxml", "Ajouter un Local");
    }

    public void consulterLocaux(ActionEvent actionEvent) {
        switchScene(actionEvent, "/Views/Admin/GL/Show_Local.fxml", "Liste des Locaux");
    }

    public void revenirFenetreHome(ActionEvent actionEvent) throws IOException {
        switchScene(actionEvent, "/Views/Admin/GL/Home.fxml", "Home ");

    }
        public void consultermodifier(ActionEvent actionEvent) {
        switchScene(actionEvent, "/Views/Admin/GL/Show_Modify.fxml", "Consultation et Modification");
    }
}
