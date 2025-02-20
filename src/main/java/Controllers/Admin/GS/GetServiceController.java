package Controllers.Admin.GS;

import Models.Service;
import Services.ServiceService;
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
import java.util.ResourceBundle;

public class GetServiceController implements Initializable {

    @FXML
    private GridPane gridPane;

    private final ServiceService serviceService = new ServiceService();

    @FXML
    private void retourVersAjoutService() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/Serviice.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gridPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private ImageView serviceImage; // Assure-toi que l'ID correspond au FXML




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        afficherServices();

    }

    private void afficherServices() {
        try {
            List<Service> services = serviceService.getAll();
            gridPane.getChildren().clear();

            int row = 0, col = 0;
            for (Service service : services) {
                VBox serviceBox = creerServiceBox(service);
                gridPane.add(serviceBox, col, row);

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

    private VBox creerServiceBox(Service service) {
        VBox serviceBox = new VBox(10);
        serviceBox.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-border-radius: 10; -fx-border-color: #1f2c50; -fx-alignment: center;");
        serviceBox.setPrefWidth(200);

        // Image du service
        ImageView serviceImage;
        if (service.getImage_url() != null && !service.getImage_url().isEmpty()) {
            serviceImage = new ImageView(new Image(service.getImage_url()));
        } else {
            serviceImage = new ImageView(new Image(getClass().getResourceAsStream("/icons/default_service.png"))); // Image par défaut
        }
        serviceImage.setFitWidth(150);
        serviceImage.setFitHeight(100);

        // Nom du service
        Label nomLabel = new Label(service.getNom_service());
        nomLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Prix du service
        Label prixLabel = new Label(service.getPrix() + "DT");
        prixLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        Button detailButton = new Button("Détail");
        detailButton.setStyle("-fx-background-color: #ed4e00; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-border-radius: 5;");
        detailButton.setOnAction(event -> afficherDetailsService(service));





        serviceBox.getChildren().addAll(serviceImage, nomLabel, prixLabel, detailButton);
        return serviceBox;
    }


    private void afficherDetailsService(Service service) {
        try {
            // Charger le FXML des détails
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/ServiceDetails.fxml"));
            Parent detailsPane = loader.load();

            // Récupérer le contrôleur et envoyer les données
            ServiceDetailsController controller = loader.getController();
            controller.setDetails(service.getNom_service(), service.getDescription(), service.getImage_url());

            // Remplacer le contenu du GridPane
            gridPane.getChildren().clear();
            gridPane.add(detailsPane, 0, 0, 3, 3); // Sur 3 colonnes et 3 lignes

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void revenirFenetreGestion1(ActionEvent actionEvent) {
        try {
            // Charger l'interface Service.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/GestionnaireS.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("gestionnaire");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de GestionnaireS.fxml !");
        }
    }
}
