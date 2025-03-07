package Controllers.Client.GS;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;

import Models.Reservation;
import Services.ReservationService;
import com.itextpdf.text.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.FadeTransition;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import utils.StripeService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class ListeServiceConfirmer {

    @FXML
    private GridPane gridPaneReservations;

    @FXML
    private ScrollPane scrollPaneReservations;

    @FXML
    private Button btnRetour;

    private final ReservationService reservationService = new ReservationService();
    private List<Reservation> listeDesReservations;
    @FXML
    private Button btnValiderPanier;
    @FXML
    private TextField cardNumber, expiryDate, cvc;

    @FXML
    private AnchorPane contenuPrincipal;


    @FXML
    public void initialize() {

        try {
            // Récupération de toutes les réservations depuis la base
            List<Reservation> toutesReservations = reservationService.getAll();
            // Filtrer pour ne garder que celles dont le statut est "Confirmée"
            listeDesReservations = toutesReservations.stream()
                    .filter(r -> r.getStatut().getValue().equalsIgnoreCase("Confirmée"))
                    .collect(Collectors.toList());

            int column = 0;
            int row = 0;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Reservation res : listeDesReservations) {
                // Création de la carte pour chaque réservation
                VBox carteReservation = new VBox(10);
                carteReservation.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 15; -fx-border-radius: 10; " +
                        "-fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
                carteReservation.setPrefWidth(250);

                // Image du service réservé
                ImageView imageView = new ImageView();
                try {
                    imageView.setImage(new Image(res.getService().getImage_url()));
                } catch (Exception e) {
                    // Si l'image ne peut être chargée, on laisse vide
                }
                imageView.setFitWidth(230);
                imageView.setFitHeight(150);

                // Informations sur la réservation
                Label nomService = new Label("Service: " + res.getService().getNom_service());
                nomService.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #333;");

                Label utilisateur = new Label("Utilisateur: " + res.getUtilisateur().getUsername());
                utilisateur.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

                Label dateReservation = new Label("Date: " + res.getDate_reservation().format(dtf));
                dateReservation.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

                Label quantite = new Label("Quantité: " + res.getQuantite());
                quantite.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");



                // Ajout des éléments à la carte
                carteReservation.getChildren().addAll(imageView, nomService, utilisateur, dateReservation, quantite);

                // Animation de transition (optionnelle)
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), carteReservation);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();

                // Ajout de la carte dans le GridPane
                gridPaneReservations.add(carteReservation, column, row);
                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void retourHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/Home1.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de Home1.fxml !");
        }
    }
    @FXML
    private void validerPanier() {
        genererContratPDF();
    }

    private void genererContratPDF() {
        Document document = new Document();
        try {
            String cheminDocuments = System.getProperty("user.home") + "/Documents/contrat_reservations.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(cheminDocuments));

            document.open();

            // **🔹 CHARGEMENT DU LOGO SANS getInstance()**

            // 🔹 **Titre**
            Paragraph titre = new Paragraph("Contrat de Réservation",
                    new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.DARK_GRAY));
            titre.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(titre);
            document.add(new Paragraph("\n"));

            // 🔹 **Ajout du tableau**
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            PdfPCell header1 = new PdfPCell(new Paragraph("Service", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            PdfPCell header2 = new PdfPCell(new Paragraph("Utilisateur", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            PdfPCell header3 = new PdfPCell(new Paragraph("Date", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            PdfPCell header4 = new PdfPCell(new Paragraph("Quantité", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

            BaseColor headerColor = new BaseColor(220, 220, 220);
            header1.setBackgroundColor(headerColor);
            header2.setBackgroundColor(headerColor);
            header3.setBackgroundColor(headerColor);
            header4.setBackgroundColor(headerColor);

            table.addCell(header1);
            table.addCell(header2);
            table.addCell(header3);
            table.addCell(header4);

            // 🔹 **Remplissage du tableau**
            double totalPrix = 0;
            int totalReservations = 0;
            for (Reservation res : listeDesReservations) {
                if (res.getStatut().getValue().equalsIgnoreCase("Confirmée")) {
                    table.addCell(res.getService().getNom_service());
                    table.addCell(res.getUtilisateur().getUsername());
                    table.addCell(res.getDate_reservation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    table.addCell(String.valueOf(res.getQuantite()));

                    totalPrix += res.getQuantite() * res.getService().getPrix();
                    totalReservations++;
                }
            }

            document.add(table);

            // 🔹 **Résumé**
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("📌 Total des réservations : " + totalReservations,
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            document.add(new Paragraph("💰 Montant total : " + String.format("%.2f", totalPrix) + " €",
                    new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.RED)));
            document.add(new Paragraph("\n"));

            // 🔹 **Signature**
            document.add(new Paragraph("✍ Signature du client : ____________________________"));
            document.add(new Paragraph("\n"));

            // 🔹 **Message de remerciement**
            Paragraph merci = new Paragraph("🙏 Merci pour votre confiance !",
                    new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, BaseColor.BLUE));
            merci.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(merci);

            document.close();

            // 🔹 **Confirmation**
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("PDF Généré");
            alert.setHeaderText(null);
            alert.setContentText("Le contrat a été généré avec succès !\n📂 Fichier : " + cheminDocuments);
            alert.showAndWait();

            System.out.println("✅ PDF généré avec succès !");
        } catch (DocumentException | IOException e) {
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

    public void gotoHome(MouseEvent mouseEvent) {
        try {
            // Charger l'interface AcceuilService.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/Home1.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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

        // **Calcul du montant total des réservations confirmées**
        double totalAmount = listeDesReservations.stream()
                .mapToDouble(res -> res.getQuantite() * res.getService().getPrix()) // Prix total = quantité * prix du service
                .sum();

        if (totalAmount <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Paiement annulé");
            alert.setHeaderText(null);
            alert.setContentText("Aucune réservation confirmée à payer.");
            alert.showAndWait();
            return;
        }

        // Stripe attend un montant en centimes (10€ => 1000)
        int montantEnCentimes = (int) (totalAmount * 100);

        // Appel au service Stripe
        StripeService stripeService = new StripeService();
        String paymentMethodId = "pm_card_visa"; // PaymentMethod test Stripe
        String clientSecret = stripeService.createPaymentIntent(montantEnCentimes, paymentMethodId);

        if (clientSecret != null) {
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

    @FXML
    private void ouvrirFormulairePaiement() {
        try {
            // Charger le formulaire de paiement depuis le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GS/Paiement.fxml"));
            AnchorPane paiementPane = loader.load();

            // Remplace le contenu principal par le formulaire de paiement
            contenuPrincipal.getChildren().setAll(paiementPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
