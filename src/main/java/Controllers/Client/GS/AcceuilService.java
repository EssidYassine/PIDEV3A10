package Controllers.Client.GS;

import Models.Service;
import Services.ServiceService;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AcceuilService {
    @FXML
    private GridPane gridPaneServices;

    @FXML
    private VBox detailsContainer;
    @FXML
    private ImageView imageService;
    @FXML
    private Label labelNomService;
    @FXML
    private Label labelDescription;
    @FXML
    private Label labelPrix;

    private final ServiceService serviceService = new ServiceService();
    private List<Service> listeDesServices;

    @FXML
    public void initialize() {
        try {
            // Chargement des services depuis la base de données
            listeDesServices = serviceService.getAll();

            int column = 0;
            int row = 0;

            for (Service service : listeDesServices) {
                // Création de la carte pour chaque service
                VBox carteService = new VBox(10);
                carteService.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
                carteService.setPrefWidth(250);

                // Image du service
                ImageView imageView = new ImageView();
                imageView.setImage(new Image(service.getImage_url()));
                imageView.setFitWidth(230);
                imageView.setFitHeight(150);

                // Nom du service
                Label nomService = new Label(service.getNom_service());
                nomService.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #333;");

                // Prix du service
                Label prixService = new Label(service.getPrix() + " DT");
                prixService.setStyle("-fx-font-size: 16; -fx-text-fill: #1e0fc6; -fx-font-weight: bold;");

                // Bouton Détail
                Button btnDetail = new Button("Détail");
                btnDetail.setStyle("-fx-background-color: #1e0fc6; -fx-text-fill: white; -fx-background-radius: 10;");
                btnDetail.setOnAction(event -> afficherDetails(service));

                carteService.getChildren().addAll(imageView, nomService, prixService, btnDetail);

                gridPaneServices.add(carteService, column, row);

                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherDetails(Service service) {
        gridPaneServices.setVisible(false);
        detailsContainer.setVisible(true);

        labelNomService.setText(service.getNom_service());
        labelDescription.setText(service.getDescription());
        labelPrix.setText(service.getPrix() + " DT");
        imageService.setImage(new Image(service.getImage_url()));

        // Animation de transition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), detailsContainer);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    private void retourListeServices() {
        detailsContainer.setVisible(false);
        gridPaneServices.setVisible(true);
    }

    public void gotoAcceuilService(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/AcceuilService.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }

    public void retourHome1(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/Home1.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }

    public void gotoReserverMateriel(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/ReserverMateriel.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }
}

