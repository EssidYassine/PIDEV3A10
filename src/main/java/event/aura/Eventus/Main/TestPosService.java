package event.aura.Eventus.Main;



import event.aura.Eventus.Models.Post;
import event.aura.Eventus.Models.User;
import event.aura.Eventus.Services.PostService;
import event.aura.Eventus.Services.UserService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TestPosService {
    public static void main(String[] args) {
        try {
            PostService postService = new PostService();
            UserService userService = new UserService();

            // ✅ Step 1: Create a test user (Ensure that a user exists before adding a post)
            User testUser = userService.getById(1); // Change ID based on an existing user
            if (testUser == null) {
                System.out.println("❌ No user found! Please add a user before testing.");
                return;
            }
            int id = testUser.getId_user();
            System.out.println("user" + testUser.getId());

            // ✅ Step 2: Create a new Post

            Post newPost = new Post("Test Title", "Test Description", "file:/C:/Users/MARIEM/Downloads/Salle.jpeg"
,0,LocalDateTime.now(), userService.getById(1));
            System.out.println("err id user");
            postService.add(newPost);
            System.out.println("✅ Post added successfully with ID: " + newPost.getId_post());

            // ✅ Step 3: Retrieve all posts and print them
            List<Post> posts = postService.getAll();
            System.out.println("\n📜 All Posts:");
            for (Post post : posts) {
                System.out.println(post);
            }

            // ✅ Step 4: Update the post
            newPost.setTitle("Updated Test Title");
            newPost.setDescription("Updated Test Description");

            postService.update(newPost);
            System.out.println("✅ Post updated successfully!");

            // ✅ Step 5: Retrieve and print the updated post
            Post updatedPost = postService.getById(15);
            if (updatedPost != null) {
                System.out.println("\n🔄 Updated Post: " + updatedPost);
            } else {
                System.out.println("❌ Failed to retrieve the updated post.");
            }

            // ✅ Step 6: Delete the post
            postService.delete(newPost);
            System.out.println("✅ Post deleted successfully!");

            // ✅ Step 7: Verify deletion
            Post deletedPost = postService.getById(newPost.getId_post());
            if (deletedPost == null) {
                System.out.println("✅ Deletion confirmed. The post no longer exists.");
            } else {
                System.out.println("❌ Post still exists! Deletion failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

