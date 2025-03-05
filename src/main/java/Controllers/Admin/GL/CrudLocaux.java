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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class CrudLocaux implements Initializable {

    @FXML
    private TextField TFAdresse, TFCapacite, TFTarifs;

    @FXML
    private ComboBox<String> CBType;

    @FXML
    private ImageView imageViewLocal;

    @FXML
    private Button btnChoisirImage, btnAjouter;

    @FXML
    private GridPane gridPaneLocaux;
    @FXML

    private CheckBox chkWifi, chkCameras, chkEspaceTravail, chkCuisine, chkParking;


    private String photoPath;
    private final LocauxService locauxService = new LocauxService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CBType.getItems().addAll("OPEN SPACE ", "CLOSED SPACE " ,"Both");
        loadLocaux();
    }

    @FXML
    void ajouterLocal(ActionEvent event) {
        try {
            String adresse = TFAdresse.getText();
            int capacite = Integer.parseInt(TFCapacite.getText());
            String type = CBType.getValue();

            if (capacite < 0) {
                showError("Valeur invalide", "La capacité ne peut pas être négative !");
                return;
            }
            StringBuilder equipements = new StringBuilder();
            if (chkWifi.isSelected()) equipements.append("Wifi, ");
            if (chkCameras.isSelected()) equipements.append("Caméras de surveillance extérieures, ");
            if (chkEspaceTravail.isSelected()) equipements.append("Espace de travail dédié, ");
            if (chkCuisine.isSelected()) equipements.append("Cuisine, ");
            if (chkParking.isSelected()) equipements.append("Parking, ");

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

            loadLocaux();
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
    @FXML

    private void loadLocaux() {
        try {
            gridPaneLocaux.getChildren().clear();
            List<Locaux> locauxList = locauxService.getAll();

            int column = 0;
            int row = 0;
            for (Locaux local : locauxList) {
                VBox card = new VBox();
                card.setStyle("-fx-background-color: #1f2c50; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;");

                ImageView imgView = new ImageView();
                imgView.setFitWidth(150);
                imgView.setFitHeight(100);
                imgView.setImage(new Image(local.getPhoto()));

                Label adresseLabel = new Label(local.getAdresse());
                adresseLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                Label capaciteLabel = new Label("Capacité: " + local.getCapacite());
                Label tarifLabel = new Label("Tarifs: " + local.getTarifs() + " DT");


                card.getChildren().addAll(imgView, adresseLabel, capaciteLabel, tarifLabel);
                gridPaneLocaux.add(card, column, row);

                column++;
                if (column == 2) {
                    column = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            showError("Erreur SQL", "Impossible de charger les locaux !");
        }
    }
/*
    private void supprimerLocal(Locaux local) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer ce local ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    locauxService.delete(local);
                    loadLocaux();
                } catch (SQLException e) {
                    showError("Erreur SQL", "Impossible de supprimer le local !");
                }
            }
        });
    }

    private void openModifyWindow(Locaux local) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/ModifierLocal.fxml"));
            Parent root = loader.load();

            ModifierLocal controller = loader.getController();
            controller.setLocal(local);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Local");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir la fenêtre de modification !");
        }
    }*/

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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
            System.err.println("Erreur de chargement de AcceuilLocaux.fxml !");
        }
    }
    public void retourAfficherLocaux(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GL/AfficherLocaux.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Locaux ");
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
            System.err.println("Erreur de chargement de AcceuilLocaux.fxml !");
        }
    }


}
