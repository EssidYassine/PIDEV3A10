

import Models.Reservation;
import Models.Service;
import Models.User;
import Services.ReservationService;
import Services.ServiceService;
import Services.UserService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class maintest {
    public static void main(String[] args) {
        // Instanciation des services nécessaires
        ReservationService reservationService = new ReservationService();
        ServiceService serviceService = new ServiceService();
        UserService userService = new UserService();

        try {
            // ======= CREATE (AJOUTER) =======
            System.out.println("=== CREATE (AJOUTER) ===");
            // Récupérer un Service et un Utilisateur pour la réservation
            Service service = serviceService.getById(69); // Remplacez par un ID valide dans votre base de données
            User utilisateur = userService.getById(1);    // Remplacez par un ID valide dans votre base de données

            int createdReservationId = -1;
            if (service != null && utilisateur != null) {
                Reservation nouvelleReservation = new Reservation();
                nouvelleReservation.setService(service);
                nouvelleReservation.setUtilisateur(utilisateur);
                nouvelleReservation.setDate_reservation(LocalDateTime.now());
                nouvelleReservation.setQuantite(2);
                nouvelleReservation.setStatut(Reservation.Statut.En_attente);

                reservationService.add(nouvelleReservation);
                System.out.println("✅ Réservation ajoutée avec succès !");

                // Récupérer l'ID de la réservation ajoutée
                List<Reservation> reservations = reservationService.getAll();
                createdReservationId = reservations.get(reservations.size() - 1).getId_reservation();
                System.out.println("ID de la nouvelle réservation : " + createdReservationId);
            } else {
                System.out.println("❌ Service ou Utilisateur introuvable pour l'ajout.");
            }

            // ======= READ (LIRE) =======
            System.out.println("\n=== READ (LIRE) ===");
            List<Reservation> reservations = reservationService.getAll();
            for (Reservation r : reservations) {
                System.out.println(r);
            }

            // ======= UPDATE (METTRE À JOUR) =======
            System.out.println("\n=== UPDATE (METTRE À JOUR) ===");
            if (createdReservationId > 0) {
                Reservation reservationToUpdate = reservationService.getById(createdReservationId);
                if (reservationToUpdate != null) {
                    reservationToUpdate.setQuantite(3);
                    reservationToUpdate.setStatut(Reservation.Statut.Confirmée);
                    reservationService.update(reservationToUpdate);
                    System.out.println("✅ Réservation mise à jour avec succès !");
                } else {
                    System.out.println("❌ Réservation introuvable pour la mise à jour.");
                }
            }

            // ======= READ BY ID (LIRE PAR ID) =======
            System.out.println("\n=== READ BY ID (LIRE PAR ID) ===");
            if (createdReservationId > 0) {
                Reservation reservationById = reservationService.getById(createdReservationId);
                if (reservationById != null) {
                    System.out.println(reservationById);
                } else {
                    System.out.println("❌ Réservation introuvable pour l'ID spécifié.");
                }
            }

            // ======= DELETE (SUPPRIMER) =======
            System.out.println("\n=== DELETE (SUPPRIMER) ===");
            if (createdReservationId > 0) {
                Reservation reservationToDelete = reservationService.getById(createdReservationId);
                if (reservationToDelete != null) {
                    reservationService.delete(reservationToDelete);
                    System.out.println("✅ Réservation supprimée avec succès !");
                } else {
                    System.out.println("❌ Réservation introuvable pour la suppression.");
                }
            }

            // ======= Relecture après suppression =======
            System.out.println("\n=== READ (LIRE) APRÈS SUPPRESSION ===");
            reservations = reservationService.getAll();
            for (Reservation r : reservations) {
                System.out.println(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
