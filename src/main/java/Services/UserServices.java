package Services;


import Models.User;
import Interfaces.IService;
import Tools.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServices implements IService<User> {
    private static Connection connection;

    public UserServices() {
        connection = DataBaseConnection.getDatabaseConnection().getConnection();
    }

    @Override
    public void add(User user) throws SQLException {
        String query = "INSERT INTO user (username, email, password, role, is_active) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setString(4, user.getRole());
        ps.setString(5, user.getIsActive());

        int rowsInserted = ps.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println(" Utilisateur ajouté avec succès !");
        } else {
            System.out.println("Aucun utilisateur ajouté.");
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String query = "UPDATE user SET username=?, email=?, password=?, role=?, is_active=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setString(4, user.getRole());
        ps.setString(5, user.getIsActive());
        ps.setInt(6, user.getId());

        int rowsUpdated = ps.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println(" Utilisateur mis à jour avec succès !");
        } else {
            System.out.println("Aucun utilisateur mis à jour.");
        }
    }

    @Override
    public void delete(User user) throws SQLException {
        String query = "DELETE FROM user WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, user.getId());

        int rowsDeleted = ps.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("✅ Utilisateur supprimé avec succès !");
        } else {
            System.out.println("❌ Aucun utilisateur supprimé.");
        }
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            User user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("is_active")
            );
            users.add(user);
        }
        return users;
    }

    @Override
    public User getById(int id) throws SQLException {
        String query = "SELECT * FROM user WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("is_active")
            );
        }
        return null;
    }
}

