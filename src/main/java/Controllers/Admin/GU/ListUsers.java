package Controllers.Admin.GU;

import Models.User;
import Services.ServiceUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Set;

public class ListUsers {
    @FXML
    private ImageView backflech;

    @FXML
    private VBox container;

    private final ServiceUser userService = new ServiceUser();

    public void initialize() {
        loadUserList(); // Appel à la méthode pour charger les utilisateurs

        backflech.setOnMouseClicked(event -> handleBack()); // Utilisation de setOnMouseClicked

        backflech.setCursor(javafx.scene.Cursor.HAND); // Change le curseur en main
    }

    private void loadUserList() {
        Set<User> users = userService.getAll();
        ObservableList<User> userList = FXCollections.observableArrayList(users);

        for (User user : userList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GU/ClientCell.fxml"));
                Parent root = loader.load();
                ClientCellController controller = loader.getController();
                controller.setUserData(user);

                container.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Admin/GU/Home.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backflech.getScene().getWindow(); // Utilisez backflech ici
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
