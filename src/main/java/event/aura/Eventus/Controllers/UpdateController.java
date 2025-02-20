package event.aura.Eventus.Controllers;

import event.aura.Eventus.Models.Post;
import event.aura.Eventus.Services.PostService;
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
        try {
            // Validate required fields
            if (titleField.getText().isEmpty() || contentField.getText().isEmpty()) {
                showAlert("Error", "All fields must be filled.", Alert.AlertType.WARNING);
                return;
            }

            // Update the Post object
            currentPost.setTitle(titleField.getText());
            currentPost.setDescription(contentField.getText());

            // Update post in the database
            postService.update(currentPost);

            showAlert("Success", "Post updated successfully.", Alert.AlertType.INFORMATION);

            // Close the window
            ((Stage) updateButton.getScene().getWindow()).close();

        } catch (SQLException e) {
            showAlert("Error", "Failed to update the post: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "Please check your inputs.", Alert.AlertType.WARNING);
            e.printStackTrace();
        }
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image");

        // Filter image files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Open dialog
        File file = fileChooser.showOpenDialog(uploadImageButton.getScene().getWindow());

        if (file != null) {
            String imagePath = file.getAbsolutePath();
            Image image = new Image("file:" + imagePath);
            imageView.setImage(image);

            // Update the model
            if (currentPost != null) {
                currentPost.setImage(imagePath);
            }

            System.out.println("Selected image: " + imagePath);
        } else {
            System.out.println("No image selected.");
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


