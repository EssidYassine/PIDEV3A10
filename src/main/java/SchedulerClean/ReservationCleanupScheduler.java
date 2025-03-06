package SchedulerClean;

import Services.ReservationGP;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReservationCleanupScheduler {

    private final ScheduledExecutorService scheduler;
    private final ReservationGP reservationService;

    public ReservationCleanupScheduler() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.reservationService = new ReservationGP();
    }

    // Démarre la tâche de nettoyage qui s'exécute toutes les 3 jours.
    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                reservationService.deleteOldCanceledReservations();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.DAYS);
    }

    // Méthode pour arrêter le scheduler si nécessaire.
    public void stop() {
        scheduler.shutdown();
    }

    // Exemple d'exécution dans la méthode main.
    public static void main(String[] args) {
        ReservationCleanupScheduler cleanupScheduler = new ReservationCleanupScheduler();
        cleanupScheduler.start();
        // Dans une application réelle, la durée de vie du scheduler sera gérée par le cycle de vie de l'application.
    }
}
