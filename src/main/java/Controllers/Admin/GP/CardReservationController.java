package Controllers.Admin.GP;

import Models.Reservation;
import Services.ReservationGP;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class CardReservationController {

    @FXML private VBox cardRoot;
    @FXML private Label lblId;
    @FXML private Label lblDate;
    @FXML private Label lblEmail;
    @FXML private Label lblStatus;
    @FXML private Label lblParticipants;
    @FXML private Button deleteBtn;
    @FXML private Label lblCommentaire;
    @FXML private ComboBox<Reservation.StatutReservation> statusComboBox;

    private Reservation reservation;
    private Runnable onDeleteListener;

    // Déclarez un listener pour rafraîchir le calendrier sans vider le container des détails.
    private Runnable onRefreshListener;

    public void setOnRefreshListener(Runnable listener) {
        this.onRefreshListener = listener;
    }


    public void setReservationData(Reservation reservation) {
        this.reservation = reservation; // Store the reservation
        statusComboBox.getItems().setAll(Reservation.StatutReservation.values());


        lblId.setText("Réservation #" + reservation.getReservationId());

        if (reservation.getDateReservation() != null) {
            String dateFormatted = reservation.getDateReservation()
                    .toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            lblDate.setText("Date: " + dateFormatted);
        } else {
            lblDate.setText("Date: Non spécifiée");
        }

        lblEmail.setText("Client: " + (reservation.getUser() != null ?
                reservation.getUser().getEmail() : "Non renseigné"));

        lblStatus.setText("Statut: " + (reservation.getStatutReservation() != null ?
                reservation.getStatutReservation() : "Inconnu"));

        lblParticipants.setText("Participants: " + reservation.getNbreInvites());

        lblParticipants.setText("Participants: " + reservation.getNbreInvites());
        lblCommentaire.setText("Commentaire: " + (reservation.getCommentaire() != null ?
                reservation.getCommentaire() : "Aucun commentaire"));

        // Configuration de la ComboBox
        statusComboBox.setValue(reservation.getStatutReservation()); // <-- Après l'initialisation

    }

    public void setOnDeleteListener(Runnable listener) {
        this.onDeleteListener = listener;
    }

    @FXML
    private void handleStatusChange() {
        try {
            ReservationGP service = new ReservationGP();
            // Met à jour l'objet avec la nouvelle valeur sélectionnée dans le ComboBox
            reservation.setStatutReservation(statusComboBox.getValue());
            service.updateStatus(reservation);

            // Recharge la réservation depuis la base pour être sûr d'avoir les données actualisées
            Reservation updatedReservation = service.getById(reservation.getReservationId());

            // Met à jour l'interface du card avec les nouvelles informations
            setReservationData(updatedReservation);

            if (onRefreshListener != null) {
                onRefreshListener.run();
            }

        } catch (SQLException e) {
            showAlert("Erreur", "Échec de la mise à jour : " + e.getMessage());
        }
    }



    @FXML
    private void handleDelete() {
        try {
            ReservationGP reservationService = new ReservationGP();
            reservationService.delete(reservation); // Delete from DB
            if (onDeleteListener != null) {
                onDeleteListener.run(); // Trigger refresh
            }
            handleClose(); // Close the card
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de la suppression : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        Pane parent = (Pane) cardRoot.getParent();
        if (parent != null) {
            parent.getChildren().remove(cardRoot);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}