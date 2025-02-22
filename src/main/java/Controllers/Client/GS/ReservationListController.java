package Controllers.Client.GS;

import Models.Reservation;
import Services.ReservationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationListController {

    @FXML
    private GridPane gridPaneReservations;

    @FXML
    private ScrollPane scrollPaneReservations;

    @FXML
    private Button btnRetour;

    private final ReservationService reservationService = new ReservationService();
    private List<Reservation> listeDesReservations;

    @FXML
    public void initialize() {
        try {
            // Chargement des réservations depuis la base de données
            listeDesReservations = reservationService.getAll();

            int column = 0;
            int row = 0;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Reservation res : listeDesReservations) {
                // Création de la carte pour chaque réservation
                VBox carteReservation = new VBox(10);
                carteReservation.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 15; -fx-border-radius: 10; " +
                        "-fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
                carteReservation.setPrefWidth(250);

                // Image (optionnelle, ici on affiche l'image du service réservé)
                ImageView imageView = new ImageView();
                try {
                    imageView.setImage(new Image(res.getService().getImage_url()));
                } catch(Exception e) {
                    // Si l'image ne peut être chargée, on laisse vide
                }
                imageView.setFitWidth(230);
                imageView.setFitHeight(150);

                // Informations sur la réservation
                Label nomService = new Label("Service: " + res.getService().getNom_service());
                nomService.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #333;");

                Label utilisateur = new Label("Utilisateur: " + res.getUtilisateur().getNom());
                utilisateur.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

                Label dateReservation = new Label("Date: " + res.getDate_reservation().format(dtf));
                dateReservation.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

                Label quantite = new Label("Quantité: " + res.getQuantite());
                quantite.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

                // Bouton détail (optionnel)
                Button btnDetail = new Button("Détails");
                btnDetail.setStyle("-fx-background-color: #1e0fc6; -fx-text-fill: white; -fx-background-radius: 10;");
                btnDetail.setOnAction(event -> afficherDetails(res));

                carteReservation.getChildren().addAll(imageView, nomService, utilisateur, dateReservation, quantite, btnDetail);

                gridPaneReservations.add(carteReservation, column, row);
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

    /**
     * Affiche les détails d'une réservation dans une nouvelle vue ou dans un conteneur.
     * Ici, on se contente d'afficher un message dans la console.
     */
    private void afficherDetails(Reservation res) {
        System.out.println("Détails de la réservation : " + res);
        // Vous pouvez étendre cette méthode pour ouvrir une vue de détails.
    }

    /**
     * Méthode appelée lors du clic sur le bouton "Acceuil".
     * Retourne à la vue d'accueil.
     */
    @FXML
    private void retourHome1(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/Home1.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de Home1.fxml !");
        }
    }


    public void retourHome1(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/Home1.fxml"));
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
