package Controllers.Client.GL;

import Models.Locaux;
import Models.ReservationLocaux;
import Services.ReservationLocauxService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    private final ReservationLocauxService reservationService = new ReservationLocauxService();
    private Locaux selectedLocaux;

    @FXML
    public void initialize() {
        statutCombo.setItems(FXCollections.observableArrayList("Confirmed", "Pending", "Cancelled"));
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
            retourListeLocaux(event);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec de la réservation.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void retourListeLocaux(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GL/AcceuilLocaux.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Locaux");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à la liste des locaux.");
        }
    }
}
