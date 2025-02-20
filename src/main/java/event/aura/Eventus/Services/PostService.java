package event.aura.Eventus.Services;



import event.aura.Eventus.Interfaces.CrudInterface;
import event.aura.Eventus.Models.Post;
import event.aura.Eventus.Models.User;
import event.aura.Eventus.Tools.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostService implements CrudInterface<Post> {
    private final Connection connection;
    private final UserService userService; // Service to fetch User details

    public PostService() {
        connection = MyDataBase.getMyDataBase().getConnection();
        userService = new UserService(); // Initialize UserService
    }

    @Override
    public void add(Post Post) throws SQLException {
        String query = "INSERT INTO posts (title, description, image, nb_Likes, date, id_user) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, Post.getTitle());
        ps.setString(2, Post.getDescription());
        ps.setString(3, Post.getImage());
        ps.setInt(4, Post.getNb_Likes());
        ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now())); // Insert current timestamp
  ps.setInt(6, Post.getId_user().getId());


        System.out.println("🔍 Inserting post: " +Post);

        int rowsInserted = ps.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("✅ Post added successfully!");
        } else {
            System.out.println("❌ No rows inserted!");
        }
    }

    @Override
    public void update(Post Post) throws SQLException {
        String query = "UPDATE posts SET title=?, description=?, image=?, nb_Likes=?, id_user=? WHERE id_post=?";
        PreparedStatement ps = connection.prepareStatement(query);

        ps.setString(1, Post.getTitle());
        ps.setString(2, Post.getDescription());
        ps.setString(3, Post.getImage());
        ps.setInt(4, Post.getNb_Likes());
        ps.setInt(5, Post.getId_user().getId());
        ps.setInt(6,Post.getId_post());



        int rowsUpdated = ps.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("✅ Post updated successfully!");
        } else {
            System.out.println("❌ No Post found with ID: " + Post.getId_post());
        }
    }

    @Override
    public void delete(Post Post) throws SQLException {
        String query = "DELETE FROM posts WHERE id_post=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, Post.getId_post());

        int rowsDeleted = ps.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("✅ Post deleted successfully.");
        } else {
            System.out.println("❌ No Post found with ID: " + Post.getId_post());
        }

        // Reset AUTO_INCREMENT if table is empty
        String countQuery = "SELECT COUNT(*) FROM posts";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(countQuery);

        if (rs.next() && rs.getInt(1) == 0) {
            String resetAutoIncrementQuery = "ALTER TABLE posts AUTO_INCREMENT = 1;";
            stmt.executeUpdate(resetAutoIncrementQuery);
            System.out.println("🔄 AUTO_INCREMENT reset for Post table.");
        }
    }

    @Override
    public List<Post> getAll() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM posts";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            User user = userService.getById(rs.getInt("id_user")); // Fetch associated User
            Post Post = new Post(

                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("image"),
                    rs.getInt("nb_Likes"),
                    rs.getTimestamp("date").toLocalDateTime(),
                    user
            );
            Post.setId_post(rs.getInt("id_post")); // Assign the Post ID
            posts.add(Post);
        }
        return posts;
    }

    @Override
    public Post getById(int id) throws SQLException {
        String query = "SELECT * FROM posts WHERE id_post=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            User user = userService.getById(rs.getInt("id_user")); // Fetch associated User
            Post Post = new Post(

                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("image"),
                    rs.getInt("nb_Likes"),
                    rs.getTimestamp("date").toLocalDateTime(),
                    user
            );
            Post.setId_post(rs.getInt("id_post")); // Assign the Post ID
            return Post;
        }
        return null;
    }
}
