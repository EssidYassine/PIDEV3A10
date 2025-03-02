package Controllers.Client.GU;
import Tools.AppUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import Tools.AppUtils;

public class chatbot {

    @FXML
    private ImageView backflech;
    @FXML
    private WebView affichage;

    private WebEngine webEngine;

    @FXML
    public void initialize() {
        backflech.setOnMouseClicked(event -> gotodetails());

        webEngine = affichage.getEngine();
        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<body style='margin: 0; padding: 0; height: 100vh;'>"
                + "<iframe"
                + " src='https://www.chatbase.co/chatbot-iframe/2m-m9hUPk5XDTr7m1D2m7'"
                + " style='width: 100%; height: 100%; min-height: 700px; border: none;'"
                + "></iframe>"
                + "</body>"
                + "</html>";
        webEngine.loadContent(htmlContent);
    }


    private void gotodetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GU/HOME1.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backflech.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
