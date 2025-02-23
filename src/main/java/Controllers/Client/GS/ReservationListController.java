package Controllers.Client.GS;

import Models.Reservation;
import Models.Service;
import Services.ReservationService;
import Services.ServiceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

                // Bouton Supprimer (optionnel)
                Button btnSupprimer = new Button("Supprimer");
                btnSupprimer.setStyle("-fx-background-color: #ed4e00; -fx-text-fill: white; -fx-background-radius: 10;");
                btnSupprimer.setOnAction(event -> SpprimerReservation(res));
                // Bouton Modifier (optionnel)
                Button btnModifier = new Button("Modifier");
                btnModifier.setStyle("-fx-background-color: #1e0fc6; -fx-text-fill: white; -fx-background-radius: 10;");
                btnModifier.setOnAction(event -> modifierReservation(res));
                HBox buttonsBox = new HBox(10); // 10 pixels d'espacement entre les boutons
                buttonsBox.getChildren().addAll(btnSupprimer, btnModifier);

                carteReservation.getChildren().addAll(imageView, nomService, utilisateur, dateReservation, quantite,buttonsBox);

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

    private void modifierReservation(Reservation res) {
        try {
            // Charger le FXML de la fenêtre de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/ModifierReservation.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de modification pour lui passer la réservation à modifier
            ModifierReservationController controller = loader.getController();
            controller.setReservation(res); // Assurez-vous que cette méthode existe dans ModifierReservationController

            // Récupérer le stage actuel et définir la nouvelle scène
            Stage stage = (Stage) gridPaneReservations.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Réservation");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de ModifierReservation.fxml !");
        }
    }

    private void SpprimerReservation(Reservation res) {// Afficher une alerte de confirmation
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer la suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer cette réservation ?");

        // Attendre la réponse de l'utilisateur
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // Appeler le service pour supprimer la réservation
                reservationService.delete(res);
                System.out.println("Réservation supprimée avec succès !");

                // Récupérer le service associé à la réservation
                // Ici, res.getService() renvoie l'objet Service associé.
                Service service = res.getService();
                // Restaurer la quantité : on ajoute la quantité réservée à la quantité actuelle du service
                int restoredQuantity = service.getQuantite_materiel() + res.getQuantite();

                // Mettre à jour le service dans la base (utilisez votre méthode updateQuantiteAndAvailability)
                ServiceService serviceService = new ServiceService();
                serviceService.updateQuantiteAndAvailability3(((Models.Service) service).getId_service(), restoredQuantity);
                System.out.println("Quantité restaurée pour le service : " + restoredQuantity);

                // Rafraîchir la vue en rechargeant le FXML de la liste des réservations
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/ListeReservationService.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) gridPaneReservations.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Liste des réservations");
                stage.show();

            } catch (SQLException | IOException ex) {
                ex.printStackTrace();
            }
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
    private void retourAcceuil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/AcceuilService.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/AcceuilService.fxml"));
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
