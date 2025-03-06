package Controllers.Admin.GP;

import Models.Reservation;
import Models.User;
import Services.ReservationGP;
import javafx.application.Platform;
import javafx.fxml.FXML;
import mailling.SendEmail;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
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
    private Runnable onRefreshListener;

    public void setOnRefreshListener(Runnable listener) {
        this.onRefreshListener = listener;
    }

    public void setReservationData(Reservation reservation) {
        this.reservation = reservation;
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
        lblCommentaire.setText("Commentaire: " + (reservation.getCommentaire() != null ?
                reservation.getCommentaire() : "Aucun commentaire"));
        statusComboBox.setValue(reservation.getStatutReservation());
    }

    @FXML
    private void handleStatusChange() {
        try {
            Reservation.StatutReservation newStatus = statusComboBox.getValue();
            reservation.setStatutReservation(newStatus);

            // Désactiver le ComboBox si confirmé
            if(newStatus == Reservation.StatutReservation.CONFIRMÉE) {
                statusComboBox.setDisable(true);
                sendConfirmationEmail(reservation.getUser(), reservation);
            }
            ReservationGP service = new ReservationGP();
            // Mettre à jour le statut dans l'objet
            reservation.setStatutReservation(statusComboBox.getValue());

            // Mettre à jour le label immédiatement
            lblStatus.setText("Statut: " + statusComboBox.getValue().toString());

            // Sauvegarder dans la base de données
            service.updateStatus(reservation);

            // Rafraîchir le calendrier parent
            if (onRefreshListener != null) {
                onRefreshListener.run();
            }

        } catch (SQLException e) {
            showAlert("Échec de la mise à jour : " + e.getMessage());
        }
    }

    public void setOnDeleteListener(Runnable listener) {
        this.onDeleteListener = listener;
    }

    @FXML
    private void handleDelete() {
        try {
            ReservationGP reservationService = new ReservationGP();
            reservationService.delete(reservation);
            if (onDeleteListener != null) {
                onDeleteListener.run();
            }
            handleClose();
        } catch (SQLException e) {
            showAlert("Échec de la suppression : " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        Pane parent = (Pane) cardRoot.getParent();
        if (parent != null) {
            parent.getChildren().remove(cardRoot);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void sendConfirmationEmail(User user, Reservation reservation) {
        new Thread(() -> {
            try {
                // Correction : Ajouter l'email du destinataire
                SendEmail.sendConfirmationEmail(user.getEmail(), user, reservation);
            } catch (MessagingException e) {
                Platform.runLater(() -> showAlert("Erreur d'envoi: " + e.getMessage()));
            } catch (UnsupportedEncodingException e) {
                Platform.runLater(() -> showAlert("Erreur d'encodage: " + e.getMessage()));
            }
        }).start();
    }

    private String generateEmailContent(Reservation res) {
        return String.format(
                "Bonjour %s,\nVotre réservation #%d du %s a été confirmée.\n\nCordialement,\nL'équipe",
                res.getUser().getNom(),
                res.getReservationId(),
                res.getDateReservation().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }

}