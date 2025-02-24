package Services;

import Models.Reclamation;
import Tools.DataBaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation {

    private final Connection cnx = DataBaseConnection.getDatabaseConnection().getConnection();

    // Méthode pour ajouter une réclamation
    public void ajouter(Reclamation reclamation) {
        String sql = "INSERT INTO reclamation (user_id, user_message, chat_response) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, reclamation.getUserId());
            pstmt.setString(2, reclamation.getUserMessage());
            pstmt.setString(3, reclamation.getChatResponse());

            pstmt.executeUpdate();
            System.out.println("Réclamation ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la réclamation : " + e.getMessage());
        }
    }

    // Méthode pour supprimer une réclamation par ID
    public void supprimer(int id) {
        String sql = "DELETE FROM reclamation WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Réclamation supprimée avec succès !");
            } else {
                System.out.println("Aucune réclamation trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la réclamation : " + e.getMessage());
        }
    }

    // Méthode pour récupérer l'historique des messages d'un utilisateur
    public List<Reclamation> getUserChatHistory(int userId) {
        List<Reclamation> chatHistory = new ArrayList<>();
        String sql = "SELECT user_message, chat_response FROM reclamation WHERE user_id = ? ORDER BY id ASC";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Reclamation rec = new Reclamation();
                rec.setUserMessage(rs.getString("user_message"));
                rec.setChatResponse(rs.getString("chat_response"));
                chatHistory.add(rec);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'historique des messages : " + e.getMessage());
        }

        return chatHistory;
    }
}
