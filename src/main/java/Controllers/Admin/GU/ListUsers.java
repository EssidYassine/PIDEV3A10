package Controllers.Admin.GU;

import Models.User;
import Services.ServiceUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.Set;

public class ListUsers {


    @FXML
    private VBox container;  // Correctly annotated

    private final ServiceUser userService = new ServiceUser();

    public void initialize() {
        Set<User> users = userService.getAll();
        ObservableList<User> userList = FXCollections.observableArrayList(users);

        for (User user : userList) { // Iterate through the ObservableList
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
}