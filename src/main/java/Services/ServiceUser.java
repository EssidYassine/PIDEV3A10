package Services;

import Interfaces.IService;
import Models.User;
import Tools.DataBaseConnection;
import javafx.application.Platform;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ServiceUser implements IService<User> {

    Connection cnx = DataBaseConnection.getDatabaseConnection().getConnection();

    public String findUserByEmailAndPassword(String email, String password) {
        Connection cnx = null; // Initialize cnx outside the try block
        try {
            cnx = DataBaseConnection.getDatabaseConnection().getConnection(); // Get connection

            if (cnx == null) {  // Check if connection is null
                return null; // or throw an exception
            }

            try (PreparedStatement pstmt = cnx.prepareStatement("SELECT * FROM user WHERE email = ?")) {

                pstmt.setString(1, email);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String hashedPassword = rs.getString("password");
                        if (BCrypt.checkpw(password, hashedPassword)) {
                            return rs.getString("role");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cnx != null) {
                try {
                    cnx.close(); // Close the connection in the finally block
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public User findUserByEmailAndPassword2(String email, String password) {
        try (PreparedStatement pstmt = cnx.prepareStatement("SELECT * FROM user WHERE email = ?")) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setDateDeNaissance(rs.getDate("date_de_naissance"));
                        user.setIsActive(rs.getString("is_active"));
                        user.setNumTel(rs.getInt("num_tel"));
                        user.setEmail(rs.getString("email"));
                        user.setRole(rs.getString("role"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
        return null;
    }
    @Override
    public void ajouter(User p) {
        // Vérification de l'email
        if (!p.getEmail().contains("@")) {
            System.out.println("Erreur : L'email doit contenir un '@'.");
            return;
        }

        String sql = "INSERT INTO user (username, email, password, role, is_active, num_tel, date_de_naissance) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setString(1, p.getUsername());
            pstmt.setString(2, p.getEmail());
            pstmt.setString(3, p.getPassword());
            pstmt.setString(4, p.getRole());
            pstmt.setString(5, p.getIsActive());

            // Gestion du numéro de téléphone et de la date de naissance
            if (p.getNumTel() != 0) {  // Vérifie si numTel est défini
                pstmt.setInt(6, p.getNumTel());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }


            if (p.getDateDeNaissance() != null) {
                pstmt.setDate(7, new java.sql.Date(p.getDateDeNaissance().getTime())); // Conversion correcte
            } else {
                pstmt.setNull(7, java.sql.Types.DATE);
            }

            pstmt.executeUpdate();
            System.out.println("Utilisateur ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public void modifier(User p) {

    }

    public void supprimer(int id) {
        String sql = "DELETE FROM user WHERE id = ?"; // Requête SQL pour la suppression

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, id); // Paramètre pour l'ID

            int rowsAffected = pstmt.executeUpdate(); // Exécution de la requête

            if (rowsAffected > 0) {
                System.out.println("Utilisateur supprimé avec succès !");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }
    @Override
    public User getOneById(int id) {
        return null;
    }

    @Override
    public Set<User> getAll() {
        Set<User> users = new HashSet<>();
        String sql = "SELECT id, username, email, password, role, is_active, num_tel, date_de_naissance FROM user";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),  // Ajout de l'ID
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("is_active"),
                        rs.getInt("num_tel"),
                        rs.getDate("date_de_naissance")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }
        return users;
    }


}
