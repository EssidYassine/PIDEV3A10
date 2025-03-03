package Controllers.Client.GL;

import Models.ReservationLocaux;
import Services.ReservationLocauxService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ModifierReservationLocauxController {

    @FXML
    private DatePicker dateDebut;
    @FXML
    private DatePicker dateFin;
    @FXML
    private ComboBox<String> statutCombo;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnAnnuler;

    private final ReservationLocauxService reservationService = new ReservationLocauxService();
    private ReservationLocaux currentReservation;

    public void setReservation(ReservationLocaux reservation) {
        this.currentReservation = reservation;

        // Populate fields with current reservation details
        dateDebut.setValue(reservation.getDateDebut().toLocalDate());
        dateFin.setValue(reservation.getDateFin().toLocalDate());
        statutCombo.setItems(FXCollections.observableArrayList("Confirmed", "Pending", "Cancelled"));
        statutCombo.setValue(reservation.getStatut());
    }

    @FXML
    private void modifierReservation() {
        if (dateDebut.getValue() == null || dateFin.getValue() == null || statutCombo.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
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
            // Update reservation object
            currentReservation.setDateDebut(startDate);
            currentReservation.setDateFin(endDate);
            currentReservation.setStatut(statutCombo.getValue());

            // Update in database
            reservationService.update(currentReservation);

            showAlert("Succès", "Réservation modifiée avec succès !");
            retourListeReservations();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier la réservation.");
        }
    }

    @FXML
    private void retourListeReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GL/LocauxReserves.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnModifier.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Réservations");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à la liste des réservations.");
        }
    }

    @FXML
    private void annulerModification() {
        retourListeReservations();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
