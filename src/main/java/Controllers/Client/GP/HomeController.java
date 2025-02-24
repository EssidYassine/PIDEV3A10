
package Controllers.Client.GP;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    public void gotoAcceuilService(MouseEvent mouseEvent) {
        try {
            // Charger l'interface AcceuilService.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GP/Acceuil.fxml"));
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

    public void gotoAcceuilPack(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Client/GP/AcceuilPacks.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène à partir du root et y ajouter la feuille de style
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/Styles/card.css").toExternalForm());

            // Récupérer la scène actuelle via le stage
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des Packs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
