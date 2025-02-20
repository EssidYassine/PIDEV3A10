package event.aura.Eventus.Services;

import event.aura.Eventus.Models.Comment;
import event.aura.Eventus.Models.User;
import event.aura.Eventus.Interfaces.CrudInterface;
import event.aura.Eventus.Tools.MyDataBase;
import jdk.jfr.Description;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentService implements CrudInterface<Comment> {
    private final Connection connection;
    private final UserService userService;

    public CommentService() {
        connection = MyDataBase.getMyDataBase().getConnection();
        userService = new UserService();
    }

    @Override
    public void add(Comment comment) throws SQLException {
        String query = "INSERT INTO comments (id_post, id_user, content) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, comment.getId_post());
            ps.setInt(2, comment.getUser().getId_user());
            ps.setString(3, comment.getContent());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    comment.setId_comment(generatedKeys.getInt(1));
                }
                System.out.println("✅ Comment added successfully!");
            } else {
                System.out.println("❌ No comment added.");
            }
        }
    }

    @Override
    public void update(Comment comment) throws SQLException {
        String query = "UPDATE comments SET content=? WHERE id_comment=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, comment.getContent());
            ps.setInt(2, comment.getId_comment());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Comment updated successfully!");
            } else {
                System.out.println("❌ No comment updated.");
            }
        }
    }

    @Override
    public void delete(Comment comment) throws SQLException {

    }


    public void delete(int id) throws SQLException {
        String query = "DELETE FROM comments WHERE id_comment=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Comment deleted successfully!");
            } else {
                System.out.println("❌ No comment deleted.");
            }
        }
    }

    @Override
    public List<Comment> getAll() throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT * FROM comments";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                comments.add(mapResultSetToComment(rs));
            }
        }
        return comments;
    }

    @Override
    public Comment getById(int id) throws SQLException {
        String query = "SELECT * FROM comments WHERE id_comment=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComment(rs);
                }
            }
        }
        return null; // Return null if not found
    }

    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        int id_comment = rs.getInt("id_comment");
        int id_post = rs.getInt("id_post");
        int id_user = rs.getInt("id_user");

        String content = rs.getString("content");
        UserService userService = new UserService();
        User user = userService.getById(id_user); // Assuming UserService has a getById method
        return new Comment(id_comment, id_post, user, content);
    }

    public List<Comment> getCommentperPost (int post_id) throws SQLException {
        String sql = "SELECT * FROM comments WHERE id_post=?";
        List<Comment> comments = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, post_id);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                comments.add(mapResultSetToComment(rs));
            }
        }
        return comments;
    }
    public void deleteCommentsByPostId(int postId) throws SQLException {
        String query = "DELETE FROM comments WHERE id_post=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, postId);
        ps.executeUpdate();
        System.out.println("🗑️ All comments for post ID " + postId + " deleted.");
    }

}
