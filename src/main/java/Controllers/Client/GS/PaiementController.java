package Controllers.Client.GS;

import Controllers.NotificationService;
import Models.Reservation;
import Services.ReservationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.StripeService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PaiementController {

    @FXML
    private TextField cardNumber, expiryDate, cvc;

    @FXML
    private void processPayment() {
        String cardNum = cardNumber.getText();
        String expDate = expiryDate.getText();
        String cvcCode = cvc.getText();

        if (cardNum.isEmpty() || expDate.isEmpty() || cvcCode.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir toutes les informations de la carte.");
            alert.showAndWait();
            return;
        }

        // Calcul du montant total des services confirmés
        double totalAmount = calculerTotal();

        if (totalAmount <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Aucune réservation confirmée à payer !");
            alert.showAndWait();
            return;
        }

        int montantEnCentimes = (int) (totalAmount * 100);

        // Appel à Stripe
        StripeService stripeService = new StripeService();
        String paymentMethodId = "pm_card_visa"; // PaymentMethod test Stripe
        String clientSecret = stripeService.createPaymentIntent(montantEnCentimes, paymentMethodId);

        if (clientSecret != null) {
            // Envoi de la notification par SMS
            NotificationService notificationService = new NotificationService();
            // Ici, le numéro de téléphone peut être récupéré depuis l'utilisateur connecté ou être statique pour le test
            String phoneNumber = "+21699831083"; // Exemple de numéro de téléphone
            notificationService.sendNotification(phoneNumber, "Votre paiement de " + totalAmount + " dt a été validé.");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Paiement en cours");
            alert.setHeaderText(null);
            alert.setContentText("Montant payé : " + totalAmount + " €. Vérifiez votre confirmation Stripe.");
            alert.showAndWait();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Échec du paiement");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du paiement. Veuillez réessayer.");
            alert.showAndWait();
        }
}

    private double calculerTotal() {
        ReservationService reservationService = new ReservationService();
        double total = 0.0;

        try {
            // Récupérer toutes les réservations confirmées
            List<Reservation> reservations = reservationService.getAll();

            for (Reservation res : reservations) {
                if (res.getStatut().getValue().equalsIgnoreCase("Confirmée")) {
                    total += res.getService().getPrix() * res.getQuantite();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public void gotoHome(MouseEvent mouseEvent) {
        try {
            // Charger l'interface AcceuilService.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/Home1.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void retourAcceuil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/AcceuilService.fxml"));
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
