package Services;



import Models.Service;
import Interfaces.IService;
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
        String query = "INSERT INTO service (nom_service, description, prix, type_service, disponibilite, id_utilisateur, image_url, quantite_materiel, role_staff, experience) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, service.getNom_service());
            ps.setString(2, service.getDescription());
            ps.setInt(3, service.getPrix());
            ps.setString(4, service.getType_service().name());
            ps.setInt(5, service.getDisponibilite());
            ps.setInt(6, service.getId_utilisateur());
            ps.setString(7, service.getImage_url());
            ps.setInt(8, service.getQuantite_materiel());
            ps.setString(9, service.getRole_staff());
            ps.setString(10, service.getExperience());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Service ajouté avec succès !");
            } else {
                System.out.println("❌ Aucun service ajouté.");
            }
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
            ps.setInt(6, service.getId_utilisateur());
            ps.setString(7, service.getImage_url());
            ps.setInt(8, service.getQuantite_materiel());
            ps.setString(9, service.getRole_staff());
            ps.setString(10, service.getExperience());
            ps.setInt(11, service.getId_service());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Service mis à jour avec succès !");
            } else {
                System.out.println(" Aucune mise à jour effectuée.");
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
        String query = "SELECT * FROM service";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Service service = new Service(
                        rs.getInt("id_service"),
                        rs.getString("nom_service"),
                        rs.getString("description"),
                        rs.getInt("prix"),
                        Service.TypeService.valueOf(rs.getString("type_service")),
                        rs.getInt("disponibilite"),
                        rs.getInt("id_utilisateur"),
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

    @Override
    public Service getById(int id) throws SQLException {
        String query = "SELECT * FROM service WHERE id_service = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Service(
                        rs.getInt("id_service"),
                        rs.getString("nom_service"),
                        rs.getString("description"),
                        rs.getInt("prix"),
                        Service.TypeService.valueOf(rs.getString("type_service")),
                        rs.getInt("disponibilite"),
                        rs.getInt("id_utilisateur"),
                        rs.getString("image_url"),
                        rs.getInt("quantite_materiel"),
                        rs.getString("role_staff"),
                        rs.getString("experience")
                );
            }
        }
        return null;
    }}
