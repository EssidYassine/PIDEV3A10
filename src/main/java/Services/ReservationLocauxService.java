package Services;

import Models.ReservationLocaux;
import Interfaces.IService;
import Tools.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ReservationLocauxService implements IService<ReservationLocaux> {
    private Connection connection;

    public ReservationLocauxService() {
        connection = DataBaseConnection.getDatabaseConnection().getConnection();
        if (connection == null) {
            throw new RuntimeException("Database connection failed!");
        }
    }

    @Override
    public void add(ReservationLocaux reservation) throws SQLException {
        String query = "INSERT INTO reservation_locaux (id_local, id_user, date_debut, date_fin, statut) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, reservation.getIdLocal());
            ps.setInt(2, reservation.getIdUser());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getDateDebut()));
            ps.setTimestamp(4, Timestamp.valueOf(reservation.getDateFin()));
            ps.setString(5, reservation.getStatut());

            int rowsInserted = ps.executeUpdate();
            System.out.println(rowsInserted > 0 ? "Réservation ajoutée avec succès !" : "Aucune réservation ajoutée.");
        }
    }

    @Override
    public void update(ReservationLocaux reservation) throws SQLException {
        String query = "UPDATE reservation_locaux SET id_local=?, id_user=?, date_debut=?, date_fin=?, statut=? WHERE id_reservation=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, reservation.getIdLocal());
            ps.setInt(2, reservation.getIdUser());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getDateDebut()));
            ps.setTimestamp(4, Timestamp.valueOf(reservation.getDateFin()));
            ps.setString(5, reservation.getStatut());
            ps.setInt(6, reservation.getIdReservation());

            int rowsUpdated = ps.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "Réservation mise à jour avec succès !" : "Mise à jour échouée.");
        }
    }

    @Override
    public void delete(ReservationLocaux reservation) throws SQLException {
        String query = "DELETE FROM reservation_locaux WHERE id_reservation=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, reservation.getIdReservation());

            int rowsDeleted = ps.executeUpdate();
            System.out.println(rowsDeleted > 0 ? "Réservation supprimée avec succès !" : "Suppression échouée.");
        }
    }

    @Override
    public List<ReservationLocaux> getAll() throws SQLException {
        List<ReservationLocaux> reservationsList = new ArrayList<>();
        String query = "SELECT * FROM reservation_locaux";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ReservationLocaux reservation = new ReservationLocaux(
                        rs.getInt("id_reservation"),
                        rs.getInt("id_local"),
                        rs.getInt("id_user"),
                        rs.getTimestamp("date_debut").toLocalDateTime(),
                        rs.getTimestamp("date_fin").toLocalDateTime(),
                        rs.getString("statut")
                );
                reservationsList.add(reservation);
            }
        }
        return reservationsList;
    }
    public List<ReservationLocaux> getReservationsByLocal(int idLocal) throws SQLException {
        List<ReservationLocaux> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation_locaux WHERE id_local = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idLocal);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReservationLocaux reservation = new ReservationLocaux(
                        rs.getInt("id_reservation"),
                        rs.getInt("id_local"),
                        rs.getInt("id_user"),
                        rs.getTimestamp("date_debut").toLocalDateTime(),
                        rs.getTimestamp("date_fin").toLocalDateTime(),
                        rs.getString("statut")
                );
                reservations.add(reservation);
            }
        }
        return reservations;
    }


    @Override
    public ReservationLocaux getById(int id) throws SQLException {
        String query = "SELECT * FROM reservation_locaux WHERE id_reservation = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new ReservationLocaux(
                        rs.getInt("id_reservation"),
                        rs.getInt("id_local"),
                        rs.getInt("id_user"),
                        rs.getTimestamp("date_debut").toLocalDateTime(),
                        rs.getTimestamp("date_fin").toLocalDateTime(),
                        rs.getString("statut")
                );
            }
        }
        return null;
    }

    public List<ReservationLocaux> getAllUserReservations(int userId) throws SQLException {
        List<ReservationLocaux> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation_locaux  WHERE id_user = ?"; // Fix column name

        System.out.println("Executing query: " + query + " with id_user : " + userId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                reservations.add(new ReservationLocaux(
                        resultSet.getInt("id_reservation"),
                        resultSet.getInt("id_local"),
                        resultSet.getInt("id_user"), // Ensure column name is correct
                        resultSet.getTimestamp("date_debut").toLocalDateTime(),
                        resultSet.getTimestamp("date_fin").toLocalDateTime(),
                        resultSet.getString("statut")
                ));
            }
        }

        System.out.println("Fetched " + reservations.size() + " reservations for user ID: " + userId);
        return reservations;
    }



}

