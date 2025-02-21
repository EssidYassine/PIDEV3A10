package Controllers.Admin.GP;

import Models.Pack;
import Services.ServiceGP;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
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
    private ListView<Pack> packListView;
    @FXML
    private GridPane packGridPane;

    private ObservableList<Pack> packObservableList = FXCollections.observableArrayList();


    private final ServiceGP serviceGP = new ServiceGP();

    @FXML
    public void initialize() {
        try {
            System.out.println("Tentative de connexion à la base de données...");
            List<Pack> packs = serviceGP.getAll();
            System.out.println("Connected to Data Base");
            System.out.println("Packs récupérés : " + packs.size());

            if (packs.isEmpty()) {
                System.out.println("⚠ Aucun pack trouvé dans la base de données.");
                return;
            }


            packGridPane.getChildren().clear();

            int col = 0;
            int row = 0;

            for (Pack pack : packs) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GP/PackCell.fxml"));
                Parent root = loader.load();
                PackCellController controller = loader.getController();
                controller.setPack(pack);

                packGridPane.add(root, col, row);
                col++;
                if (col == 3) {
                    col = 0;
                    row++;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des packs : " + e.getMessage());
            showErrorAlert("Erreur d'initialisation", "Impossible de charger les packs.");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Une erreur inconnue est survenue.");
        }
    }

    @FXML
    private void handleGestionUsers(ActionEvent event) {
        navigateToScene(event, "/Views/Admin/GP/UserManagement.fxml");
    }

    @FXML
    private void handleGestionPacks(ActionEvent event) {
        navigateToScene(event, "/Views/Admin/GP/Pack.fxml");
    }

    @FXML
    private void handleAjouterClick(ActionEvent event) {
        navigateToScene(event, "/Views/Admin/GP/CreatePack.fxml");
    }

    @FXML
    private void handleUpdateClick(ActionEvent event) {
        navigateToScene(event, "/Views/Admin/GP/UpdatePack.fxml");
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
}
