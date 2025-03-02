package Tools;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AppUtils {

    // Méthode statique pour naviguer entre les scènes
    public static void gotoScene(Stage stage, String path, String title) {
        try {
            // Charger le fichier FXML à partir du chemin donné
            FXMLLoader loader = new FXMLLoader(AppUtils.class.getResource(path));
            Parent root = loader.load();

            // Créer une nouvelle scène avec le contenu du FXML chargé
            Scene scene = new Scene(root, 1086, 700); // Vous pouvez ajuster la taille ici

            // Définir la scène dans le stage
            stage.setScene(scene);

            // Définir le titre de la fenêtre
            stage.setTitle(title);

            // Centrer la fenêtre sur l'écran
            stage.centerOnScreen();

            // Empêcher le redimensionnement de la fenêtre (si nécessaire)
            stage.setResizable(false);

            // Afficher la fenêtre
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
