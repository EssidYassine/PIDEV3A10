package Controllers.Admin.GP;

import Models.Pack;
import Services.ServiceGP;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class HomeController {

    @FXML
    private Button btnClasses;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnSettings;

    @FXML
    private Button btnStudents;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btn_Timetable;

    @FXML
    private Button btn_Timetable1;

    @FXML
    private ListView<Pack> packListView; // Assurez-vous que cet ID correspond à votre fichier FXML

    private ObservableList<Pack> packObservableList = FXCollections.observableArrayList();

    @FXML
    private GridPane packGridPane;

    @FXML
    public void initialize() {
        refreshPackList(); // Charge les packs à l'initialisation
        try {
            ServiceGP serviceGP = new ServiceGP();
            List<Pack> packs = serviceGP.getAll();
            System.out.println("Packs récupérés : " + packs);

            packObservableList.setAll(packs);
            packGridPane.getChildren().clear(); // Vider la grille avant d'ajouter les packs

            int col = 0;
            int row = 0;

            for (Pack pack : packObservableList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GP/PackCell.fxml"));
                Pane root = loader.load();
                PackCellController cellController = loader.getController();
                cellController.setPack(pack, packObservableList);

                packGridPane.add(root, col, row); // Ajout à la grille
                col++;

                if (col == 3) { // Changer de ligne après 3 colonnes
                    col = 0;
                    row++;
                }
            }
        } catch (Exception e) {
            showErrorAlert("Erreur d'initialisation", "Erreur : " + e.getMessage());
        }
    }


    @FXML
    private void handleGestionUsers(ActionEvent event) {
        navigateToScene(event, "/Views/Admin/GP/UserManagement.fxml");
    }

    @FXML
    private void handleGestionPacks(ActionEvent event) {
        refreshPackList(); // Rafraîchissez la liste avant de naviguer
        navigateToScene(event, "/Views/Admin/GP/Pack.fxml");
    }

    @FXML
    private void handleAjouterClick(ActionEvent event) {
        navigateToScene(event, "/Views/Admin/GP/CreatePack.fxml");
    }

    private void navigateToScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de chargement", "Erreur lors du chargement du fichier FXML : " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void refreshPackList() {
        try {
            ServiceGP serviceGP = new ServiceGP();
            List<Pack> packs = serviceGP.getAll(); // Récupérer tous les packs
            packObservableList.setAll(packs); // Mettre à jour la liste observable
        } catch (Exception e) {
            showErrorAlert("Erreur de rafraîchissement", "Une erreur est survenue lors du rafraîchissement de la liste des packs : " + e.getMessage());
        }
    }

}
