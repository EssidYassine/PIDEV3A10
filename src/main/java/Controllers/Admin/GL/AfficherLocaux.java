package Controllers.Admin.GL;

import Models.Locaux;
import Services.LocauxService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class AfficherLocaux {

    @FXML
    private ScrollPane scrollPaneLocaux;

    @FXML
    private GridPane gridPaneLocaux;

    private final LocauxService locauxService = new LocauxService();

    @FXML
    public void initialize() {
        try {
            chargerLocaux();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur SQL", "Impossible de charger les locaux : " + e.getMessage());
        }
    }

    /**
     * Charge la liste des locaux et les affiche dans le GridPane.
     */
    private void chargerLocaux() throws SQLException {
        gridPaneLocaux.getChildren().clear();
        List<Locaux> locauxList = locauxService.getAll();
        int row = 0;
        int column = 0;

        for (Locaux local : locauxList) {
            VBox card = new VBox();
            card.setSpacing(10);
            card.setStyle("-fx-background-color: #1f2c50; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;");

            // Image du local
            ImageView imgView = new ImageView();
            imgView.setFitWidth(150);
            imgView.setFitHeight(100);
            imgView.setPreserveRatio(true);

            try {
                String imageUrl = local.getPhoto();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    if (!imageUrl.startsWith("http") && !imageUrl.startsWith("file:")) {
                        imageUrl = "file:///" + imageUrl.replace("\\", "/");
                    }
                    Image image = new Image(imageUrl, true);
                    imgView.setImage(image);
                } else {
                    String defaultImagePath = "/Images/default.png";
                    URL defaultUrl = getClass().getResource(defaultImagePath);
                    if (defaultUrl != null) {
                        imgView.setImage(new Image(defaultUrl.toExternalForm(), true));
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'image : " + e.getMessage());
            }

            card.getChildren().add(imgView);

            // Affichage de l'adresse
            Label adresseLabel = new Label("Adresse: " + local.getAdresse());
            adresseLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            card.getChildren().add(adresseLabel);

            // Affichage du type
            Label typeLabel = new Label("Type: " + local.getType());
            card.getChildren().add(typeLabel);

            // Affichage du tarif
            Label tarifLabel = new Label("Tarif: " + local.getTarifs() + " €");
            card.getChildren().add(tarifLabel);

            // Bouton Supprimer
            Button deleteButton = new Button("Supprimer");
            deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            deleteButton.setOnAction(e -> {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmation");
                confirmation.setHeaderText(null);
                confirmation.setContentText("Voulez-vous vraiment supprimer ce local ?");
                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            locauxService.delete(local);
                            chargerLocaux();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            showErrorAlert("Erreur SQL", "Impossible de supprimer le local : " + ex.getMessage());
                        }
                    }
                });
            });
            card.getChildren().add(deleteButton);

            // Bouton Modifier
            Button modifyButton = new Button("Modifier");
            modifyButton.setStyle("-fx-background-color: #ffa500; -fx-text-fill: white;");
            modifyButton.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/ModifierLocal.fxml"));
                    Parent root = loader.load();
                    Controllers.Admin.GL.ModifierLocal modifierController = loader.getController();
                    modifierController.setLocal(local);
                    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Modifier Local");
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showErrorAlert("Erreur", "Impossible d'ouvrir le formulaire de modification : " + ex.getMessage());
                }
            });
            card.getChildren().add(modifyButton);

            gridPaneLocaux.add(card, column, row);
            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    /**
     * Affiche une alerte d'erreur.
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Méthodes de navigation

    @FXML
    private void allerHome(ActionEvent event) {
        navigateTo("/Views/Admin/GL/Home.fxml", event, "Accueil");
    }

    @FXML
    private void allerCrudLocaux(ActionEvent event) {
        navigateTo("/Views/Admin/GL/CrudLocaux.fxml", event, "Gérer Locaux");
    }

    @FXML
    private void retourAfficherLocaux(ActionEvent event) {
        navigateTo("/Views/Admin/GL/AfficherLocaux.fxml", event, "Afficher Locaux");
    }

    /**
     * Méthode utilitaire de navigation.
     */
    private void navigateTo(String fxmlPath, ActionEvent event, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur de navigation", "Impossible de charger la fenêtre : " + e.getMessage());
        }
    }
    public void goTohome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/Home.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Locaux");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
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
            System.err.println("Erreur de chargement de AcceuilLocaux.fxml !");
        }
    }
}
