package Models;

import java.time.LocalDateTime;

public class Reservation {
    private int id_reservation;
    private int id_service;
    private int id_utilisateur;
    private LocalDateTime date_reservation;
    private int quantite;
    private Statut statut;

    public enum Statut {
        En_attente, Confirmée, Annulée
    }

    public Reservation() {}

    public Reservation(int id_reservation, int id_service, int id_utilisateur, LocalDateTime date_reservation, int quantite, Statut statut) {
        this.id_reservation = id_reservation;
        this.id_service = id_service;
        this.id_utilisateur = id_utilisateur;
        this.date_reservation = date_reservation;
        this.quantite = quantite;
        this.statut = statut;
    }

    public int getId_reservation() { return id_reservation; }
    public void setId_reservation(int id_reservation) { this.id_reservation = id_reservation; }

    public int getId_service() { return id_service; }
    public void setId_service(int id_service) { this.id_service = id_service; }

    public int getId_utilisateur() { return id_utilisateur; }
    public void setId_utilisateur(int id_utilisateur) { this.id_utilisateur = id_utilisateur; }

    public LocalDateTime getDate_reservation() { return date_reservation; }
    public void setDate_reservation(LocalDateTime date_reservation) { this.date_reservation = date_reservation; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id_reservation=" + id_reservation +
                ", id_service=" + id_service +
                ", id_utilisateur=" + id_utilisateur +
                ", date_reservation=" + date_reservation +
                ", quantite=" + quantite +
                ", statut=" + statut +
                '}';
    }
}
