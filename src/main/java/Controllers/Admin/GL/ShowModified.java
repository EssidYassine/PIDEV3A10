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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ShowModified implements Initializable {

    @FXML
    private GridPane gridPane;

    private final LocauxService locauxService = new LocauxService();

    @FXML
    private void retourVersAjoutLocal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/Add_Local.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gridPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        afficherLocaux();
    }

    private void afficherLocaux() {
        try {
            List<Locaux> locaux = locauxService.getAll();
            gridPane.getChildren().clear();

            int row = 0, col = 0;
            for (Locaux local : locaux) {
                VBox localBox = creerLocalBox(local);
                gridPane.add(localBox, col, row);

                col++;
                if (col == 4) {
                    col = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox creerLocalBox(Locaux local) {
        VBox localBox = new VBox(10);
        localBox.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-border-radius: 10; -fx-border-color: #1f2c50; -fx-alignment: center;");
        localBox.setPrefWidth(200);

        // Image du local
        ImageView localImage;
        if (local.getPhoto() != null && !local.getPhoto().isEmpty()) {
            localImage = new ImageView(new Image(local.getPhoto()));
        } else {
            localImage = new ImageView(new Image(getClass().getResourceAsStream("/icons/default_local.png"))); // Image par défaut
        }
        localImage.setFitWidth(150);
        localImage.setFitHeight(100);

        // Adresse du local
        Label adresseLabel = new Label(local.getAdresse());
        adresseLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Tarifs du local
        Label tarifLabel = new Label(local.getTarifs() + " DT");
        tarifLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        Button modifierButton = new Button("Modifier");
        modifierButton.setStyle("-fx-background-color: #ed4e00; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-border-radius: 5;");
        modifierButton.setOnAction(event -> afficherModifier(local));

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

        localBox.getChildren().addAll(localImage, adresseLabel, tarifLabel, modifierButton, deleteBox);
        return localBox;
    }

    private void supprimerLocal(Locaux local) {
        try {
            locauxService.delete(local);
            afficherLocaux();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Suppression");
            alert.setHeaderText(null);
            alert.setContentText("Local supprimé avec succès !");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de supprimer le local !");
            alert.showAndWait();
        }
    }

    private void afficherModifier(Locaux local) {
        try {
            // Charger le FXML du formulaire de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/Modify.fxml"));
            Parent detailsPane = loader.load();

            // Récupérer le contrôleur et envoyer les données
            CrudModify controller = loader.getController();
            controller.setLocal(local);

            // Remplacer le contenu du GridPane
            gridPane.getChildren().clear();
            gridPane.add(detailsPane, 0, 0, 3, 3); // Sur 3 colonnes et 3 lignes

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void revenirFenetreGestionLocaux(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/Show_Local.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Locaux");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de Show_Local.fxml !");
        }
    }
}

