package Controllers.Client.GP;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class QrConfirmationController {

    @FXML private ImageView qrImageView;
    @FXML private Label detailsLabel;
    @FXML private Hyperlink urlLink;

    private static final String BASE_URL = "https://bright-kashata-88b9ac.netlify.app/reservation.html";

    // Dans QrConfirmationController.java

    public void setQrData(Map<String, String> reservationDetails) {
        if (reservationDetails == null) {
            throw new IllegalArgumentException("Reservation details cannot be null");
        }

        String qrContent;
        // Si "qrContent" est présent, on l'utilise directement
        if (reservationDetails.containsKey("qrContent") && !reservationDetails.get("qrContent").isEmpty()) {
            qrContent = reservationDetails.get("qrContent");
        } else {
            qrContent = generateReservationUrl(reservationDetails);
        }

        BufferedImage qrImage = generateQrImage(qrContent);

        if (qrImage != null) {
            qrImageView.setImage(SwingFXUtils.toFXImage(qrImage, null));
        } else {
            // Chargement de l'image par défaut via les ressources
            Image defaultImage = new Image(getClass().getResourceAsStream("/Images/icons8-sortie-48 (1).png"));
            qrImageView.setImage(defaultImage);
        }

        detailsLabel.setText("Scannez le QR Code ou cliquez sur le lien ci-dessous.");
        urlLink.setText(qrContent);
        urlLink.setOnAction(e -> openBrowser(qrContent));
    }


    private String generateReservationUrl(Map<String, String> details) {
        // Si la map contient la clé "qrContent", on retourne sa valeur
        if (details.containsKey("qrContent") && details.get("qrContent") != null && !details.get("qrContent").isEmpty()) {
            return details.get("qrContent");
        }
        try {
            // Pour chaque détail, si la valeur est nulle, on utilise une chaîne vide
            String id = details.get("id") != null ? details.get("id") : "";
            String pack = details.get("pack") != null ? details.get("pack") : "";
            String date = details.get("date") != null ? details.get("date") : "";
            String invites = details.get("invites") != null ? details.get("invites") : "";
            String budget = details.get("budget") != null ? details.get("budget") : "";
            String lieu = details.get("lieu") != null ? details.get("lieu") : "";
            String services = details.get("services") != null ? details.get("services") : "";

            return BASE_URL + "?" +
                    "id=" + URLEncoder.encode(id, StandardCharsets.UTF_8.name()) +
                    "&pack=" + URLEncoder.encode(pack, StandardCharsets.UTF_8.name()) +
                    "&date=" + URLEncoder.encode(date, StandardCharsets.UTF_8.name()) +
                    "&invites=" + URLEncoder.encode(invites, StandardCharsets.UTF_8.name()) +
                    "&budget=" + URLEncoder.encode(budget, StandardCharsets.UTF_8.name()) +
                    "&lieu=" + URLEncoder.encode(lieu, StandardCharsets.UTF_8.name()) +
                    "&services=" + URLEncoder.encode(services, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return BASE_URL;
        }
    }

    private BufferedImage generateQrImage(String qrData) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 300, 300);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e) {
            return null;
        }
    }

    private void openBrowser(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;

            if (os.contains("win")) {
                // Windows
                processBuilder = new ProcessBuilder("cmd", "/c", "start", url);
            } else if (os.contains("mac")) {
                // macOS
                processBuilder = new ProcessBuilder("open", url);
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                // Linux/Unix
                processBuilder = new ProcessBuilder("xdg-open", url);
            } else {
                throw new UnsupportedOperationException("Système d'exploitation non supporté : " + os);
            }

            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.INFORMATION, "Erreur lors de l'ouverture du navigateur : " , e.getMessage());
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.INFORMATION,"", e.getMessage());
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