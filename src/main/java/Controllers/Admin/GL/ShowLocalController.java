package Controllers.Admin.GL;

import Models.Locaux;
import Services.LocauxService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ShowLocalController {

    public TextField searchField;
    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private GridPane gridPane;
    private List<Locaux> allLocaux;
    private List<Locaux> filteredLocaux;  // Class-level variable to store filtered Locaux


    private final LocauxService locauxService = new LocauxService();

    @FXML
    public void initialize() {
        try {
            allLocaux = locauxService.getAll();
            filteredLocaux = allLocaux;
            afficherLocaux();

            sortComboBox.getItems().addAll();

            sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    sortLocaux(newVal);  // Call the sorting method with the selected option
                }
            });

            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    searchLocaux(newVal);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchLocaux(String searchText) throws SQLException {
        final String finalSearchText = searchText.toLowerCase();

        // Filter the list based on the search text
        filteredLocaux = allLocaux.stream()
                .filter(local -> local.getAdresse().toLowerCase().contains(finalSearchText))
                .toList();

        afficherLocaux();
    }

    private void afficherLocaux() throws SQLException {
        gridPane.getChildren().clear(); // Clear previous content

        int column = 0;
        int row = 0;

        for (Locaux local : filteredLocaux) { // Use filteredLocaux instead of fetching again
            VBox localBox = creerLocalBox(local);
            gridPane.add(localBox, column, row);

            column++;
            if (column > 2) { // 3 columns per row
                column = 0;
                row++;
            }
        }
    }

    private VBox creerLocalBox(Locaux local) {
        VBox localBox = new VBox(10);
        localBox.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-border-radius: 10; -fx-border-color: #1f2c50; -fx-alignment: center;");
        localBox.setPrefWidth(300);

        // Image du local
        ImageView localImage;
        if (local.getPhoto() != null && !local.getPhoto().isEmpty()) {
            localImage = new ImageView(new Image(local.getPhoto()));
        } else {
            // Load default image
            try {
                localImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/default_local.png"))));
            } catch (NullPointerException e) {
                System.err.println("Default local image not found!");
                localImage = new ImageView();
            }
        }

        localImage.setFitWidth(150);
        localImage.setFitHeight(100);

        Label adresseLabel = new Label(local.getAdresse());
        adresseLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Capacité du local
        Label capaciteLabel = new Label("Capacité: " + local.getCapacite());
        capaciteLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        // Tarifs du local
        Label tarifsLabel = new Label("Tarifs: " + local.getTarifs() + " TND");
        tarifsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        // Bouton Détail
        Button detailButton = new Button("Détail");
        detailButton.setStyle("-fx-background-color: #ed4e00; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-border-radius: 5;");
        detailButton.setOnAction(event -> afficherDetailsLocal(local));

        // Icône de suppression
        ImageView deleteIcon;
        try {
            deleteIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/pngtree-vector-trash-icon-png-image_865253.jpg"))));
        } catch (NullPointerException e) {
            System.err.println("Delete icon not found!");
            deleteIcon = new ImageView(); // Empty ImageView
        }
        deleteIcon.setFitWidth(20);
        deleteIcon.setFitHeight(20);
        deleteIcon.setOnMouseClicked(event -> supprimerLocal(local));

        HBox deleteBox = new HBox(deleteIcon);
        deleteBox.setStyle("-fx-alignment: center-right;");

        localBox.getChildren().addAll(localImage, adresseLabel, capaciteLabel, tarifsLabel, detailButton, deleteBox);
        return localBox;
    }


    private void afficherDetailsLocal(Locaux local) {
        try {
            // Charger la page detail_local.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/detail_Local.fxml"));
            Parent root = loader.load();

            // Passer le local sélectionné au contrôleur DetailLocalController
            DetailController controller = loader.getController();
            controller.setLocal(local);

            // Afficher la nouvelle scène
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Détails du Local");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de detail_local.fxml");
        }
    }

    private void supprimerLocal(Locaux local) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Voulez-vous vraiment supprimer ce local ?");
        confirmationAlert.setContentText("Cette action est irréversible.");

        ButtonType buttonYes = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(buttonYes, buttonNo);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                try {
                    locauxService.delete(local);
                    afficherLocaux(); // Refresh the list after deletion
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sortLocaux(String sortOrder) {
        try {
            if (sortOrder.equals("Tarifs: Ascendant")) {
                filteredLocaux = locauxService.getAllSorted("Tarifs: Ascending");
            } else if (sortOrder.equals("Tarifs: Descendant")) {
                filteredLocaux = locauxService.getAllSorted("Tarifs: Descending");
            } else {
                filteredLocaux = locauxService.getAll(); // Default unsorted list
            }

            // Debug: Print sorted results
            System.out.println("Sorted Locaux:");
            for (Locaux loc : filteredLocaux) {
                System.out.println(loc.getAdresse() + " - " + loc.getTarifs());
            }

            afficherLocaux(); // Refresh UI
        } catch (SQLException e) {
            e.printStackTrace();
        }
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