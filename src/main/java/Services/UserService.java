package Services;

import Models.User;
import Interfaces.IService;
import Tools.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {
    private static Connection connection;

    public UserService() {
        connection = DataBaseConnection.getMyDataBase().getConnection();
    }

    @Override
    public void add(User user) throws SQLException {
        String query = "INSERT INTO user (nom, prenom, email, mot_de_passe) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, user.getNom());
        ps.setString(2, user.getPrenom());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getMot_de_passe());

        int rowsInserted = ps.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println(" Utilisateur ajouté avec succès !");
        } else {
            System.out.println(" Aucun utilisateur ajouté.");
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String query = "UPDATE user SET nom=?, prenom=?, email=?, mot_de_passe=? WHERE id_user=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, user.getNom());
        ps.setString(2, user.getPrenom());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getMot_de_passe());
        ps.setInt(5, user.getId_user());

        ps.executeUpdate();
    }

    @Override
    public void delete(User user) throws SQLException {
        String query = "DELETE FROM user WHERE id_user=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, user.getId_user());
        ps.executeUpdate();
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> user = new ArrayList<>();
        String query = "SELECT * FROM user";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            User users = new User(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe")
            );
            user.add(users);
        }
        return user;
    }

    @Override
    public User getById(int id) throws SQLException {
        String query = "SELECT * FROM user WHERE id_user = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new User(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe")
            );
        }
        return null;
    }
}


