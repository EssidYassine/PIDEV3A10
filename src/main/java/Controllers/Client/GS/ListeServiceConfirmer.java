package Controllers.Client.GS;

import Models.Reservation;
import Services.ReservationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.FadeTransition;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.control.Alert;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

                Label utilisateur = new Label("Utilisateur: " + res.getUtilisateur().getNom());
                utilisateur.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

                Label dateReservation = new Label("Date: " + res.getDate_reservation().format(dtf));
                dateReservation.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

                Label quantite = new Label("Quantité: " + res.getQuantite());
                quantite.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

                // Bouton Détail (optionnel, vous pouvez adapter ou retirer)
                Button btnDetail = new Button("Détail");
                btnDetail.setStyle("-fx-background-color: #1e0fc6; -fx-text-fill: white; -fx-background-radius: 10;");
                btnDetail.setOnAction(event -> afficherDetails(res));

                // Ajout des éléments à la carte
                carteReservation.getChildren().addAll(imageView, nomService, utilisateur, dateReservation, quantite, btnDetail);

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

    private void afficherDetails(Reservation res) {
        System.out.println("Détails de la réservation : " + res);
        // Vous pouvez étendre cette méthode pour ouvrir une vue détaillée
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
            // Chemin vers le dossier "Documents" de l'utilisateur
            String cheminDocuments = System.getProperty("user.home") + "/Documents/contrat_reservations.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(cheminDocuments));

            document.open();
            document.add(new Paragraph("Contrat de Réservation"));
            document.add(new Paragraph("Date de génération : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            document.add(new Paragraph(" "));

            // Création du tableau PDF
            PdfPTable table = new PdfPTable(4); // 4 colonnes
            table.addCell("Service");
            table.addCell("Utilisateur");
            table.addCell("Date");
            table.addCell("Quantité");

            for (Reservation res : listeDesReservations) {
                // Vérification du statut pour s'assurer que la réservation est bien confirmée
                if (res.getStatut().getValue().equalsIgnoreCase("Confirmée")) {
                    table.addCell(res.getService().getNom_service());
                    table.addCell(res.getUtilisateur().getNom());
                    table.addCell(res.getDate_reservation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    table.addCell(String.valueOf(res.getQuantite()));
                }
            }

            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Signature : _____________________"));
            document.close();

            // Alerte de confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("PDF Généré");
            alert.setHeaderText(null);
            alert.setContentText("Le contrat a été généré avec succès !");
            alert.showAndWait();

            System.out.println("PDF généré avec succès !");
        } catch (DocumentException | FileNotFoundException e) {
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
}
