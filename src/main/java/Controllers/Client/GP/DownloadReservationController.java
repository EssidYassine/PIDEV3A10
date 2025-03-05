package Controllers.Client.GP;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class DownloadReservationController {

    @FXML private Label infoLabel;
    @FXML private Button downloadReservationButton;

    private String pdfPath;

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
        downloadReservationButton.setVisible(true);
        infoLabel.setText("PDF prêt : " + pdfPath);
    }

    @FXML
    private void handleDownloadReservation(ActionEvent event) {
        if (pdfPath != null) {
            try {
                File file = new File(pdfPath);
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                } else {
                    infoLabel.setText("Erreur : Fichier introuvable");
                }
            } catch (IOException e) {
                infoLabel.setText("Erreur d'ouverture : " + e.getMessage());
            }
        }
    }

    @FXML
    private void retourHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Client/GP/Home1.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}