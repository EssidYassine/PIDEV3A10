package Services;

import Interfaces.IService;
import Models.Locaux;
import Models.Pack;
import Models.Service;
import Tools.DataBaseConnection;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceGP implements IService<Pack> {
    DataBaseConnection cnx = new DataBaseConnection();


    @Override
    public void ajouter(Pack pack, List<Integer> serviceIds, int idLocal) {
        String insertPackQuery = "INSERT INTO packevenement (nom, type, description, prix, nbre_invites_max, budget_prevu, date_evenement, lieu, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertPackServiceQuery = "INSERT INTO packservice (pack_id, service_id) VALUES (?, ?)";

        try (Connection conn = cnx.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertPackQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pstmtPackService = conn.prepareStatement(insertPackServiceQuery)) {

            conn.setAutoCommit(false);

            pstmt.setString(1, pack.getNom());
            pstmt.setString(2, pack.getType());
            pstmt.setString(3, pack.getDescription());
            pstmt.setBigDecimal(4, pack.getPrix());
            pstmt.setInt(5, pack.getNbreInvitesMax());
            pstmt.setBigDecimal(6, pack.getBudgetPrevu());
            pstmt.setDate(7, java.sql.Date.valueOf(pack.getDateEvenement()));
            pstmt.setString(8, pack.getLieu());
            pstmt.setString(9, pack.getStatut());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("L'insertion du pack a échoué, aucune ligne affectée.");
            }

            int idPack = -1;
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idPack = rs.getInt(1);
                    System.out.println("Pack inséré avec l'ID : " + idPack);
                } else {
                    throw new SQLException("Échec de la récupération de l'ID généré pour le pack.");
                }
            }

            for (Integer idService : serviceIds) {
                pstmtPackService.setInt(1, idPack);
                pstmtPackService.setInt(2, idService);
                pstmtPackService.executeUpdate();
            }

            conn.commit();
            System.out.println("Pack et services enregistrés avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ajout du pack et des services");
        }
    }

    @Override
    public void modifier(Pack p, List<Integer> serviceIds, int idLocal) {
        String updateQuery = "UPDATE packevenement SET nom = ?, type = ?, description = ?, prix = ?, nbre_invites_max = ?, budget_prevu = ?, date_evenement = ?, lieu = ?, lieu_id = ?, statut = ? WHERE pack_id = ?";
        try (Connection connection = cnx.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                pstmt.setString(1, p.getNom());
                pstmt.setString(2, p.getType());
                pstmt.setString(3, p.getDescription());
                pstmt.setBigDecimal(4, p.getPrix());
                pstmt.setInt(5, p.getNbreInvitesMax());
                pstmt.setBigDecimal(6, p.getBudgetPrevu());
                pstmt.setDate(7, java.sql.Date.valueOf(p.getDateEvenement()));
                pstmt.setString(8, p.getLieu());
                pstmt.setInt(9, idLocal);
                pstmt.setString(10, p.getStatut());
                pstmt.setInt(11, p.getId());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Pack modifié avec succès !");
                } else {
                    System.out.println("Aucun pack trouvé avec l'ID : " + p.getId());
                }
            }

            String deleteServicesQuery = "DELETE FROM packservice WHERE pack_id = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteServicesQuery)) {
                deleteStmt.setInt(1, p.getId());
                deleteStmt.executeUpdate();
            }

            String insertServiceQuery = "INSERT INTO packservice (pack_id, service_id) VALUES (?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertServiceQuery)) {
                for (Integer serviceId : serviceIds) {
                    insertStmt.setInt(1, p.getId());
                    insertStmt.setInt(2, serviceId);
                    insertStmt.executeUpdate();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int recupererIdLocal(String adresse) {
        String query = "SELECT id_local FROM locaux WHERE Adresse = ?";
        try (Connection connection = cnx.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, adresse);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_local");
                } else {
                    System.out.println("Aucun local trouvé pour l'adresse : " + adresse);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }



    public void supprimer(int id) {
        String deletePackServiceQuery = "DELETE FROM packservice WHERE pack_id = ?";
        String deletePackQuery = "DELETE FROM packevenement WHERE pack_id = ?";

        try (Connection connection = cnx.getConnection();
             PreparedStatement psPackService = connection.prepareStatement(deletePackServiceQuery);
             PreparedStatement psPack = connection.prepareStatement(deletePackQuery)) {

            psPackService.setInt(1, id);
            psPackService.executeUpdate();

            psPack.setInt(1, id);
            int rowsAffected = psPack.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Pack avec l'ID '" + id + "' supprimé avec succès.");
            } else {
                System.out.println("Aucun pack trouvé avec l'ID : " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Pack getOneById(int id) {
        return null;
    }

    @Override
    public List<Pack> getAll() {
        List<Pack> packs = new ArrayList<>();
        String query = "SELECT * FROM packevenement";
        try (Connection connection = cnx.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Pack pack = new Pack(
                        rs.getInt("pack_id"),
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
        }
        return packs;
    }

    public List<Locaux> getAllLocaux() {
        List<Locaux> locaux = new ArrayList<>();
        String query = "SELECT id_local, Adresse FROM locaux";

        try (Connection conn = cnx.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_local");
                String adresse = rs.getString("Adresse");
                Locaux local = new Locaux();
                local.setIdLocal(id);
                local.setAdresse(adresse);
                locaux.add(local);
            }
        } catch (SQLException e) {
        }
        return locaux;
    }


    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String query = "SELECT id_service, nom_service FROM service"; // Vérifiez que le nom de la table et des colonnes est correct

        try (Connection conn = cnx.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_service");
                String nom = rs.getString("nom_service");
                Service service = new Service();
                service.setId_service(id);
                service.setNom_service(nom);
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }


    public List<Integer> getServicesByPackId(int idPack) {
        List<Integer> serviceIds = new ArrayList<>();
        String query = "SELECT id_service FROM pack_service WHERE id_pack = ?";

        try (Connection conn = cnx.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idPack);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    serviceIds.add(rs.getInt("id_service"));
                }
            }
        } catch (SQLException e) {
        }
        return serviceIds;
    }
}
