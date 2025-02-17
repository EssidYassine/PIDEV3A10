package Services;

import Interfaces.IService;
import Models.User;
import Tools.DataBaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

public class ServiceUser implements IService<User> {

    Connection cnx = DataBaseConnection.getDatabaseConnection().getConnection();


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

    @Override
    public void supprimer(int id) {

    }

    @Override
    public User getOneById(int id) {
        return null;
    }

    @Override
    public Set<User> getAll() {
        return Set.of();
    }
}
