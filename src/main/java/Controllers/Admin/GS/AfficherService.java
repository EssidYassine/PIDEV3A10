package Controllers.Admin.GS;

import Models.Service;
import Services.ServiceService;
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

public class AfficherService {

    @FXML
    private ScrollPane scrollPaneReservations;

    @FXML
    private GridPane gridPaneReservations;

    private final ServiceService serviceService = new ServiceService();

    @FXML
    public void initialize() {
        try {
            chargerServices();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur SQL", "Impossible de charger les services : " + e.getMessage());
        }
    }

    /**
     * Charge la liste des services et les affiche dans le GridPane.
     */
    private void chargerServices() throws SQLException {
        gridPaneReservations.getChildren().clear();
        List<Service> services = serviceService.getAll();
        int row = 0;
        int column = 0;

        for (Service service : services) {
            VBox card = new VBox();
            card.setSpacing(10);
            card.setStyle("-fx-background-color: #1f2c50; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;");

            // Image du service
            ImageView imgView = new ImageView();
            imgView.setFitWidth(150);
            imgView.setFitHeight(100);
            imgView.setPreserveRatio(true);

            try {
                String serviceImageUrl = service.getImageUrl();
                if (serviceImageUrl != null && !serviceImageUrl.isEmpty()) {
                    // Si l'URL ne commence pas par "http:" ou "file:", on ajoute "file:///" et on corrige le chemin
                    if (!serviceImageUrl.startsWith("http") && !serviceImageUrl.startsWith("file:")) {
                        serviceImageUrl = "file:///" + serviceImageUrl.replace("\\", "/");
                    }
                    Image image = new Image(serviceImageUrl, true);
                    if (image.getException() == null) {
                        imgView.setImage(image);
                    } else {
                        throw image.getException();
                    }
                } else {
                    // Chargement de l'image par défaut depuis les ressources
                    String defaultImagePath = "/Images/default.png";
                    URL defaultUrl = getClass().getResource(defaultImagePath);
                    if (defaultUrl != null) {
                        String defaultImageUrl = defaultUrl.toExternalForm();
                        imgView.setImage(new Image(defaultImageUrl, true));
                    } else {
                        System.err.println("Default image resource not found: " + defaultImagePath);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'image : " + e.getMessage());
            }
            card.getChildren().add(imgView);

            // Affichage du nom du service
            Label nomLabel = new Label(service.getNom_service());
            nomLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            card.getChildren().add(nomLabel);

            // Affichage du prix du service
            Label prixLabel = new Label("Prix : " + service.getPrix() + " €");
            card.getChildren().add(prixLabel);

            // Bouton Supprimer
            Button deleteButton = new Button("Supprimer");
            deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            deleteButton.setOnAction(e -> {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmation");
                confirmation.setHeaderText(null);
                confirmation.setContentText("Voulez-vous vraiment supprimer ce service ?");
                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            serviceService.delete(service);
                            chargerServices(); // recharge la liste après suppression
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            showErrorAlert("Erreur SQL", "Impossible de supprimer le service : " + ex.getMessage());
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
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/ModifierService.fxml"));
                    Parent root = loader.load();
                    Controllers.Admin.GS.ModifierService modifierController = loader.getController();
                    modifierController.setServiceToModify(service);
                    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Modifier Service");
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showErrorAlert("Erreur", "Impossible d'ouvrir le formulaire de modification : " + ex.getMessage());
                }
            });
            card.getChildren().add(modifyButton);

            gridPaneReservations.add(card, column, row);
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

    // Méthodes de navigation (exemple)

    @FXML
    private void allerHome(ActionEvent event) {
        navigateTo("/Views/Admin/GS/Home.fxml", event, "Accueil");
    }

    @FXML
    private void allerCrudService(ActionEvent event) {
        navigateTo("/Views/Admin/GS/CrudService.fxml", event, "Gérer Service");
    }

    @FXML
    private void retourAfficherService(ActionEvent event) {
        navigateTo("/Views/Admin/GS/AfficherService.fxml", event, "Afficher Service");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GS/Home.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }

    public void gotocrudservice(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/admin/GS/CrudService.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }
    }
}
