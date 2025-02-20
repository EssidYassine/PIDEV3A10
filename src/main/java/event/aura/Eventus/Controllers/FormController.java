package event.aura.Eventus.Controllers;



import event.aura.Eventus.Models.Post;
import event.aura.Eventus.Models.User;
import event.aura.Eventus.Services.PostService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jdk.jfr.Description;

import java.time.LocalDateTime;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import java.io.File;
import java.sql.SQLException;

public class FormController {
    Stage stage;

    @FXML
    private Button inventory_addBtn;

    @FXML
    private TextField inventory_col_productDescription;

    @FXML
    private TextField inventory_col_productName;

    @FXML
    private Button inventory_deleteBtn;

    @FXML
    private ImageView inventory_imageView;

    @FXML
    private Button inventory_importBtn;

    @FXML
    private Button inventory_updateBtn;

    @FXML
    private AnchorPane main_form;
    private File selectedImageFile;
    private final PostService articleService = new PostService();



@FXML
    void AddProduct(ActionEvent event) {
    String title = inventory_col_productName.getText();
    String content = inventory_col_productDescription.getText();
    String imagePath = (selectedImageFile != null) ? selectedImageFile.getAbsolutePath() : "";

    int nb_likes=0;


    if (title.isEmpty() || content.isEmpty()) {
        System.out.println(" Erreur: Veuillez remplir tous les champs !");
        return;
    }

    User user = new User(); // Simuler un utilisateur connecté
    user.setId_user(1);

    Post article = new Post(title, content, imagePath, nb_likes, LocalDateTime.now(), user);
    try {
        articleService.add(article);
        System.out.println("Article publié avec succès !");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    @FXML
    private void handleCancel() {
        if (stage != null) {
            stage.close(); // Close the current window
        }
    }

    // Dummy method to simulate getting the current logged-in user
    private User getCurrentUser() {
        // Replace this with your actual logic to get the current logged-in user
        return new User (); // Example User with ID 1
    }




    @FXML
    void DeleteProduct(ActionEvent event) {

    }

    @FXML
    void UpdateProduct(ActionEvent event) {

    }
    @FXML
    void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", ".png", ".jpg", ".jpeg", ".gif"));
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            inventory_imageView.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }



private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;
    Alert alert = new Alert(AlertType.INFORMATION);

    private Image image;


    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
















