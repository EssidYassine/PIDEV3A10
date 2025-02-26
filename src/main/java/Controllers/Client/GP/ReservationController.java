package Controllers.Client.GP;

import Models.Locaux;
import Models.Reservation;
import Models.Service;
import Models.User;
import Services.ReservationGP;
import Services.ServiceGP;
import Services.UserService;
import Tools.DataBaseConnection;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReservationController {

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
    // Ajoutez cette méthode pour construire l'URL avec les données de réservation
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

            String userEmail = userEmailField.getText().trim();
            if (userEmail.isEmpty()) {
                showAlert("Erreur : L'email de l'utilisateur ne peut pas être vide.");
                return;
            }

            User user = userService.getUserByEmail(userEmail);
            if (user == null) {
                showAlert("Erreur : Utilisateur non trouvé.");
                return;
            }

            Locaux lieu = reservationGP.getLocauxByName(lieuField.getValue());
            List<Service> services = getSelectedServices();

            Reservation reservation = createReservation(user, lieu, services);
            reservationGP.addR(reservation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le pack a été ajouté avec succès !");
            redirectToPackPage(event);

            // Construire l'URL de QR code avec les données de réservation
            String qrUrl = buildQrUrl(reservation);
            BufferedImage qrImage = generateQrWithApi(qrUrl);
            showQRConfirmation(qrImage, qrUrl);

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
                null,
                Timestamp.valueOf(dateReservationField.getValue().atStartOfDay()),
                Reservation.StatutReservation.EN_ATTENTE,
                commentaireField.getText().trim(),
                lieu,
                services
        );
    }

    private String generateQrContent(Reservation reservation) {
        return String.format(
                "Réservation #%d\nPack: %d\nDate: %s\nInvites: %d\nBudget: %s€\nLieu: %s\nServices: %s",
                reservation.getReservationId(),
                packId,
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(reservation.getDateReservation()),
                reservation.getNbreInvites(),
                reservation.getBudgetAlloue().toPlainString(),
                reservation.getLieu().getAdresse(),
                getServicesNames(reservation)
        );
    }

    private String getServicesNames(Reservation reservation) {
        return reservation.getServices().stream()
                .map(Service::getNom_service)
                .collect(Collectors.joining(", "));
    }

    private BufferedImage generateQrWithApi(String qrData) {
        try {
            // Remplacer les retours à la ligne (\r, \n) par un espace
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

                // Vérifier le code de statut HTTP
                if (response.getStatusLine().getStatusCode() != 200) {
                    System.err.println("Erreur API: " + response.getStatusLine());
                    return generateFallbackQr(qrData); // Génération locale de secours
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
            return generateFallbackQr(qrData); // Retourne un QR code local en cas d'erreur
        }
    }

    // Méthode de secours avec ZXing
    private BufferedImage generateFallbackQr(String qrData) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 300, 300);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e) {
            System.err.println("Erreur génération locale QR: " + e.getMessage());
            // Retourne une image vide si tout échoue
            return new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        }
    }

    private void showQRConfirmation(BufferedImage qrImage, String qrUrl) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GP/QrConfirmation.fxml"));
            Parent root = loader.load();
            QrConfirmationController controller = loader.getController();

            if (qrImage == null) {
                qrImage = generateFallbackQr("Erreur de génération du QR code");
                if (qrImage == null) {
                    throw new IllegalStateException("Impossible de générer un QR code de secours.");
                }
            }

            // Préparation de la map contenant directement le QR URL
            Map<String, String> reservationDetails = new HashMap<>();
            reservationDetails.put("qrContent", qrUrl);
            controller.setQrData(reservationDetails);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Confirmation de Réservation");
            stage.show();

        } catch (IOException e) {
            showAlert("Erreur de chargement de l'interface : " + e.getMessage());
        } catch (IllegalStateException e) {
            showAlert("Erreur de génération du QR code : " + e.getMessage());
        } catch (Exception e) {
            showAlert("Erreur inattendue : " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
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

    private void redirectToPackPage(ActionEvent event) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GP/AcceuilPacks.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène à partir du root et y ajouter la feuille de style
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/Styles/card.css").toExternalForm());

            // Récupérer la scène actuelle via le stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des Packs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}