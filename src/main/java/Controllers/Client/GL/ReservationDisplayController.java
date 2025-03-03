package Controllers.Client.GL;

import Models.ReservationLocaux;
import Services.ReservationLocauxService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import java.sql.SQLException;
import java.util.List;

public class ReservationDisplayController {

    @FXML
    private ListView<String> reservationListView;

    private final ReservationLocauxService reservationService = new ReservationLocauxService();

    @FXML
    public void initialize() {
        System.out.println("Initializing ReservationDisplayController...");
        loadUserReservations();
    }

    public void loadUserReservations() {
        try {
            int userId = 1; // Replace with the actual user ID if available
            System.out.println("Fetching reservations for user ID: " + userId);

            List<ReservationLocaux> reservations = reservationService.getAllUserReservations(userId);

            if (reservations == null || reservations.isEmpty()) {
                System.out.println("No reservations found for user ID: " + userId);
            }

            ObservableList<String> reservationDetails = FXCollections.observableArrayList();
            for (ReservationLocaux reservation : reservations) {
                String details = "Local ID: " + reservation.getIdLocal() +
                        " | Début: " + reservation.getDateDebut().toLocalDate() +
                        " | Fin: " + reservation.getDateFin().toLocalDate() +
                        " | Statut: " + reservation.getStatut();
                reservationDetails.add(details);
            }

            reservationListView.setItems(reservationDetails);
            System.out.println("Reservations successfully loaded!");

        } catch (SQLException e) {
            e.printStackTrace();
            //showAlert("Erreur", "Impossible de charger les réservations. SQL Error: " + e.getMessage());
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
}
