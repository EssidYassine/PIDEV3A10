package Services;

import Models.User;
import Interfaces.IService;
import Tools.DataBaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {
    private static Connection connection;

    public UserService() {
        connection = DataBaseConnection.getMyDataBase().getConnection();
    }

    @Override
    public void add(User user) throws SQLException {
        String query = "INSERT INTO user (username, email, password, role, is_active, num_tel, date_de_naissance) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);

        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setString(4, user.getRole());
        ps.setBoolean(5, user.isActive());
        ps.setString(6, user.getNumTel());

        // Si dateDeNaissance est null, on met NULL en base
        if (user.getDateDeNaissance() != null) {
            ps.setDate(7, Date.valueOf(user.getDateDeNaissance()));
        } else {
            ps.setNull(7, Types.DATE);
        }

        int rowsInserted = ps.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Utilisateur ajouté avec succès !");
        } else {
            System.out.println("Aucun utilisateur ajouté.");
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String query = "UPDATE user "
                + "SET username = ?, email = ?, password = ?, role = ?, is_active = ?, "
                + "    num_tel = ?, date_de_naissance = ? "
                + "WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);

        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setString(4, user.getRole());
        ps.setBoolean(5, user.isActive());
        ps.setString(6, user.getNumTel());

        if (user.getDateDeNaissance() != null) {
            ps.setDate(7, Date.valueOf(user.getDateDeNaissance()));
        } else {
            ps.setNull(7, Types.DATE);
        }

        ps.setInt(8, user.getId()); // La colonne PK s'appelle désormais "id"

        ps.executeUpdate();
    }

    @Override
    public void delete(User user) throws SQLException {
        String query = "DELETE FROM user WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, user.getId());
        ps.executeUpdate();
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            // Récupération des valeurs
            int id = rs.getInt("id");
            String username = rs.getString("username");
            String email = rs.getString("email");
            String password = rs.getString("password");
            String role = rs.getString("role");
            boolean isActive = rs.getBoolean("is_active");
            String numTel = rs.getString("num_tel");

            Date sqlDate = rs.getDate("date_de_naissance");
            LocalDate dateDeNaissance = (sqlDate != null) ? sqlDate.toLocalDate() : null;

            // Construction de l'objet User
            User user = new User(id, username, email, password, role, isActive, numTel, dateDeNaissance);
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
            String username = rs.getString("username");
            String email = rs.getString("email");
            String password = rs.getString("password");
            String role = rs.getString("role");
            boolean isActive = rs.getBoolean("is_active");
            String numTel = rs.getString("num_tel");

            Date sqlDate = rs.getDate("date_de_naissance");
            LocalDate dateDeNaissance = (sqlDate != null) ? sqlDate.toLocalDate() : null;

            return new User(id, username, email, password, role, isActive, numTel, dateDeNaissance);
        }
        return null;
    }
}
