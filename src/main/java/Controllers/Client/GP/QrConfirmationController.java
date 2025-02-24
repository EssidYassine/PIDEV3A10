package Controllers.Client.GP;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public class QrConfirmationController {

    private BitMatrix bitMatrix; // Ajouter cette variable membre

    @FXML
    private ImageView qrImageView;
    @FXML
    private Label qrCodeLabel;

    public void setQrCode(String qrData) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 200, 200); // Stocker le BitMatrix

            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            Image qrImage = SwingFXUtils.toFXImage(bufferedImage, null);

            qrImageView.setImage(qrImage);
            qrCodeLabel.setText(qrData);

        } catch (WriterException e) {
            handleQrError(qrData, e);
        }
    }

    // Méthode corrigée pour utiliser BitMatrix
    public void saveQrCode(String filename) {
        try {
            MatrixToImageWriter.writeToPath(
                    bitMatrix, // Utiliser le BitMatrix stocké
                    "PNG",
                    java.nio.file.Paths.get(filename)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleQrError(String qrData, Exception e) {
        e.printStackTrace();
        qrCodeLabel.setText("Erreur de génération: " + qrData);
        qrImageView.setVisible(false);
    }

    // Méthode pratique pour déclencher la sauvegarde
    public void handleSaveButton() {
        saveQrCode("qrcode_" + System.currentTimeMillis() + ".png");
    }


}