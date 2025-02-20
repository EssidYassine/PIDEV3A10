package event.aura.Eventus.Services;



import event.aura.Eventus.Models.React;
import event.aura.Eventus.Models.User;
import event.aura.Eventus.Interfaces.CrudInterface;
import event.aura.Eventus.Tools.MyDataBase;

import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReactService implements CrudInterface<React> {
    private static Connection connection = null;
    private final UserService userService;

    public ReactService() {
        connection = MyDataBase.getMyDataBase().getConnection();
        userService = new UserService();
    }

    public static void delete(Integer key) {
    }

    @Override
    public void add(React react) throws SQLException {
        String query = "INSERT INTO reacts (id_post, id_user, reaction) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, react.getId_post());
        ps.setInt(2, react.getUser().getId_user());
        ps.setString(3, react.getReaction());

        int rowsInserted = ps.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("✅ React added successfully!");
        } else {
            System.out.println("❌ No react added.");
        }
    }

    @Override
    public void update(React react) throws SQLException {
        String query = "UPDATE reacts SET reaction=? WHERE id_react=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, react.getReaction());
        ps.setInt(2, react.getId_react());

        ps.executeUpdate();
    }public Map.Entry<Integer,String > getUserReactionByArticle(int idArticle, int idUser) throws SQLException {
        String query = "SELECT id_react, reaction FROM reacts WHERE id_post=? AND id_user=? LIMIT 1";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, idArticle);
        ps.setInt(2, idUser);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new AbstractMap.SimpleEntry<>(rs.getInt("id_react"),rs.getString("reaction"));
        }
        return null;
    }

    public void delete(React react) throws SQLException {
        String query = "DELETE FROM reacts WHERE id_react=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, react.getId_react());
        ps.executeUpdate();
    }

    @Override
    public List<React> getAll() throws SQLException {
        List<React> reacts = new ArrayList<>();
        String query = "SELECT * FROM reacts";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            User user = userService.getById(rs.getInt("id_user"));
            React react = new React(
                    rs.getInt("id_react"),
                    rs.getInt("id_post"),
                    user,
                    rs.getString("reaction")
            );
            reacts.add(react);
        }
        return reacts;
    }

    @Override
    public React getById(int id) throws SQLException {
        String query = "SELECT * FROM reacts WHERE id_react=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            User user = userService.getById(rs.getInt("id_user"));
            return new React(
                    rs.getInt("id_react"),
                    rs.getInt("id_post"),
                    user,
                    rs.getString("reaction")
            );
        }
        return null;
    }
    public void deleteReactsByPostId(int postId) throws SQLException {
        String query = "DELETE FROM reacts WHERE id_post=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, postId);
        ps.executeUpdate();
        System.out.println("🗑️ All reactions for post ID " + postId + " deleted.");
    }

}
