package Controllers.Client.GL;

import Models.Locaux;
import Models.ReservationLocaux;
import Services.ReservationLocauxService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationController {
    @FXML
    private ImageView imageViewLocal;
    @FXML
    private Label labelAdresse, labelCapacite, labelTarifs, labelType, equipementLabel;
    @FXML
    private DatePicker dateDebut;
    @FXML
    private DatePicker dateFin;
    @FXML
    private ComboBox<String> statutCombo;
    @FXML
    private Button reserveButton;
    @FXML
    //private ListView<String> reservationListView;

    private final ReservationLocauxService reservationService = new ReservationLocauxService();
    private Locaux selectedLocaux;

    @FXML
    public void initialize() {
        statutCombo.setItems(FXCollections.observableArrayList("Confirmed", "Pending", "Cancelled"));

            loadUserReservations();  // ✅ Correctly handling SQLException here

    }

    public void initData(Locaux local) {
        this.selectedLocaux = local;
        labelAdresse.setText(local.getAdresse());
        labelCapacite.setText("Capacité: " + local.getCapacite());
        labelTarifs.setText("Tarif: " + local.getTarifs() + " DT");
        labelType.setText("Type: " + local.getType());
        imageViewLocal.setImage(new Image(local.getPhoto()));
        equipementLabel.setText("Équipement: " + local.getEquipement());
    }

    @FXML
    private void handleReserve(ActionEvent event) {
        System.out.println("Handling reservation...");

        if (selectedLocaux == null || dateDebut.getValue() == null || dateFin.getValue() == null || statutCombo.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs et sélectionner un local.");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startDate = dateDebut.getValue().atStartOfDay();
        LocalDateTime endDate = dateFin.getValue().atStartOfDay();

        if (dateDebut.getValue().isBefore(today)) {
            showAlert("Erreur", "La date de début ne peut pas être antérieure à aujourd'hui.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            showAlert("Erreur", "La date de fin doit être après la date de début.");
            return;
        }

        try {
            List<ReservationLocaux> existingReservations = reservationService.getReservationsByLocal(selectedLocaux.getIdLocal());
            for (ReservationLocaux res : existingReservations) {
                if ((startDate.isBefore(res.getDateFin()) && startDate.isAfter(res.getDateDebut())) ||
                        (endDate.isBefore(res.getDateFin()) && endDate.isAfter(res.getDateDebut())) ||
                        (startDate.isEqual(res.getDateDebut()) || endDate.isEqual(res.getDateFin())) ||
                        (startDate.isBefore(res.getDateDebut()) && endDate.isAfter(res.getDateFin()))) {
                    showAlert("Erreur", "Ce local est déjà réservé pour la période sélectionnée.");
                    return;
                }
            }

            ReservationLocaux reservation = new ReservationLocaux(0, selectedLocaux.getIdLocal(), 1, startDate, endDate, statutCombo.getValue());
            reservationService.add(reservation);
            showAlert("Succès", "Réservation effectuée avec succès !");

            System.out.println("Reservation confirmed. Navigating to reservations page...");

            //goToReservationsPage(event);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec de la réservation.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur inattendue est survenue.");
        }
    }



    public void loadUserReservations() {
        try {
            int userId = 1; // Replace with actual logged-in user ID
            List<ReservationLocaux> reservations = reservationService.getAllUserReservations(userId);

            if (reservations == null || reservations.isEmpty()) {
                System.out.println("Aucune réservation trouvée pour l'utilisateur " + userId);
            }

            ObservableList<String> reservationDetails = FXCollections.observableArrayList();
            for (ReservationLocaux reservation : reservations) {
                String details = "Local ID: " + reservation.getIdLocal() +
                        " | Début: " + reservation.getDateDebut().toLocalDate() +
                        " | Fin: " + reservation.getDateFin().toLocalDate() +
                        " | Statut: " + reservation.getStatut();
                reservationDetails.add(details);
            }

            System.out.println("Réservations chargées avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            //showAlert("Erreur", "Impossible de charger les réservations.");
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Erreur", "Une erreur inattendue est survenue.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // this function directs back to acceuil  page

    @FXML
    private void retourListeLocaux(ActionEvent event) {
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
