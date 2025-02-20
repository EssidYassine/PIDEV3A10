package Services;

import Interfaces.IService;
import Models.Pack;
import Tools.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceGP implements IService<Pack> {
    DataBaseConnection cnx = new DataBaseConnection();



    @Override
    public void ajouter(Pack p) {

    }
    // Exemple plus complet pour insérer également les services liés
    public void ajouter(Pack p, List<Integer> servicesIds) {
        String insertPackQuery = "INSERT INTO packevenement (nom, type, description, prix, nbre_invites_max, "
                + "budget_prevu, date_evenement, lieu, statut) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = cnx.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertPackQuery, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getType());
            ps.setString(3, p.getDescription());
            ps.setBigDecimal(4, p.getPrix());
            ps.setInt(5, p.getNbreInvitesMax());
            ps.setBigDecimal(6, p.getBudgetPrevu());
            ps.setDate(7, Date.valueOf(p.getDateEvenement()));  // Conversion LocalDate -> Date SQL
            ps.setString(8, p.getLieu());
            ps.setString(9, "actif"); // statut par défaut ou p.getStatut()

            // Exécution de l'INSERT dans la table pack
            ps.executeUpdate();

            // Récupération de l'ID auto-généré (pack_id)
            int generatedPackId = 0;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedPackId = rs.getInt(1);
                }
            }

            // Insertion dans la table packservice
            //if (!servicesIds.isEmpty()) {
            //    String insertPackServiceQuery = "INSERT INTO packservice (pack_id, services_id) VALUES (?, ?)";
            //    try (PreparedStatement psService = connection.prepareStatement(insertPackServiceQuery)) {
             //       for (Integer serviceId : servicesIds) {
              //          psService.setInt(1, generatedPackId);
              //          psService.setInt(2, serviceId);
              //          psService.addBatch();
               //     }
                //    psService.executeBatch();
               // }
           // }

            System.out.println("Pack inséré avec succès avec ID = " + generatedPackId);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Pack p) {

    }


    @Override
    public void supprimer(Pack pack) {
        String deleteQuery = "DELETE FROM packevenement WHERE id = ?"; // Suppression par ID

        try (Connection connection = cnx.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {

            pstmt.setInt(1, pack.getId()); // Suppression basée sur l'ID

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Pack supprimé avec succès (ID: " + pack.getId() + ")");
            } else {
                System.out.println("Aucun pack trouvé avec l'ID : " + pack.getId());
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du pack : " + e.getMessage());
            e.printStackTrace(); // Ajouter un printStackTrace pour plus de détails
        }
    }



    @Override
    public Pack getOneById(int id) {
        return null;
    }

    @Override
    public List<Pack> getAll() {
        List<Pack> packs = new ArrayList<>();
        String query = "SELECT * FROM packevenement"; // Assurez-vous que la table s'appelle bien "packevenement"

        try (Connection conn = cnx.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Pack pack = new Pack(
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getBigDecimal("prix"),
                        rs.getInt("nbre_invites_max"),
                        rs.getBigDecimal("budget_prevu"),
                        rs.getDate("date_evenement").toLocalDate(),
                        rs.getString("lieu"),
                        rs.getString("statut")
                );
                packs.add(pack);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return packs;
    }


}
