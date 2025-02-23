package Controllers.Client.GS;

import Models.Reservation;
import Models.Service;
import Services.ReservationService;
import Services.ServiceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ModifierReservationController {

    @FXML
    private ComboBox<Service> comboService;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField timeField;

    @FXML
    private TextField txtQuantite;

    @FXML
    private Button btnAjouter; // Bouton "Confirmer"

    @FXML
    private Button btnAnnuler;

    private ReservationService reservationService = new ReservationService();
    private ServiceService serviceService = new ServiceService();

    // La réservation à modifier
    private Reservation reservation;

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        System.out.println("Réservation à modifier : " + reservation);
        // Pré-remplissage des champs du formulaire
        comboService.setValue(reservation.getService());
        datePicker.setValue(reservation.getDate_reservation().toLocalDate());
        timeField.setText(reservation.getDate_reservation().toLocalTime().toString());
        txtQuantite.setText(String.valueOf(reservation.getQuantite()));
    }

    @FXML
    public void initialize() {
        try {
            // Charger tous les services pour la ComboBox
            List<Service> allServices = new ServiceService().getAll();
            comboService.getItems().clear();
            comboService.getItems().addAll(allServices);
            comboService.setConverter(new javafx.util.StringConverter<Service>() {
                @Override
                public String toString(Service service) {
                    return service == null ? "" : service.getNom_service();
                }

                @Override
                public Service fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void Modifier(ActionEvent event) {
        try {
            Service selectedService = comboService.getValue();
            if (selectedService == null) {
                System.out.println("Veuillez sélectionner un service.");
                return;
            }

            LocalDate date = datePicker.getValue();
            if (date == null) {
                System.out.println("Veuillez sélectionner une date.");
                return;
            }

            String timeText = timeField.getText();
            if (timeText == null || timeText.isEmpty()) {
                System.out.println("Veuillez saisir l'heure.");
                return;
            }
            String[] parts = timeText.split(":");
            if (parts.length != 2) {
                System.out.println("Format de l'heure invalide. Utilisez HH:MM.");
                return;
            }
            int hour = Integer.parseInt(parts[0].trim());
            int minute = Integer.parseInt(parts[1].trim());
            LocalDateTime newDateTime = LocalDateTime.of(date, LocalTime.of(hour, minute));

            int quantite;
            try {
                quantite = Integer.parseInt(txtQuantite.getText().trim());
            } catch (NumberFormatException e) {
                System.out.println("Quantité invalide.");
                return;
            }

            // Mise à jour des champs de la réservation
            reservation.setService(selectedService);
            reservation.setDate_reservation(newDateTime);
            reservation.setQuantite(quantite);

            System.out.println("Mise à jour de la réservation avec :");
            System.out.println("Service : " + selectedService.getNom_service());
            System.out.println("Date/Heure : " + newDateTime);
            System.out.println("Quantité : " + quantite);

            // Appel de la méthode update pour enregistrer les modifications en base
            reservationService.update(reservation);
            System.out.println("Réservation modifiée avec succès !");

            // Navigation vers la liste des réservations
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/ListeReservationService.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des réservations");
            stage.show();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


    public void retourListeReservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/ListeReservationService.fxml"));
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
