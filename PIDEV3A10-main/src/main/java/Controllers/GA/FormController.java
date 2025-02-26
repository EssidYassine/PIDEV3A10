package Controllers.GA;

import Models.Post;
import Models.User;
import Services.PostService;
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
    // All your existing fields remain unchanged
    Stage stage;

    @FXML private Button inventory_addBtn;
    @FXML private TextField inventory_col_productDescription;
    @FXML private TextField inventory_col_productName;
    @FXML private Button inventory_deleteBtn;
    @FXML private ImageView inventory_imageView;
    @FXML private Button inventory_importBtn;
    @FXML private Button inventory_updateBtn;
    @FXML private AnchorPane main_form;
    private File selectedImageFile;
    private final PostService articleService = new PostService();

    @FXML
    void AddProduct(ActionEvent event) {
        if (validateInputs()) {
            String title = inventory_col_productName.getText();
            String content = inventory_col_productDescription.getText();
            String imagePath = (selectedImageFile != null) ? selectedImageFile.getAbsolutePath() : "";
            int nb_likes = 0;

            User user = new User(); // Simuler un utilisateur connecté
            user.setId_user(1);

            Post article = new Post(title, content, imagePath, nb_likes, LocalDateTime.now(), user);
            try {
                articleService.add(article);
                showAlert(AlertType.INFORMATION, "Succès", "Publication réussie", "Article publié avec succès !");
                clearFields();
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Erreur", "Erreur de Base de Données",
                        "Une erreur s'est produite lors de l'enregistrement: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private boolean validateInputs() {
        String title = inventory_col_productName.getText().trim();
        String content = inventory_col_productDescription.getText().trim();

        if (title.isEmpty()) {
            showAlert(AlertType.WARNING, "Erreur de Validation", "Titre Requis",
                    "Veuillez entrer un titre pour votre post.");
            inventory_col_productName.requestFocus();
            return false;
        }

        if (title.length() < 3) {
            showAlert(AlertType.WARNING, "Erreur de Validation", "Titre Invalide",
                    "Le titre doit contenir au moins 3 caractères.");
            inventory_col_productName.requestFocus();
            return false;
        }

        if (content.isEmpty()) {
            showAlert(AlertType.WARNING, "Erreur de Validation", "Description Requise",
                    "Veuillez entrer une description pour votre post.");
            inventory_col_productDescription.requestFocus();
            return false;
        }

        if (content.length() < 10) {
            showAlert(AlertType.WARNING, "Erreur de Validation", "Description Invalide",
                    "La description doit contenir au moins 10 caractères.");
            inventory_col_productDescription.requestFocus();
            return false;
        }

        if (selectedImageFile == null || inventory_imageView.getImage() == null) {
            showAlert(AlertType.WARNING, "Erreur de Validation", "Image Requise",
                    "Veuillez télécharger une image pour votre post.");
            return false;
        }

        return true;
    }

    private void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        inventory_col_productName.clear();
        inventory_col_productDescription.clear();
        inventory_imageView.setImage(null);
        selectedImageFile = null;
    }

    // All your other existing methods remain unchanged
    @FXML
    private void handleCancel() {
        if (stage != null) {
            stage.close();
        }
    }

    private User getCurrentUser() {
        return new User();
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