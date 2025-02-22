package Controllers.Client.GS;

import Models.Reservation;
import Models.Service;
import Models.User;
import Services.ReservationService;
import Services.ServiceService;
import com.sun.javafx.collections.ElementObservableListDecorator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationMaterielController {

    @FXML
    private Button btnRetour1;

    @FXML
    private ComboBox<Service> comboService;

    @FXML
    private TextField txtUtilisateur;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField timeField;

    @FXML
    private TextField txtQuantite;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnAnnuler;
    private ReservationService reservationService = new ReservationService();


    /**
     * Méthode d'initialisation appelée automatiquement après le chargement du fichier FXML.
     */
    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur AcceuilService");
        try {
            // Récupération de tous les services
            List<Service> allServices = new Services.ServiceService().getAll();
            // Filtrer uniquement les services de type "Matériel"
            List<Service> materielServices = new ArrayList<>();
            for (Service s : allServices) {
                if (s.getType_service() != null && s.getType_service().name().equalsIgnoreCase("Matériel")) {
                    materielServices.add(s);}
            }
            // Ajouter ces services filtrés à la ComboBox
            comboService.getItems().addAll(materielServices);

            // Configurez le convertisseur pour afficher uniquement le nom du service
            comboService.setConverter(new StringConverter<Service>() {
                @Override
                public String toString(Service service) {
                    return service == null ? "" : service.getNom_service();
                }

                @Override
                public Service fromString(String string) {
                    return null; // pas utilisé dans ce contexte
                }
            });

            // Configurez la cell factory pour que la liste affiche uniquement le nom du service
            comboService.setCellFactory(lv -> new ListCell<Service>() {
                @Override
                protected void updateItem(Service item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getNom_service());
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Méthode appelée lors du clic sur le bouton "Acceuil".
     */
    @FXML
    private void retourHome1(ActionEvent event) { try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/Home1.fxml"));
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

    /**
     * Méthode appelée lors du clic sur le bouton "Ajouter".
     */
    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            // Récupération du service sélectionné
            Service selectedService = comboService.getValue();
            if (selectedService == null) {
                System.out.println("Veuillez sélectionner un service.");
                return;
            }

            // Récupération du nom de l'utilisateur
            String utilisateurName = txtUtilisateur.getText();
            if (utilisateurName == null || utilisateurName.isEmpty()) {
                System.out.println("Veuillez entrer le nom de l'utilisateur.");
                return;
            }

            // Récupération de la date de réservation
            LocalDate date = datePicker.getValue();
            if (date == null) {
                System.out.println("Veuillez sélectionner une date.");
                return;
            }

            // Récupération et vérification de l'heure
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
            LocalDateTime reservationDateTime = LocalDateTime.of(date, LocalTime.of(hour, minute));

            // Récupération et vérification de la quantité demandée
            int quantite;
            try {
                quantite = Integer.parseInt(txtQuantite.getText().trim());
            } catch (NumberFormatException e) {
                System.out.println("Quantité invalide.");
                return;
            }

            // Vérification de la disponibilité : la quantité demandée doit être inférieure ou égale à celle du service

                if (selectedService.getQuantite_materiel() < quantite) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Quantité insuffisante");
                    alert.setHeaderText(null);
                    alert.setContentText("Quantité insuffisante. Disponible : " + selectedService.getQuantite_materiel());
                    alert.showAndWait();
                    return;

            }

            // Création d'un objet User à partir du nom saisi (en pratique, utilisez l'utilisateur connecté)
            User user = new User(1, utilisateurName, "", "");

            // Définir un statut par défaut, par exemple "En attente"
            Reservation.Statut statut = Reservation.Statut.fromValue("En attente");

            // Création de l'objet Reservation (id_reservation sera généré en BDD)
            Reservation reservation = new Reservation(
                    0, // id_reservation : 0 pour une nouvelle réservation
                    selectedService,
                    user,
                    reservationDateTime,
                    quantite,
                    statut,
                    null, // date_confirmation par défaut
                    null  // date_annulation par défaut
            );

            // Appel du service pour ajouter la réservation en base
            reservationService.add(reservation);
            System.out.println("Réservation ajoutée avec succès !");
            // Décrémenter la quantité disponible du service
            int nouvelleQuantite = selectedService.getQuantite_materiel() - quantite;
            ServiceService.updateQuantiteAndAvailability(selectedService.getId_service(), nouvelleQuantite);
            System.out.println("Quantité du service mise à jour : " + nouvelleQuantite);
// Vous pouvez mettre à jour l'objet si besoin
            selectedService.setQuantite_materiel(nouvelleQuantite);

// Utiliser la méthode updateQuantite pour mettre à jour uniquement la quantité dans la base
            ServiceService serviceService = new ServiceService(); // ou utilisez un service déjà instancié
            serviceService.updateQuantite(selectedService.getId_service(), nouvelleQuantite);
            System.out.println("Quantité du service mise à jour : " + nouvelleQuantite);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/AcceuilService.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode appelée lors du clic sur le bouton "Annuler".
     */
    @FXML
    private void handleAnnuler(ActionEvent event) {
        // Logique pour annuler l'ajout de réservation
        System.out.println("Bouton Annuler cliqué");
    }

    public void retourAcceuilService(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/AcceuilService.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de AcceuilService.fxml !");
        }

    }
}
