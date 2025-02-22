package Services;



import Models.Service;
import Interfaces.IService;
import Models.User;
import Tools.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceService implements IService<Service> {
    private static Connection connection;

    public ServiceService() {
        connection = DataBaseConnection.getMyDataBase().getConnection();
    }

    @Override

    public void add(Service service) throws SQLException {
        String query = "INSERT INTO service (nom_service, description, prix, type_service, id_utilisateur, image_url, quantite_materiel, role_staff, experience) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, service.getNom_service());
            ps.setString(2, service.getDescription());
            ps.setInt(3, service.getPrix());
            ps.setString(4, service.getType_service().name());
            ps.setInt(5, service.getUtilisateur().getId_utilisateur()); // Utilisation de l'objet Utilisateur
            ps.setString(6, service.getImage_url());
            ps.setInt(7, service.getQuantite_materiel());
            ps.setString(8, service.getRole_staff());
            ps.setString(9, service.getExperience());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Service ajouté avec succès !");
            } else {
                System.out.println("❌ Aucun service ajouté.");
            }
        }

        // Mettre automatiquement disponibilité à 1 après l'ajout
        try (PreparedStatement psUpdate = connection.prepareStatement("UPDATE service SET disponibilite = 1 WHERE id_service = (SELECT MAX(id_service) FROM service)")) {
            psUpdate.executeUpdate();
        }
    }

    @Override
    public void update(Service service) throws SQLException {
        String query = "UPDATE service SET nom_service=?, description=?, prix=?, type_service=?, disponibilite=?, id_utilisateur=?, image_url=?, quantite_materiel=?, role_staff=?, experience=? WHERE id_service=?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, service.getNom_service());
            ps.setString(2, service.getDescription());
            ps.setInt(3, service.getPrix());
            ps.setString(4, service.getType_service().name());
            ps.setInt(5, service.getDisponibilite());
            ps.setInt(6, service.getUtilisateur().getId_utilisateur()); // Utilisation de l'objet Utilisateur
            ps.setString(7, service.getImage_url());
            ps.setInt(8, service.getQuantite_materiel());
            ps.setString(9, service.getRole_staff());
            ps.setString(10, service.getExperience());
            ps.setInt(11, service.getId_service());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Service mis à jour avec succès !");
            } else {
                System.out.println("❌ Aucune mise à jour effectuée.");
            }
        }
    }

    @Override
    public void delete(Service service) throws SQLException {
        String query = "DELETE FROM service WHERE id_service=?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, service.getId_service());
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println(" Service supprimé avec succès !");
            } else {
                System.out.println("Aucun service supprimé.");
            }
        }
    }

    @Override
    public List<Service> getAll() throws SQLException {
        List<Service> services = new ArrayList<>();
        String query = "SELECT s.*, u.nom, u.prenom, u.email FROM service s " +
                "JOIN user u ON s.id_utilisateur = u.id_user";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Création de l'objet Utilisateur
                User utilisateur = new User(
                        rs.getInt("id_utilisateur"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email")
                );

                // Création de l'objet Service avec l'objet Utilisateur
                Service service = new Service(
                        rs.getInt("id_service"),
                        rs.getString("nom_service"),
                        rs.getString("description"),
                        rs.getInt("prix"),
                        Service.TypeService.valueOf(rs.getString("type_service")),
                        rs.getInt("disponibilite"),
                        utilisateur, // Utilisation de l'objet Utilisateur
                        rs.getString("image_url"),
                        rs.getInt("quantite_materiel"),
                        rs.getString("role_staff"),
                        rs.getString("experience")
                );
                services.add(service);
            }
        }
        return services;
    }
    public Service getById(int id) throws SQLException {
        String query = "SELECT s.*, u.nom, u.prenom, u.email FROM service s " +
                "JOIN user u ON s.id_utilisateur = u.id_user " +
                "WHERE s.id_service = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Création de l'objet Utilisateur
                User utilisateur = new User(
                        rs.getInt("id_utilisateur"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email")
                );

                // Création de l'objet Service avec l'objet Utilisateur
                return new Service(
                        rs.getInt("id_service"),
                        rs.getString("nom_service"),
                        rs.getString("description"),
                        rs.getInt("prix"),
                        Service.TypeService.valueOf(rs.getString("type_service")),
                        rs.getInt("disponibilite"),
                        utilisateur, // Utilisation de l'objet Utilisateur
                        rs.getString("image_url"),
                        rs.getInt("quantite_materiel"),
                        rs.getString("role_staff"),
                        rs.getString("experience")
                );
            }
        }
        return null;
    }
    public void updateQuantite(int idService, int nouvelleQuantite) throws SQLException {
        String query = "UPDATE service SET quantite_materiel = ? WHERE id_service = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, nouvelleQuantite);
            ps.setInt(2, idService);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Quantité mise à jour pour le service " + idService);
            } else {
                System.out.println("Aucune mise à jour effectuée pour le service " + idService);
            }
        }
    }
    public static void updateQuantiteAndAvailability(int idService, int nouvelleQuantite) throws SQLException {
        // Si la nouvelle quantité est 0, la disponibilité sera mise à 0, sinon à 1 (ou une autre valeur selon votre logique)
        int disponibilite = (nouvelleQuantite == 0) ? 0 : 1;
        String query = "UPDATE service SET quantite_materiel = ?, disponibilite = ? WHERE id_service = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, nouvelleQuantite);
            ps.setInt(2, disponibilite);
            ps.setInt(3, idService);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Service mis à jour avec succès. Nouvelle quantité : " + nouvelleQuantite
                        + ", Disponibilité : " + disponibilite);
            } else {
                System.out.println("Aucune mise à jour effectuée pour le service " + idService);
            }
        }
    }
    public void updateQuantiteAndAvailability1(int idService, int nouvelleQuantite) throws SQLException {
        // Si la nouvelle quantité est 0 ou moins, on considère que la disponibilité devient 0, sinon 1.
        int disponibilite = (nouvelleQuantite <= 0) ? 0 : 1;
        String query = "UPDATE service SET quantite_materiel = ?, disponibilite = ? WHERE id_service = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, nouvelleQuantite);
            ps.setInt(2, disponibilite);
            ps.setInt(3, idService);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Mise à jour effectuée : nouvelle quantité = " + nouvelleQuantite
                        + ", disponibilité = " + disponibilite);
            } else {
                System.out.println("Aucune mise à jour effectuée pour le service " + idService);
            }
        }
    }
}
