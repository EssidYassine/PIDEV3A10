package event.aura.Eventus.Controllers;

import event.aura.Eventus.Models.Post;
import event.aura.Eventus.Models.Comment;
import event.aura.Eventus.Models.User;
import event.aura.Eventus.Services.PostService;
import event.aura.Eventus.Services.CommentService;
import event.aura.Eventus.Services.ReactService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AvailableController {

    @FXML
    private ListView<Post> postListView;

    private PostService postService;
    private CommentService commentService;

    public void initialize() {
        postService = new PostService();
        commentService = new CommentService();

        try {
            loadPosts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPosts() throws SQLException {
        List<Post> posts = postService.getAll();

        postListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);
                if (empty || post == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox postBox = createPostBox(post);
                    setGraphic(postBox);
                }
            }
        });

        postListView.getItems().setAll(posts);
    }

    private VBox createPostBox(Post post) {
        VBox postBox = new VBox(10);
        postBox.setPadding(new Insets(10));
        postBox.getStyleClass().add("post-box");

        // Title
        Label titleLabel = new Label(post.getTitle());
        titleLabel.getStyleClass().add("post-title");

        // Post Image (if available)
        ImageView postImageView = new ImageView();
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            File imageFile = new File(post.getImage());
            if (imageFile.exists()) {
                Image postImage = new Image(imageFile.toURI().toString(), 100, 100, true, true);
                postImageView.setImage(postImage);
                postImageView.setFitWidth(100);
                postImageView.setPreserveRatio(true);
            }
        }

        // Content Preview
        Label contentLabel = new Label(post.getDescription().length() > 100 ?
                post.getDescription().substring(0, 100) + "..." : post.getDescription());
        contentLabel.getStyleClass().add("post-content");
        contentLabel.setWrapText(true);

        // Comments Section (Always Visible)
        VBox commentsContent = new VBox(5);
        commentsContent.getStyleClass().add("comments-content");

        VBox commentsList = new VBox(5);
        commentsList.getStyleClass().add("comments-list");

        try {
            List<Comment> comments = commentService.getCommentperPost(post.getId_post());
            for (Comment comment : comments) {
                Label commentLabel = new Label(comment.getUser().getUsername() + ": " + comment.getContent());
                commentLabel.getStyleClass().add("comment-label");
                commentsList.getChildren().add(commentLabel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        commentsContent.getChildren().add(commentsList);

        // Edit and Delete Buttons (Stacked Vertically)
        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(event -> handleEditPost(post));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> handleDeletePost(post));

        VBox buttonContainer = new VBox(5, editButton, deleteButton);
        buttonContainer.setAlignment(Pos.CENTER_LEFT);

        // Post Details Container
        postBox.getChildren().addAll(titleLabel, postImageView, contentLabel, commentsContent, buttonContainer);

        editButton.setOnAction(e -> handleEditPost(post));


        return postBox;

    }

    private void handleDeletePost(Post post) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Post");
        confirmAlert.setHeaderText("Are you sure you want to delete this post?");
        confirmAlert.setContentText("All reactions and comments related to this post will also be deleted. This action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    ReactService reactService = new ReactService(); // Initialize reaction service
                    reactService.deleteReactsByPostId(post.getId_post()); // Delete reactions first
                    commentService.deleteCommentsByPostId(post.getId_post()); // Then delete comments
                    postService.delete(post); // Finally, delete the post
                    postListView.getItems().remove(post); // Remove from UI
                    System.out.println("✅ Post and related reactions/comments deleted: " + post.getTitle());
                } catch (SQLException e) {
                    System.err.println("❌ Error deleting post: " + e.getMessage());
                    showErrorAlert("Error", "Failed to delete post, reactions, and comments.");
                }
            }
        });
    }



    // Helper function to show error alerts
    private void showErrorAlert(String title, String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }


    private void handleEditPost(Post post) {
        try {
            // Load the edit dialog FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMl/edit.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the hebergement
            UpdateController controller = loader.getController();
            controller.setPost(post);

            // Create and configure the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Accommodation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initStyle(StageStyle.DECORATED);
            dialogStage.setResizable(false);

            // Set the scene
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Show the dialog and wait
            dialogStage.showAndWait();

            // Refresh the list after editing
            loadPosts();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to open edit dialog: " + e.getMessage());
            alert.showAndWait();
        }
    }}
