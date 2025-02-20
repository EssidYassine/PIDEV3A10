package Services;



import Models.Reservation;
import Interfaces.IService;
import Tools.DataBaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements IService<Reservation> {
    private static Connection connection;

    public ReservationService() {
        connection = DataBaseConnection.getMyDataBase().getConnection();
    }

    @Override
    public void add(Reservation reservation) throws SQLException {
        String query = "INSERT INTO reservation (id_service, id_utilisateur, date_reservation, quantite, statut) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getId_service());
            ps.setInt(2, reservation.getId_utilisateur());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getDate_reservation())); // Conversion LocalDateTime -> Timestamp
            ps.setInt(4, reservation.getQuantite());
            ps.setString(5, reservation.getStatut().name());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(" Réservation ajoutée avec succès !");
            } else {
                System.out.println(" Aucune réservation ajoutée.");
            }
        }
    }

    @Override
    public void update(Reservation reservation) throws SQLException {
        String query = "UPDATE reservation SET id_service=?, id_utilisateur=?, date_reservation=?, quantite=?, statut=? WHERE id_reservation=?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, reservation.getId_service());
            ps.setInt(2, reservation.getId_utilisateur());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getDate_reservation())); // Conversion LocalDateTime -> Timestamp
            ps.setInt(4, reservation.getQuantite());
            ps.setString(5, reservation.getStatut().name());
            ps.setInt(6, reservation.getId_reservation());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Réservation mise à jour avec succès !");
            } else {
                System.out.println(" Aucune mise à jour effectuée.");
            }
        }
    }

    @Override
    public void delete(Reservation reservation) throws SQLException {
        String query = "DELETE FROM reservation WHERE id_reservation=?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, reservation.getId_reservation());
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println(" Réservation supprimée avec succès !");
            } else {
                System.out.println(" Aucune réservation supprimée.");
            }
        }
    }

    @Override
    public List<Reservation> getAll() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt("id_reservation"),
                        rs.getInt("id_service"),
                        rs.getInt("id_utilisateur"),
                        rs.getTimestamp("date_reservation").toLocalDateTime(), // Conversion Timestamp -> LocalDateTime
                        rs.getInt("quantite"),
                        Reservation.Statut.valueOf(rs.getString("statut"))
                );
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    @Override
    public Reservation getById(int id) throws SQLException {
        String query = "SELECT * FROM reservation WHERE id_reservation = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Reservation(
                        rs.getInt("id_reservation"),
                        rs.getInt("id_service"),
                        rs.getInt("id_utilisateur"),
                        rs.getTimestamp("date_reservation").toLocalDateTime(), // Conversion Timestamp -> LocalDateTime
                        rs.getInt("quantite"),
                        Reservation.Statut.valueOf(rs.getString("statut"))
                );
            }
        }
        return null;
    }
}

