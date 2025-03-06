package Services;

import Models.Reservation;
import Models.User;
import Tools.DataBaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private static Connection connection;
    DataBaseConnection cnx = new DataBaseConnection();

    public List<Reservation> getNewReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.reservation_id, r.pack_id, r.created_at, r.date_reservation,u.email " +
                "FROM reservationpack r " +
                "JOIN user u ON r.user_id = u.id " +
                "WHERE r.created_at >= ?";

        try (Connection conn = cnx.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Recherche sur les 5 dernières minutes
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().minusMinutes(5)));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt("reservation_id"));
                reservation.setPackId(rs.getInt("pack_id"));

                User user = new User();
                user.setEmail(rs.getString("email"));
                reservation.setUser(user);

                reservation.setDateReservation(rs.getTimestamp("date_reservation"));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
        }
        return reservations;
    }
}