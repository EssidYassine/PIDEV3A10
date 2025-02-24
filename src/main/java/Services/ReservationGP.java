package Services;

import Interfaces.IService;
import Models.Reservation;
import Tools.DataBaseConnection;
import Models.Locaux;
import Models.Pack;
import Models.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReservationGP implements IService<Reservation> {

    DataBaseConnection cnx = new DataBaseConnection();

    private final ServiceGP serviceService = new ServiceGP();
    private final UserService userService = new UserService();

    public String addR(Reservation reservation) throws SQLException {
        Connection conn = null;
        String qrCode = null;
        try {
            conn = cnx.getConnection();
            conn.setAutoCommit(false);

            String reservationQuery = "INSERT INTO reservationpack (pack_id, user_id, nbre_invites, "
                    + "budget_alloue, date_reservation, statut_reservation, commentaire, qr_code_url) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; // Suppression de lieu_id

            try (PreparedStatement stmt = conn.prepareStatement(reservationQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                // Génération QR Code
                qrCode = "QR_" + UUID.randomUUID() + "_" + System.currentTimeMillis();

                stmt.setInt(1, reservation.getPackId());
                stmt.setInt(2, reservation.getUser().getId_user());
                stmt.setInt(3, reservation.getNbreInvites());
                stmt.setBigDecimal(4, reservation.getBudgetAlloue());
                stmt.setTimestamp(5, reservation.getDateReservation());
                stmt.setString(6, "en attente"); // Correction ici pour correspondre à l'énum
                stmt.setString(7, reservation.getCommentaire());
                stmt.setString(8, qrCode); // Ajustement pour correspondre aux colonnes

                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int reservationId = generatedKeys.getInt(1);
                        insertServices(conn, reservationId, reservation.getServices());
                    }
                }
            }
            conn.commit();
            return qrCode;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }



    public List<Service> getServicesByPackId(int packId) throws SQLException {
        System.out.println("Tentative de récupération des services pour pack_id=" + packId);

        List<Service> services = new ArrayList<>();
        String query = "SELECT s.id_service, s.nom_service FROM service s " +
                "JOIN packservice ps ON s.id_service = ps.service_id " +
                "WHERE ps.pack_id = ?";

        try (Connection conn = cnx.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, packId);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                Service service = new Service();
                service.setId_service(rs.getInt("id_service"));
                service.setNom_service(rs.getString("nom_service"));
                services.add(service);
                count++;
            }
            System.out.println(count + " services trouvés");

        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            throw e;
        }

        return services;
    }


    private void insertServices(Connection conn, int reservationId, List<Service> services) throws SQLException {
        String serviceQuery = "INSERT INTO reservationservice (reservation_id, service_id) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(serviceQuery)) {
            for (Service service : services) {
                stmt.setInt(1, reservationId);
                stmt.setInt(2, service.getId_service());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }



    public List<Locaux> getAllLieux() throws SQLException {
        List<Locaux> lieux = new ArrayList<>();
        String query = "SELECT * FROM locaux";

        try (Connection conn = cnx.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Locaux lieu = new Locaux();
                lieu.setIdLocal(rs.getInt("id_local")); // Corrected column name
                lieu.setAdresse(rs.getString("adresse"));
                lieux.add(lieu);
            }
        }
        return lieux;
    }

    public Locaux getLocauxByName(String lieuNom) throws SQLException {
        List<Locaux> tousLesLocaux = getAllLieux();

        for (Locaux local : tousLesLocaux) {
            if (local.getAdresse().equalsIgnoreCase(lieuNom)) {
                return local; // Retourner le lieu si une correspondance est trouvée
            }
        }

        return null; // Retourner null si aucun lieu n'est trouvé
    }

    public List<Service> getServicesByNames(List<String> serviceNames) throws SQLException {
        List<Service> services = new ArrayList<>();
        if (serviceNames.isEmpty()) return services;

        String query = "SELECT id_service, nom_service FROM service WHERE nom_service IN ("
                + String.join(",", Collections.nCopies(serviceNames.size(), "?"))
                + ")";

        try (Connection conn = cnx.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < serviceNames.size(); i++) {
                stmt.setString(i + 1, serviceNames.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Service service = new Service();
                service.setId_service(rs.getInt("id_service"));
                service.setNom_service(rs.getString("nom_service"));
                services.add(service);
            }
        }
        return services;
    }


    public Locaux getDefaultLieuByPackId(int packId) throws SQLException {
        String query = "SELECT lieu_id FROM packevenement WHERE pack_id = ?";

        try (Connection conn = cnx.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, packId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int lieuId = rs.getInt("lieu_id");
                return getLocauxById(lieuId);
            }
        }
        return null;
    }


    private Locaux getLocauxById(int id) throws SQLException {
        String query = "SELECT * FROM locaux WHERE id_local = ?";

        try (Connection conn = cnx.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Locaux lieu = new Locaux();
                lieu.setIdLocal(rs.getInt("id_local"));
                lieu.setAdresse(rs.getString("adresse"));
                return lieu;
            }
        }
        return null;
    }

    private void insertReservationServices(int reservationId, List<Service> services) throws SQLException {
        String query = "INSERT INTO reservationservice (reservation_id, service_id) VALUES (?, ?)";

        try (Connection conn = cnx.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (Service service : services) {
                stmt.setInt(1, reservationId);
                stmt.setInt(2, service.getId_service());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }


    @Override
    public void add(Reservation reservation) throws SQLException {

    }
    @Override
    public void update(Reservation reservation) throws SQLException {

    }

    @Override
    public void delete(Reservation reservation) throws SQLException {

    }

    @Override
    public List<Reservation> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public Reservation getById(int id) throws SQLException {
        return null;
    }
}