package Controllers.Client.GP;

import Models.Pack;
import Services.ServiceGP;
import com.sun.javafx.menu.MenuItemBase;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class AcceuilPack  {
    public Button btnRetour1;
    @FXML
    private GridPane gridPaneServices;

    @FXML
    private VBox detailsContainer;
    @FXML
    private Label labelNomPack;
    @FXML
    private Label labelDescription;
    @FXML
    private Label labelPrix;
    @FXML
    private Button btnReservation;

    private final ServiceGP serviceGP = new ServiceGP();
    private List<Pack> listPack;

    @FXML
    public void initialize() {
        // Chargement des services depuis la base de données
        listPack = serviceGP.getAll();

        int column = 0;
        int row = 0;

        for (Pack pack : listPack) {
            // Si le pack est inactif, on passe au suivant
            if (!pack.isActive()) {
                continue;
            }

            // Création de la carte pour chaque service actif
            VBox carteService = new VBox(10);
            carteService.getStyleClass().add("card");
            carteService.setPrefWidth(250);

            // Ajout d'effets interactifs sur le survol
            carteService.setOnMouseEntered(event -> carteService.getStyleClass().add("card-hover"));
            carteService.setOnMouseExited(event -> carteService.getStyleClass().remove("card-hover"));

            // Nom du service
            Label nomService = new Label(pack.getNom());
            nomService.getStyleClass().add("card-title");

            // Description
            Label descPack = new Label(pack.getDescription());
            descPack.getStyleClass().add("card-desc");

            // Type
            Label typePack = new Label(pack.getType());
            typePack.getStyleClass().add("card-type");

            // Lieu
            Label lieuPack = new Label(pack.getLieu());
            lieuPack.getStyleClass().add("card-lieu");

            // Nombre d'invités maximum
            Label nbreInvitePack = new Label(pack.getNbreInvitesMax().toString());
            nbreInvitePack.getStyleClass().add("card-invite");

            // Prix du service
            Label prixService = new Label(pack.getPrix() + " DT");
            prixService.getStyleClass().add("card-price");

            // Bouton Détail
            Button btnDetail = new Button("Détail");
            btnDetail.getStyleClass().add("card-button");
            btnDetail.setOnAction(event -> afficherDetails(pack));

            // Bouton Réserver
            Button btnReserver = new Button("Réserver");
            btnReserver.getStyleClass().add("card-button");
            btnReserver.setOnAction(event -> {
                // Ouvrir la fenêtre de réservation et passer l'ID du pack
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GP/Reservation.fxml"));
                    Parent root = loader.load();

                    // Récupérer le contrôleur de la page de réservation et lui passer l'ID du pack
                    ReservationController controller = loader.getController();
                    controller.setPackId(pack.getId());
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Réservation du Pack");
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            carteService.getChildren().addAll(nomService, descPack, typePack, lieuPack, nbreInvitePack, prixService, btnDetail, btnReserver);
            gridPaneServices.add(carteService, column, row);

            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }



    private void afficherDetails(Pack p) {
        gridPaneServices.setVisible(false);
        detailsContainer.setVisible(true);

        labelNomPack.setText(p.getNom());
        labelDescription.setText(p.getDescription());
        labelPrix.setText(p.getPrix() + " DT");

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/AcceuilPack.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilPack.fxml !");
        }
    }

    public void retourHome1(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GP/Home1.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de Home1.fxml !");
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

    public void gotoReserverStaff(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/ReserverStaff.fxml"));
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

    public void gotoListeReservation(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/ListeReservationService.fxml"));
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