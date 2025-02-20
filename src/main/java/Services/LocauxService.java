package Services;

import Models.Locaux;
import Interfaces.IService;
import Tools.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class LocauxService implements IService<Locaux> {
    private Connection connection; // Changed from static

    public LocauxService() {
        connection = DataBaseConnection.getDatabaseConnection().getConnection();
        if (connection == null) {
            throw new RuntimeException("Database connection failed!"); // Handle null connection
        }
    }

    @Override
    public void add(Locaux local) throws SQLException {
        String query = "INSERT INTO locaux (id_user, Adresse, capacite, type, photo, equipement, tarifs) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, local.getIdUser());
            ps.setString(2, local.getAdresse());
            ps.setInt(3, local.getCapacite());
            ps.setString(4, local.getType());
            ps.setString(5, local.getPhoto());
            ps.setString(6, local.getEquipement());
            ps.setBigDecimal(7, local.getTarifs());

            int rowsInserted = ps.executeUpdate();
            System.out.println(rowsInserted > 0 ? "Local ajouté avec succès !" : "Aucun local ajouté.");
        }
    }

    @Override
    public void update(Locaux local) throws SQLException {
        String query = "UPDATE locaux SET id_user=?, Adresse=?, capacite=?, type=?, photo=?, equipement=?, tarifs=? WHERE id_local=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, local.getIdUser());
            ps.setString(2, local.getAdresse());
            ps.setInt(3, local.getCapacite());
            ps.setString(4, local.getType());
            ps.setString(5, local.getPhoto());
            ps.setString(6, local.getEquipement());
            ps.setBigDecimal(7, local.getTarifs());
            ps.setInt(8, local.getIdLocal());

            int rowsUpdated = ps.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "Local mis à jour avec succès !" : "Mise à jour échouée.");
        }
    }

    @Override
    public void delete(Locaux local) throws SQLException {
        String query = "DELETE FROM locaux WHERE id_local=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, local.getIdLocal());

            int rowsDeleted = ps.executeUpdate();
            System.out.println(rowsDeleted > 0 ? "Local supprimé avec succès !" : "Suppression échouée.");
        }
    }

    @Override
    public List<Locaux> getAll() throws SQLException {
        List<Locaux> locauxList = new ArrayList<>();
        String query = "SELECT * FROM locaux";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Locaux local = new Locaux(
                        rs.getInt("id_local"), // Uncommented this
                        rs.getInt("id_user"),
                        rs.getString("Adresse"),
                        rs.getInt("capacite"),
                        rs.getString("type"),
                        rs.getString("photo"),
                        rs.getString("equipement"),
                        rs.getBigDecimal("tarifs")
                );
                locauxList.add(local);
            }
        }
        return locauxList;
    }


    @Override
    public Locaux getById(int id) throws SQLException {
        String query = "SELECT * FROM locaux WHERE id_local = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Locaux(
                        rs.getInt("id_local"),
                        rs.getInt("id_user"),
                        rs.getString("Adresse"),
                        rs.getInt("capacite"),
                        rs.getString("type"),
                        rs.getString("photo"),
                        rs.getString("equipement"),
                        rs.getBigDecimal("tarifs")
                );
            }
        }
        return null;
    }
    public List<Locaux> getAllSorted(String sortOrder) throws SQLException {
        String query;
        if ("Tarifs: Ascending".equals(sortOrder)) {
            query = "SELECT * FROM locaux ORDER BY tarifs ASC";  // Ascending order
        } else if ("Tarifs: Descending".equals(sortOrder)) {
            query = "SELECT * FROM locaux ORDER BY tarifs DESC"; // Descending order
        } else {
            query = "SELECT * FROM locaux"; // Default: No sorting
        }

        List<Locaux> locauxList = new ArrayList<>();

        try (Connection connection = DataBaseConnection.getConnection();  // Use your own database connection
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Locaux local = new Locaux();

                local.setAdresse(rs.getString("adresse"));
                local.setCapacite(rs.getInt("capacite"));
                local.setTarifs(rs.getBigDecimal("tarifs"));
                // Add more fields as needed
                locauxList.add(local);
            }
        }

        return locauxList;
    }
}
