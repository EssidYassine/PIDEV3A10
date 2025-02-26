package Controllers.GA ;

import Models.Post;
import Services.PostService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;

public class UpdateController {

    private Post currentPost;
    private final PostService postService = new PostService();

    @FXML
    private TextField titleField;

    @FXML
    private TextArea contentField;

    @FXML
    private ImageView imageView;

    @FXML
    private Button updateButton, uploadImageButton;

    public void setPost(Post post) {
        this.currentPost = post;
        if (post != null) {
            System.out.println("Editing Post: " + post.getTitle());
            loadPostData();
        } else {
            System.out.println("Post is NULL");
        }
    }

    private void loadPostData() {
        if (currentPost != null) {
            titleField.setText(currentPost.getTitle());
            contentField.setText(currentPost.getDescription());

            // Load image if available
            if (currentPost.getImage() != null && !currentPost.getImage().isEmpty()) {
                Image image = new Image("file:" + currentPost.getImage());
                imageView.setImage(image);
            } else {
                System.out.println("No image found for this post.");
            }
        }
    }

    @FXML
    private void updatePost() {
        if (validateInputs()) {
            try {
                // Update the Post object
                currentPost.setTitle(titleField.getText().trim());
                currentPost.setDescription(contentField.getText().trim());

                // Update post in the database
                postService.update(currentPost);

                showAlert("Succès", "Le post a été mis à jour avec succès.", Alert.AlertType.INFORMATION);

                // Close the window
                ((Stage) updateButton.getScene().getWindow()).close();

            } catch (SQLException e) {
                showAlert("Erreur", "Échec de la mise à jour du post: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur inattendue s'est produite.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private boolean validateInputs() {
        String title = titleField.getText().trim();
        String content = contentField.getText().trim();

        // Validate title
        if (title.isEmpty()) {
            showAlert("Erreur de Validation", "Le titre est requis.", Alert.AlertType.WARNING);
            titleField.requestFocus();
            return false;
        }

        if (title.length() < 3) {
            showAlert("Erreur de Validation", "Le titre doit contenir au moins 3 caractères.", Alert.AlertType.WARNING);
            titleField.requestFocus();
            return false;
        }

        if (title.length() > 100) {
            showAlert("Erreur de Validation", "Le titre ne doit pas dépasser 100 caractères.", Alert.AlertType.WARNING);
            titleField.requestFocus();
            return false;
        }

        // Validate content
        if (content.isEmpty()) {
            showAlert("Erreur de Validation", "La description est requise.", Alert.AlertType.WARNING);
            contentField.requestFocus();
            return false;
        }

        if (content.length() < 10) {
            showAlert("Erreur de Validation", "La description doit contenir au moins 10 caractères.", Alert.AlertType.WARNING);
            contentField.requestFocus();
            return false;
        }

        if (content.length() > 1000) {
            showAlert("Erreur de Validation", "La description ne doit pas dépasser 1000 caractères.", Alert.AlertType.WARNING);
            contentField.requestFocus();
            return false;
        }

        // Validate image
        if (imageView.getImage() == null) {
            showAlert("Erreur de Validation", "Une image est requise pour le post.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une Image");

        // Filter image files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Open dialog
        File file = fileChooser.showOpenDialog(uploadImageButton.getScene().getWindow());

        if (file != null) {
            try {
                String imagePath = file.getAbsolutePath();
                Image image = new Image("file:" + imagePath);

                // Validate image size (optional)
                if (image.getWidth() > 2000 || image.getHeight() > 2000) {
                    showAlert("Erreur de Validation", "L'image est trop grande. Taille maximale : 2000x2000 pixels.", Alert.AlertType.WARNING);
                    return;
                }

                imageView.setImage(image);

                // Update the model
                if (currentPost != null) {
                    currentPost.setImage(imagePath);
                }

                System.out.println("Image sélectionnée: " + imagePath);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors du chargement de l'image: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            System.out.println("Aucune image sélectionnée.");
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}