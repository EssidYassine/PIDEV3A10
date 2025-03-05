package Controllers.Admin.GP;

import Models.Reservation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;

public class CardReservationController {

    @FXML private VBox cardRoot;
    @FXML private Label lblId;
    @FXML private Label lblDate;
    @FXML private Label lblEmail;
    @FXML private Label lblStatus;
    @FXML private Label lblParticipants;

    public void setReservationData(Reservation reservation) {
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
    }

    @FXML
    private void handleClose() {
        Pane parent = (Pane) cardRoot.getParent();
        if (parent != null) {
            parent.getChildren().remove(cardRoot);
        }
    }

}