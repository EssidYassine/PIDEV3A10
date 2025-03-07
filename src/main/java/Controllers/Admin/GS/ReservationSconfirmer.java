package Controllers.Admin.GS;

import Models.Service;
import Services.ServiceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ReservationSconfirmer {

    @FXML
    private GridPane gridPaneServicesConfirmes;

    private final ServiceService serviceService = new ServiceService();

    @FXML
    public void initialize() {
        try {
            List<Service> servicesConfirmes = serviceService.getServicesConfirmes();
            int column = 0;
            int row = 0;

            for (Service service : servicesConfirmes) {
                VBox carteService = new VBox(10);
                carteService.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 15; -fx-border-radius: 10; " +
                        "-fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
                carteService.setPrefWidth(250);

                // Image du service
                ImageView imageView = new ImageView();
                try {
                    imageView.setImage(new Image(service.getImage_url()));
                } catch (Exception e) {
                    // Si l'image ne peut être chargée, on laisse vide
                }
                imageView.setFitWidth(230);
                imageView.setFitHeight(150);

                // Nom du service
                Label nomService = new Label(service.getNom_service());
                nomService.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #333;");

                // Prix
                Label prixService = new Label(service.getPrix() + " DT");
                prixService.setStyle("-fx-font-size: 16; -fx-text-fill: #1e0fc6; -fx-font-weight: bold;");

                // Ajout des éléments dans la carte
                carteService.getChildren().addAll(imageView, nomService, prixService);

                // Ajout dans le GridPane
                gridPaneServicesConfirmes.add(carteService, column, row);
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

    public void retourAdminDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord Admin");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement du tableau de bord Admin !");
        }
    }

    public void gotohome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/Home.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }

    public void retourAfficherService(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/AfficherService.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }
}
