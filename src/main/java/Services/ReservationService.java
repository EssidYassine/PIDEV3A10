package Services;

import Models.Reservation;
import Models.Service;
import Models.User;
import Interfaces.IService;
import Tools.DataBaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements IService<Reservation> {
    private static Connection connection;

    // Dépendances pour charger les objets liés
    private final ServiceService serviceService = new ServiceService();
    private final UserService userService = new UserService();

    public ReservationService() {
        connection = DataBaseConnection.getMyDataBase().getConnection();
    }

    @Override
    public void add(Reservation reservation) throws SQLException {
        String query = "INSERT INTO reservation (id_service, id_utilisateur, date_reservation, quantite, statut) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getService().getId_service());
            ps.setInt(2, reservation.getUtilisateur().getId_utilisateur());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getDate_reservation())); // Conversion LocalDateTime -> Timestamp
            ps.setInt(4, reservation.getQuantite());
            ps.setString(5, reservation.getStatut().getValue());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Réservation ajoutée avec succès !");
            } else {
                System.out.println("❌ Aucune réservation ajoutée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Reservation reservation) throws SQLException {
        String query = "UPDATE reservation SET id_service=?, id_utilisateur=?, date_reservation=?, quantite=?, statut=? WHERE id_reservation=?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, reservation.getService().getId_service());
            ps.setInt(2, reservation.getUtilisateur().getId_utilisateur());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getDate_reservation())); // Conversion LocalDateTime -> Timestamp
            ps.setInt(4, reservation.getQuantite());
            ps.setString(5, reservation.getStatut().getValue());
            ps.setInt(6, reservation.getId_reservation());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Réservation mise à jour avec succès !");
            } else {
                System.out.println("❌ Aucune mise à jour effectuée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Reservation reservation) throws SQLException {
        String query = "DELETE FROM reservation WHERE id_reservation=?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, reservation.getId_reservation());
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Réservation supprimée avec succès !");
            } else {
                System.out.println("❌ Aucune réservation supprimée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Reservation> getAll() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Reservation reservation = creerReservation(rs);
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                return creerReservation(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Méthode pour créer un objet Reservation à partir d'un ResultSet
     * @param rs ResultSet contenant les données
     * @return Objet Reservation
     * @throws SQLException
     */
    private Reservation creerReservation(ResultSet rs) throws SQLException {
        // Récupération des objets liés (Service et User)
        Service service = serviceService.getById(rs.getInt("id_service"));
        User utilisateur = userService.getById(rs.getInt("id_utilisateur"));

        // Récupération du statut en utilisant la méthode fromValue()
        Reservation.Statut statut = Reservation.Statut.fromValue(rs.getString("statut"));

        // Création de l'objet Reservation
        return new Reservation(
                rs.getInt("id_reservation"),
                service, // Utilisation de l'objet Service
                utilisateur, // Utilisation de l'objet User
                rs.getTimestamp("date_reservation").toLocalDateTime(),
                rs.getInt("quantite"),
                statut, // Utilisation de l'énumération Statut
                rs.getTimestamp("date_confirmation") != null ? rs.getTimestamp("date_confirmation").toLocalDateTime() : null,
                rs.getTimestamp("date_annulation") != null ? rs.getTimestamp("date_annulation").toLocalDateTime() : null
        );
    }
}
