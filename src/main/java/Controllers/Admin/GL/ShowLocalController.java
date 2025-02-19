package Controllers.Admin.GL;

import Models.Locaux;
import Services.LocauxService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ShowLocalController {

    @FXML
    private GridPane gridPane;

    private final LocauxService locauxService = new LocauxService();

    @FXML
    public void initialize() {
        try {
            afficherLocaux();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherLocaux() throws SQLException {
        List<Locaux> locauxList = locauxService.getAll();
        gridPane.getChildren().clear();

        int column = 0;
        int row = 0;

        for (Locaux local : locauxList) {
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
                localImage = new ImageView(); // Empty ImageView
            }
        }
        localImage.setFitWidth(150);
        localImage.setFitHeight(100);

        // Adresse du local
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
        // Logic to show details of the local
        System.out.println("Détails du local: " + local.getAdresse());
    }

    private void supprimerLocal(Locaux local) {
        // Logic to delete the local
        try {
            locauxService.delete(local); // Use getIdLocal() instead of getId()
            afficherLocaux(); // Refresh the list
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}