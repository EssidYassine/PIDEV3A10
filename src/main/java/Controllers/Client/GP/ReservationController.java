package Controllers.Client.GP;

import Models.Locaux;
import Models.Reservation;
import Models.Service;
import Models.User;
import Services.GoogleCalendarService;
import Services.ReservationGP;
import Services.ServiceGP;
import Services.UserService;
import Tools.DataBaseConnection;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReservationController implements Initializable {

    @FXML private TextField userEmailField;
    @FXML private TextField nbreInvitesField;
    @FXML private TextField budgetField;
    @FXML private DatePicker dateReservationField;
    @FXML private TextArea commentaireField;
    @FXML private ComboBox<String> lieuField;
    @FXML private VBox servicesContainer;
    @FXML private ComboBox<String> hourComboBox;
    @FXML private ComboBox<String> minuteComboBox;

    private final ReservationGP reservationGP = new ReservationGP();
    private final UserService userService = new UserService();
    private final ServiceGP serviceGP = new ServiceGP();
    private int packId;
    DataBaseConnection cnx = new DataBaseConnection();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialisation des ComboBox d'heure
        hourComboBox.getItems().addAll(
                "00","01","02","03","04","05","06","07","08", "09", "10", "11", "12",
                "13", "14", "15", "16", "17", "18","19","20","21","22","23"
        );
        minuteComboBox.getItems().addAll("00","30");

        // Désactiver les dates antérieures à demain dans le DatePicker
        dateReservationField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Désactive toutes les dates inférieures à demain (y compris les mois précédents)
                if (date.isBefore(LocalDate.now().plusDays(1))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #EEEEEE;"); // Optionnel: changer le style pour visualiser le blocage
                }
            }
        });
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

            servicesContainer.getChildren().clear();

            for (Service service : allServices) {
                CheckBox checkBox = new CheckBox(service.getNom_service());
                checkBox.setUserData(service);

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

    // Méthode de construction de l'URL pour le QR code (invariée)
    private String buildQrUrl(Reservation reservation) {
        final String BASE_URL = "https://bright-kashata-88b9ac.netlify.app/reservation.html";
        try {
            return BASE_URL + "?" +
                    "id=" + URLEncoder.encode(String.valueOf(reservation.getReservationId()), StandardCharsets.UTF_8.name()) +
                    "&pack=" + URLEncoder.encode(String.valueOf(packId), StandardCharsets.UTF_8.name()) +
                    "&date=" + URLEncoder.encode(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(reservation.getDateReservation()), StandardCharsets.UTF_8.name()) +
                    "&invites=" + URLEncoder.encode(String.valueOf(reservation.getNbreInvites()), StandardCharsets.UTF_8.name()) +
                    "&budget=" + URLEncoder.encode(reservation.getBudgetAlloue().toPlainString(), StandardCharsets.UTF_8.name()) +
                    "&lieu=" + URLEncoder.encode(reservation.getLieu().getAdresse(), StandardCharsets.UTF_8.name()) +
                    "&services=" + URLEncoder.encode(getServicesNames(reservation), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return BASE_URL;
        }
    }

    @FXML
    public void handleAddReservation(ActionEvent event) {
        try {
            if (!validateInputs()) return;

            // Récupération de l'utilisateur
            User user = userService.getUserByEmail(userEmailField.getText().trim());
            if (user == null) {
                showAlert("Erreur : Utilisateur non trouvé.");
                return;
            }

            // Création de la date/heure
            LocalDate selectedDate = dateReservationField.getValue();
            LocalTime time = LocalTime.of(
                    Integer.parseInt(hourComboBox.getValue()),
                    Integer.parseInt(minuteComboBox.getValue())
            );
            // Si la date sélectionnée est antérieure à demain, on la force à être demain.
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            if (selectedDate.isBefore(tomorrow)) {
                selectedDate = tomorrow;
            }
            LocalDateTime dateTime = LocalDateTime.of(selectedDate, time);

            // Vérification de la disponibilité
            if (reservationGP.isDateTimeReserved(dateTime)) {
                showAlert(Alert.AlertType.ERROR, "Créneau occupé",
                        "Un événement existe déjà à cette date/heure !");
                return;
            }

            // Création de la réservation
            Reservation reservation = new Reservation();
            // Affectation du packId pour éviter qu'il reste à 0
            reservation.setPackId(packId);
            reservation.setUser(user);
            reservation.setNbreInvites(Integer.parseInt(nbreInvitesField.getText()));
            reservation.setBudgetAlloue(new BigDecimal(budgetField.getText()));
            reservation.setDateReservation(Timestamp.valueOf(dateTime));
            reservation.setLieu(reservationGP.getLocauxByName(lieuField.getValue()));
            reservation.setServices(getSelectedServices());
            reservation.setCommentaire(commentaireField.getText());

            // Enregistrement de la réservation
            String qrCode = reservationGP.addR(reservation);
            GoogleCalendarService.createCalendarEvent(reservation); // Appeler APRÈS avoir obtenu l'ID

            // Affichage de la confirmation sous forme de card (popup)
            showConfirmationCard(reservation);
            // Après avoir inséré la réservation et récupéré l'ID

        } catch (Exception e) {
            showAlert("Erreur critique: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Affiche une fenêtre popup (card) de confirmation avec un message de remerciement,
     * un bouton pour télécharger le PDF de la réservation et redirige ensuite vers la page Home.
     */
    private void showConfirmationCard(Reservation reservation) {
        // Déclaration de la fenêtre de confirmation (popup)
        final Stage dialog = new Stage();
        dialog.setTitle("Confirmation de réservation");

        VBox dialogVBox = new VBox(20);
        dialogVBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 20; -fx-border-color: #000000; " +
                "-fx-border-radius: 5; -fx-background-radius: 5;");

        // Message de remerciement
        Label thankYouLabel = new Label("Merci pour votre réservation !");
        thankYouLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Bouton pour télécharger le PDF
        Button downloadPdfButton = new Button("Télécharger votre réservation en PDF");
        downloadPdfButton.setOnAction(e -> {
            String pdfPath = generateReservationPdf(reservation);
            if (pdfPath != null) {
                showAlert(Alert.AlertType.INFORMATION, "PDF généré",
                        "Votre réservation a été enregistrée à l'emplacement :\n" + pdfPath);
            }
            // Fermer la popup
            dialog.close();
            // Rediriger vers la page Home
            redirectToHome();
        });

        dialogVBox.getChildren().addAll(thankYouLabel, downloadPdfButton);
        Scene dialogScene = new Scene(dialogVBox, 350, 150);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Redirige vers la page Home.
     */
    private void redirectToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GP/AcceuilPacks.fxml"));
            Parent root = loader.load();
            // Récupère la scène principale à partir d'un composant (ici userEmailField)
            Stage primaryStage = (Stage) userEmailField.getScene().getWindow();
            primaryStage.setScene(new Scene(root));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Styles/card.css")).toExternalForm());
            primaryStage.setTitle("Gestionnaire de Services");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
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

    private String getServicesNames(Reservation reservation) {
        return reservation.getServices().stream()
                .map(Service::getNom_service)
                .collect(Collectors.joining(", "));
    }

    private BufferedImage generateQrWithApi(String qrData) {
        try {
            String cleanQrData = qrData.replaceAll("[\\r\\n]+", " ");
            String json = String.format(
                    "{\"data\":\"%s\",\"config\":{\"body\":\"square\",\"eye\":\"frame0\",\"eyeBall\":\"ball0\"},\"size\":300,\"download\":false,\"file\":\"png\"}",
                    cleanQrData
            );
            System.out.println("JSON envoyé à l'API: " + json);

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost("https://api.qrcode-monkey.com/qr/custom");
                post.setHeader("Content-Type", "application/json");
                post.setEntity(new StringEntity(json, StandardCharsets.UTF_8));

                HttpResponse response = client.execute(post);

                if (response.getStatusLine().getStatusCode() != 200) {
                    System.err.println("Erreur API: " + response.getStatusLine());
                    return generateFallbackQr(qrData);
                }

                try (InputStream is = response.getEntity().getContent()) {
                    BufferedImage image = ImageIO.read(is);
                    if (image == null) {
                        throw new IOException("Réponse API invalide - pas d'image générée");
                    }
                    return image;
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur API QR Code: " + e.getMessage());
            return generateFallbackQr(qrData);
        }
    }

    private BufferedImage generateFallbackQr(String qrData) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 300, 300);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e) {
            System.err.println("Erreur génération locale QR: " + e.getMessage());
            return new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        }
    }

    private String generateReservationPdf(Reservation reservation) {
        try {
            String qrUrl = buildQrUrl(reservation);
            BufferedImage qrImage = generateQrWithApi(qrUrl);

            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Votre Réservation");
            contentStream.endText();

            PDImageXObject pdImage = LosslessFactory.createFromImage(document, qrImage);
            contentStream.drawImage(pdImage, 50, 500, 200, 200);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 470);
            String details = "ID : " + reservation.getReservationId() + " | Pack : " + packId +
                    " | Date : " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(reservation.getDateReservation());
            contentStream.showText(details);
            contentStream.endText();

            contentStream.close();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.setInitialFileName("reservation_" + reservation.getReservationId() + ".pdf");
            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                document.save(file);
                document.close();
                return file.getAbsolutePath();
            }
            return null;
        } catch (Exception e) {
            showAlert("Erreur PDF : " + e.getMessage());
            return null;
        }
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void retourHome1(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GP/Home1.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestionnaire de Services");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de Home1.fxml !");
        }
    }

}
