package Services;

import Interfaces.IService;
import Models.Reservation;
import Tools.DataBaseConnection;
import Models.Locaux;
import Models.Service;
import Models.User;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class ReservationGP implements IService<Reservation> {

    private static Connection connection;
    DataBaseConnection cnx = new DataBaseConnection();


    public ReservationGP() {
        connection = DataBaseConnection.getConnection();
    }


    public String addR(Reservation reservation) throws SQLException {
        Connection conn = null;
        String qrCode = null;
        try {
            conn = cnx.getConnection();
            conn.setAutoCommit(false);

            String reservationQuery = "INSERT INTO reservationpack (pack_id, user_id, nbre_invites, "
                    + "budget_alloue, date_reservation, statut_reservation, commentaire, qr_code_url,lieu_id,created_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

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
                stmt.setInt(9, reservation.getLieu().getIdLocal()); // Ajustement pour correspondre aux colonnes
                stmt.setTimestamp(10, new Timestamp(System.currentTimeMillis())); // created_at



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

    public List<Reservation> getReservationsByWeekAndStatus(LocalDate weekStart, Reservation.StatutReservation status)
            throws SQLException {

        List<Reservation> reservations = new ArrayList<>();
        LocalDate weekEnd = weekStart.plusDays(6);

        String query = "SELECT r.*, u.email, p.nom as pack_nom " +
                "FROM reservationpack r " +
                "LEFT JOIN user u ON r.user_id = u.id " +
                "LEFT JOIN packevenement p ON r.pack_id = p.pack_id  " +
                "WHERE r.date_reservation BETWEEN ? AND ?";

        if (status != null) {
            query += " AND r.statut_reservation = ?";
        }

        try (Connection conn = cnx.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setTimestamp(1, Timestamp.valueOf(weekStart.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(weekEnd.atTime(LocalTime.MAX)));

            if (status != null) {
                stmt.setString(3, status.getDbValue());
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt("reservation_id"));
                reservation.setDateReservation(rs.getTimestamp("date_reservation"));

                // User info
                User user = new User();
                user.setEmail(rs.getString("email"));
                reservation.setUser(user);

                // Pack info
                reservation.setPackId(rs.getInt("pack_id"));

                // Correction clé : Récupération du statut depuis la base
                String dbStatus = rs.getString("statut_reservation");
                reservation.setStatutReservation(Reservation.StatutReservation.fromDbValue(dbStatus)); // Utilisation du convertisseur

                reservation.setNbreInvites(rs.getInt("nbre_invites"));
                reservation.setCommentaire(rs.getString("commentaire"));

                reservations.add(reservation);
            }
        }
        return reservations;
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


    public List<Reservation> getReservationsByWeek(LocalDate weekStart) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        LocalDateTime startOfWeek = weekStart.atStartOfDay();
        LocalDateTime endOfWeek = weekStart.plusDays(6).atTime(23, 59, 59);

        String query = "SELECT * FROM reservationpack WHERE date_reservation BETWEEN ? AND ?";
        try (Connection conn = cnx.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startOfWeek));
            stmt.setTimestamp(2, Timestamp.valueOf(endOfWeek));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Reservation r = new Reservation();
                r.setReservationId(rs.getInt("reservation_id"));
                r.setDateReservation(rs.getTimestamp("date_reservation"));
                // Ajoutez le reste des champs nécessaires
                User user = new User();
                user.setEmail(rs.getString("user_id")); // À adapter selon votre modèle
                r.setUser(user);
                r.setNbreInvites(rs.getInt("nbre_invites"));
                String statutString = rs.getString("statut_reservation").trim();
// Remplacer les espaces par des underscores et mettre en majuscules pour correspondre à l'enum
                statutString = statutString.replace(" ", "_").toUpperCase();
                r.setStatutReservation(Reservation.StatutReservation.valueOf(statutString));


                reservations.add(r);
            }
        }
        return reservations;
    }

    public boolean isDateTimeReserved(LocalDateTime dateTime) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM reservationpack WHERE date_reservation = ?";
        try (Connection conn = cnx.getConnection();          PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(dateTime));
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt("count") > 0;
        }
    }

    public void deleteOldCanceledReservations() throws SQLException {
        // Requête SQL qui supprime les réservations annulées datant de plus de 3 jours.
        String sql = "DELETE FROM reservationpack WHERE statut_reservation = ? AND date_reservation < ?";
        try (Connection conn = cnx.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "ANNULÉE");
            // Calcule la date limite : aujourd'hui moins 3 jours.
            LocalDateTime threshold = LocalDateTime.now().minusDays(3);
            ps.setTimestamp(2, Timestamp.valueOf(threshold));

            int deletedRows = ps.executeUpdate();
            System.out.println(deletedRows + " réservations annulées ont été supprimées.");
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
        String query = "DELETE FROM reservationpack WHERE reservation_id = ?";
        try (Connection conn = cnx.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reservation.getReservationId());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Reservation> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public Reservation getById(int reservationId) throws SQLException {
        String query = "SELECT r.*, u.email, l.adresse, r.statut_reservation, r.nbre_invites, r.commentaire " +
                "FROM reservationpack r " +
                "LEFT JOIN user u ON r.user_id = u.id " +
                "LEFT JOIN locaux l ON r.lieu_id = l.id_local " +
                "WHERE r.reservation_id = ?";


        try (Connection conn = cnx.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, reservationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt("reservation_id"));
                // Récupération de la date
                Timestamp timestamp = rs.getTimestamp("date_reservation");
                if (timestamp != null) {
                    reservation.setDateReservation(timestamp);
                } else {
                    throw new SQLException("Date de réservation non trouvée pour ID: " + reservationId);
                }
                // Récupération User
                User user = new User();
                user.setEmail(rs.getString("email"));
                reservation.setUser(user);

                // Récupération Lieu
                Locaux lieu = new Locaux();
                lieu.setAdresse(rs.getString("adresse"));
                reservation.setLieu(lieu);
                // Correction clé : Récupération du statut depuis la base
                String dbStatus = rs.getString("statut_reservation");
                reservation.setStatutReservation(Reservation.StatutReservation.fromDbValue(dbStatus)); // Utilisation du convertisseur
                reservation.setNbreInvites(rs.getInt("nbre_invites"));
                reservation.setCommentaire(rs.getString("commentaire"));


                return reservation;
            }
        }
        return null;
    }

    public void updateStatus(Reservation reservation) throws SQLException {
        String query = "UPDATE reservationpack SET statut_reservation = ? WHERE reservation_id = ?";
        try (Connection conn = cnx.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, reservation.getStatutReservation().getDbValue());
            stmt.setInt(2, reservation.getReservationId());
            stmt.executeUpdate();
        }
    }



}