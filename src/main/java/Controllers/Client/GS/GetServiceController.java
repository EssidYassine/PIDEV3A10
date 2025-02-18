package Controllers.Client.GS;



import Models.Service;
import Services.ServiceService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class GetServiceController {

    @FXML
    private GridPane gridPane;

    private final ServiceService serviceService = new ServiceService();

    @FXML
    public void initialize() {
        afficherServices();
    }

    private void afficherServices() {
        try {
            List<Service> services = serviceService.getAll();
            int row = 0;
            int col = 0;

            for (Service service : services) {
                VBox vbox = new VBox();
                vbox.setSpacing(8);
                vbox.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-border-radius: 10px; -fx-border-color: #ccc; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 3);");

                // Image du service
                ImageView imageView = new ImageView(new Image(service.getImage_url()));
                imageView.setFitWidth(200);
                imageView.setFitHeight(150);
                imageView.setStyle("-fx-border-radius: 10px;");

                // Nom du service
                Label nomLabel = new Label(service.getNom_service());
                nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

                // Prix
                Label prixLabel = new Label("Prix : " + service.getPrix() + "DT");
                prixLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");



                // Ajouter les éléments à la VBox
                vbox.getChildren().addAll(imageView, nomLabel, prixLabel);

                // Ajouter au GridPane
                gridPane.add(vbox, col, row);

                col++;
                if (col == 4) { // Passe à la ligne après 3 services par ligne
                    col = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void retourVersAjoutService(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/Serviice.fxml"));
            Parent root = loader.load();

            Scene currentScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) currentScene.getWindow();

            Scene newScene = new Scene(root, 900, 700);
            stage.setScene(newScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

