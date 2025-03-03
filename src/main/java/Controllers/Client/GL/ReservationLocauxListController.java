package Controllers.Client.GL;

import Models.Locaux;
import Models.ReservationLocaux;
import Services.LocauxService;
import Services.ReservationLocauxService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationLocauxListController {

    @FXML
    private GridPane gridPaneReservations;

    @FXML
    private ScrollPane scrollPaneReservations;

    @FXML
    private Button btnRetour;

    private final ReservationLocauxService reservationService = new ReservationLocauxService();
    private final LocauxService locauxService = new LocauxService();
    private List<ReservationLocaux> listeDesReservations;

    @FXML
    public void initialize() {
        try {
            // Chargement des réservations de locaux depuis la base de données
            int userId = 1; // Remplacez par l'ID de l'utilisateur connecté
            listeDesReservations = reservationService.getAllUserReservations(userId);

            int column = 0;
            int row = 0;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (ReservationLocaux res : listeDesReservations) {
                // Récupérer les détails du local réservé
                Locaux local = locauxService.getById(res.getIdLocal());

                VBox carteReservation = new VBox(10);
                carteReservation.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 15; -fx-border-radius: 10; " +
                        "-fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
                carteReservation.setPrefWidth(250);

                // Image du local réservé
                ImageView imageView = new ImageView();
                try {
                    imageView.setImage(new Image(local.getPhoto()));
                } catch(Exception e) {
                    System.out.println("Erreur lors du chargement de l'image du local.");
                }
                imageView.setFitWidth(230);
                imageView.setFitHeight(150);

                // Informations sur la réservation
                Label nomLocal = new Label("Local: " + local.getAdresse());
                nomLocal.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #333;");

                Label capacite = new Label("Capacité: " + local.getCapacite());
                capacite.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

                Label dateReservation = new Label("Début: " + res.getDateDebut().format(dtf));
                dateReservation.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

                Label dateFin = new Label("Fin: " + res.getDateFin().format(dtf));
                dateFin.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");


                Button btnModifier = new Button("Modifier");
                btnModifier.setStyle("-fx-background-color: #1e0fc6; -fx-text-fill: white; -fx-background-radius: 10;");
                btnModifier.setOnAction(event -> modifierReservation(res));

                /*

                 Boutons Modifier et Supprimer
                Button btnSupprimer = new Button("Supprimer");
                btnSupprimer.setStyle("-fx-background-color: #ed4e00; -fx-text-fill: white; -fx-background-radius: 10;");
                btnSupprimer.setOnAction(event -> supprimerReservation(res));

                Button btnConfirmer = new Button("Confirmer");
                btnConfirmer.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 10;");
                btnConfirmer.setOnAction(event -> confirmerReservation(res));*/

                HBox buttonsBox = new HBox(10);
                buttonsBox.getChildren().addAll( btnModifier);

                carteReservation.getChildren().addAll(imageView, nomLocal, capacite, dateReservation, dateFin, buttonsBox);

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
/*
    private void confirmerReservation(ReservationLocaux res) {
        try {
            res.setStatut("Confirmé");
            reservationService.update(res);
            showAlert("Succès", "Réservation confirmée avec succès !");
            reloadPage();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de confirmer la réservation.");
        }
    }*/

    private void modifierReservation(ReservationLocaux res) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GL/ModifierReservationLoc.fxml"));
            Parent root = loader.load();

            ModifierReservationLocauxController controller = loader.getController();
            controller.setReservation(res);

            Stage stage = (Stage) gridPaneReservations.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Réservation");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de modification.");
        }
    }

    /*
    private void supprimerReservation(ReservationLocaux res) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setContentText("Voulez-vous vraiment supprimer cette réservation ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                reservationService.delete(res);
                showAlert("Succès", "Réservation supprimée avec succès !");
                reloadPage();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de supprimer la réservation.");
            }
        }
    }*/

    private void reloadPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GL/LocauxReserves.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gridPaneReservations.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Réservations de Locaux");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void retourAcceuil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GL/AcceuilLocaux.fxml"));
            Parent root = loader.load();

            // Get the current stage and close it
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            // Open the new scene
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Locaux");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à la liste des locaux.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void gotoHome(MouseEvent mouseEvent) {
        try {
            // Charger l'interface AcceuilService.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GL/Home1.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
