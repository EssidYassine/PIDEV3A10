package Controllers.Client.GP;

import Models.Locaux;
import Models.Reservation;
import Models.Service;
import Models.User;
import Services.ReservationGP;
import Services.ServiceGP;
import Services.UserService;
import Tools.DataBaseConnection;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationController {

    // Déclarations FXML
    @FXML private TextField userEmailField;
    @FXML private TextField nbreInvitesField;
    @FXML private TextField budgetField;
    @FXML private DatePicker dateReservationField;
    @FXML private TextArea commentaireField;
    @FXML private ComboBox<String> lieuField;
    @FXML private VBox servicesContainer;

    private final ReservationGP reservationGP = new ReservationGP();
    private final UserService userService = new UserService();
    private final ServiceGP serviceGP = new ServiceGP();
    private int packId;
    DataBaseConnection cnx = new DataBaseConnection();


    // Classe wrapper interne pour gérer les sélections
    private static class ServiceWrapper {
        private final Service service;
        private final BooleanProperty selected = new SimpleBooleanProperty(false);

        public ServiceWrapper(Service service) {
            this.service = service;
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public Service getService() {
            return service;
        }
    }


    public void setPackId(int packId) {
        this.packId = packId;
        loadServices();
        loadLieux();
        setDefaultLieu();
    }

    private void loadLieux() {
        try {
            List<Locaux> lieux = reservationGP.getAllLieux();
            lieuField.setItems(FXCollections.observableArrayList(
                    lieux.stream().map(Locaux::getAdresse).collect(Collectors.toList()))
            );
        } catch (SQLException e) {
            showAlert("Erreur de chargement des lieux : " + e.getMessage());
        }
    }

    private void setDefaultLieu() {
        try {
            Locaux defaultLieu = reservationGP.getDefaultLieuByPackId(packId);
            if (defaultLieu != null) {
                lieuField.setValue(defaultLieu.getAdresse());
            }
        } catch (SQLException e) {
            showAlert("Erreur de chargement du lieu par défaut : " + e.getMessage());
        }
    }

    private void loadServices() {
        try {
            List<Service> allServices = serviceGP.getAllServices();
            List<Service> packServices = reservationGP.getServicesByPackId(packId);

            servicesContainer.getChildren().clear(); // Vider les anciens services

            for (Service service : allServices) {
                CheckBox checkBox = new CheckBox(service.getNom_service());
                checkBox.setUserData(service); // Stocker l'objet Service dans le CheckBox

                // Vérifier si le service est inclus dans le pack
                if (packServices.stream().anyMatch(p -> p.getId_service() == service.getId_service())) {
                    checkBox.setSelected(true);
                }

                servicesContainer.getChildren().add(checkBox);
            }
        } catch (SQLException e) {
            showAlert("Erreur de chargement des services : " + e.getMessage());
        }
    }



    private List<Service> getSelectedServices() {
        return servicesContainer.getChildren().stream()
                .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                .map(node -> (Service) ((CheckBox) node).getUserData())
                .collect(Collectors.toList());
    }


    @FXML
    public void handleAddReservation(ActionEvent event) {
        try {
            // Validation des entrées
            if (!validateInputs()) return;

            // Récupération des données
            User user = userService.getUserByEmail(userEmailField.getText().trim());

            // Vérification si l'utilisateur existe
            if (user == null) {
                showAlert("Erreur : Utilisateur non trouvé.");
                return; // Ou gérez l'erreur comme nécessaire
            }

            Locaux lieu = reservationGP.getLocauxByName(lieuField.getValue());
            List<Service> services = getSelectedServices();

            // Création de la réservation
            Reservation reservation = createReservation(user, lieu, services);

            // Création d'un objet User (en pratique, utilisez l'utilisateur connecté)// Utilisez l'ID de l'utilisateur ici
                reservation.setUser(user); // Assurez-vous que setUserId est une méthode de Reservation

                String qrCode = reservationGP.addR(reservation);
                showQRConfirmation(qrCode);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la réservation : " + e.getMessage());
        }
    }


    private boolean validateInputs() {
        if (userEmailField.getText().trim().isEmpty()) {
            showAlert("Veuillez entrer un email utilisateur");
            return false;
        }
        if (!isValidNumber(nbreInvitesField.getText().trim())) {
            showAlert("Nombre d'invités invalide");
            return false;
        }
        if (!isValidBudget(budgetField.getText().trim())) {
            showAlert("Budget invalide");
            return false;
        }
        if (dateReservationField.getValue() == null) {
            showAlert("Veuillez sélectionner une date");
            return false;
        }
        if (getSelectedServices().isEmpty()) {
            showAlert("Veuillez sélectionner au moins un service");
            return false;
        }

        // Correction de la validation des services
        if (getSelectedServices().isEmpty()) { // Au lieu de vérifier la sélection de la liste
            showAlert("Veuillez sélectionner au moins un service");
            return false;
        }
        return true;
    }

    private boolean isValidNumber(String input) {
        try {
            int value = Integer.parseInt(input);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidBudget(String input) {
        try {
            new BigDecimal(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private Reservation createReservation(User user, Locaux lieu, List<Service> services) {
        return new Reservation(
                packId,
                user,
                Integer.parseInt(nbreInvitesField.getText().trim()),
                new BigDecimal(budgetField.getText().trim()),
                null, // QR code généré côté service
                Timestamp.valueOf(dateReservationField.getValue().atStartOfDay()),
                Reservation.StatutReservation.EN_ATTENTE,
                commentaireField.getText().trim(),
                lieu,
                services
        );
    }

    private void showQRConfirmation(String qrCode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GP/QrConfirmation.fxml"));
            Parent root = loader.load();

            QrConfirmationController controller = loader.getController();
            controller.setQrCode(qrCode);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Confirmation de réservation");
            stage.show();

        } catch (IOException e) {
            showAlert("Erreur d'affichage du QR Code : " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }
}
