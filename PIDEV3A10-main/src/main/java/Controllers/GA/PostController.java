package Controllers.GA;
import javafx.scene.shape.Rectangle;
import Models.Post;
import Models.Comment;
import Models.React;
import Models.User;
import Services.PostService;
import Services.CommentService;
import Services.ReactService;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class PostController {

    @FXML
    private ListView<Post> postListView;

    private PostService postService;
    private CommentService commentService;
    private ReactService reactService;

    public void initialize() {
        postService = new PostService();
        commentService = new CommentService();
        reactService = new ReactService();

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
                    HBox postBox = createPostBox(post);
                    setGraphic(postBox);
                }
            }
        });

        postListView.getItems().setAll(posts);
    }

    private HBox createPostBox(Post post) {
        HBox postBox = new HBox(15);
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

        // Likes Section
        ImageView heartIcon = new ImageView(new Image(getClass().getResourceAsStream("/Images/heart-empty.png")));
        heartIcon.setFitWidth(20);
        heartIcon.setFitHeight(20);

// Check if the user has already liked the post
        boolean isLiked = false;
        try {
            Map.Entry<Integer, String> entry = reactService.getUserReactionByArticle(post.getId_post(), 1);
            isLiked = (entry != null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

// If already liked, set the heart icon to red
        if (isLiked) {
            heartIcon.setImage(new Image(getClass().getResourceAsStream("/Images/heart-filled.png")));
        }

// Make the heart clickable
        heartIcon.setOnMouseClicked(event -> handleLikeButtonClick(post, heartIcon));

        HBox likesBox = new HBox(5, heartIcon);
        likesBox.setAlignment(Pos.CENTER_LEFT);


        // Comments Section
        VBox commentsContent = new VBox(5);
        commentsContent.setVisible(false);
        commentsContent.setManaged(false);
        commentsContent.getStyleClass().add("comments-content");

        VBox commentsList = new VBox(5);
        commentsList.getStyleClass().add("comments-list");

        try {
            CommentService commentService = new CommentService();
            List<Comment> comments = commentService.getCommentperPost(post.getId_post());
            for (Comment comment : comments) {
                Label commentLabel = new Label(comment.getUser().getUsername() + ": " + comment.getContent());
                commentLabel.getStyleClass().add("comment-label");
                commentsList.getChildren().add(commentLabel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Comment Input Section
        TextField commentInput = new TextField();
        commentInput.setPromptText("Write a comment...");
        commentInput.getStyleClass().add("comment-input");

        Button submitCommentButton = new Button("Post");
        submitCommentButton.getStyleClass().add("submit-comment-button");
        submitCommentButton.setOnAction(event -> {
            handleComment(post, commentInput.getText(), commentsList);
            commentInput.clear();
        });

        HBox commentInputBox = new HBox(10, commentInput, submitCommentButton);
        commentInputBox.setAlignment(Pos.CENTER_LEFT);

        commentsContent.getChildren().addAll(commentsList, commentInputBox);

        // Comments Header with Toggle
        HBox commentsHeader = new HBox(10);
        commentsHeader.setAlignment(Pos.CENTER_LEFT);

        Label commentsIcon = new Label("💬");
        commentsIcon.getStyleClass().add("icon");

        Label commentsCount = new Label(String.valueOf(commentsList.getChildren().size()));
        commentsCount.getStyleClass().addAll("post-meta", "comments-count");

        Button toggleButton = new Button("Show Comments ▼");
        toggleButton.getStyleClass().add("toggle-button");

        commentsHeader.getChildren().addAll(commentsIcon, commentsCount, toggleButton);

        // Toggle button action with animation
        toggleButton.setOnAction(event -> {
            boolean expanding = !commentsContent.isVisible();
            if (expanding) {
                commentsContent.setVisible(true);
                commentsContent.setManaged(true);
                toggleButton.setText("Hide Comments ▲");

                Rectangle clip = new Rectangle((int) commentsContent.getBoundsInLocal().getWidth(), 0);
                commentsContent.setClip(clip);

                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(clip.heightProperty(), 0)),
                        new KeyFrame(Duration.millis(300), new KeyValue(clip.heightProperty(), commentsList.getHeight() + 50))
                );
                timeline.setOnFinished(e -> commentsContent.setClip(null));
                timeline.play();
            } else {
                Rectangle clip = new Rectangle(commentsContent.getBoundsInLocal().getWidth(), commentsContent.getBoundsInLocal().getHeight());
                commentsContent.setClip(clip);

                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(clip.heightProperty(), commentsContent.getHeight())),
                        new KeyFrame(Duration.millis(300), new KeyValue(clip.heightProperty(), 0))
                );
                timeline.setOnFinished(e -> {
                    commentsContent.setVisible(false);
                    commentsContent.setManaged(false);
                    commentsContent.setClip(null);
                });
                toggleButton.setText("Show Comments ▼");
                timeline.play();
            }
        });

        // Post Details Container
        VBox postDetails = new VBox(5, titleLabel, postImageView, contentLabel, likesBox, commentsHeader, commentsContent);
        postBox.getChildren().add(postDetails);

        return postBox;
    }



    // Focuses on fixing the comment handling methods
    private void handleLikeButtonClick(Post post, ImageView heartIcon) {
        User user = new User();
        user.setId_user(1); // Get the actual logged-in user

        if (user == null) {
            System.out.println("❌ No user logged in.");
            return;
        }

        ReactService reactService = new ReactService();
        Map.Entry<Integer, String> entry = null;

        try {
            entry = reactService.getUserReactionByArticle(post.getId_post(), user.getId_user());
        } catch (SQLException e) {
            throw new RuntimeException("❌ Error checking user reaction", e);
        }

        if (entry == null) {
            // Add a like
            React react = new React(post.getId_post(), user, "like");
            try {
                reactService.add(react);
                heartIcon.setImage(new Image(getClass().getResourceAsStream("/Images/heart-filled.png")));
                System.out.println("❤️ Liked post: " + post.getTitle());
            } catch (SQLException e) {
                throw new RuntimeException("❌ Error adding like", e);
            }
        } else {
            // Remove the like
            try {
                React reactToDelete = new React(entry.getKey(), post.getId_post(), user, entry.getValue());
                reactService.delete(reactToDelete);
                heartIcon.setImage(new Image(getClass().getResourceAsStream("/Images/heart-empty.png")));
                System.out.println("💔 Unliked post: " + post.getTitle());
            } catch (SQLException e) {
                throw new RuntimeException("❌ Error removing like", e);
            }
        }
    }



    private void handleComment(Post post, String content, VBox commentsBox) {
        User user = new User();
        user.setId_user(1);
        if (content != null && !content.isEmpty()) {
            try {
                Comment comment = new Comment(post.getId_post(), user, content);
                commentService.add(comment);
                System.out.println("Comment added to post: " + post.getTitle());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
