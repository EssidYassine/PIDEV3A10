package event.aura.Eventus.Services;


import event.aura.Eventus.Models.User;
import event.aura.Eventus.Interfaces.CrudInterface;
import event.aura.Eventus.Tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements CrudInterface<User> {
    private static Connection connection;

    public UserService() {
        connection = MyDataBase.getMyDataBase().getConnection();
    }

    @Override
    public void add(User user) throws SQLException {
        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());

        int rowsInserted = ps.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("✅ User added successfully!");
        } else {
            System.out.println("❌ No user added.");
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String query = "UPDATE users SET username=?, email=?, password=? WHERE id_user=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setInt(4, user.getId_user());

        ps.executeUpdate();
    }

    @Override
    public void delete(User user) throws SQLException {
        String query = "DELETE FROM users WHERE id_user=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, user.getId_user());
        ps.executeUpdate();
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            User user = new User(
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password")
            );
            users.add(user);
        }
        return users;
    }

    @Override
    public User getById(int id) throws SQLException {
        String query = "SELECT * FROM users WHERE id_user = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, id);  // Remplacement du paramètre `?` par la valeur `id`

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) { // Vérifie si un utilisateur est trouvé
            return new User(
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password")
            );
        }

        return null; // Retourne null si aucun utilisateur n'est trouvé
    }
}


