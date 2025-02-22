package Models;

import java.time.LocalDateTime;

public class Reservation {
    private int id_reservation;
    private Service service; // Clé étrangère en tant qu'objet
    private User utilisateur; // Clé étrangère en tant qu'objet
    private LocalDateTime date_reservation;
    private int quantite;
    private Statut statut;
    private LocalDateTime date_confirmation;
    private LocalDateTime date_annulation;

    public enum Statut {
        En_attente("En attente"),
        Confirmée("Confirmée"),
        Annulée("Annulée");

        private final String value;

        Statut(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Statut fromValue(String value) {
            for (Statut statut : Statut.values()) {
                if (statut.value.equals(value)) {
                    return statut;
                }
            }
            throw new IllegalArgumentException("Valeur inconnue : " + value);
        }
    }


    public Reservation() {}

    // Constructeur complet
    public Reservation(int id_reservation, Service service, User utilisateur, LocalDateTime date_reservation,
                       int quantite, Statut statut, LocalDateTime date_confirmation, LocalDateTime date_annulation) {
        this.id_reservation = id_reservation;
        this.service = service;
        this.utilisateur = utilisateur;
        this.date_reservation = date_reservation;
        this.quantite = quantite;
        this.statut = statut;
        this.date_confirmation = date_confirmation;
        this.date_annulation = date_annulation;
    }

    // Getters et Setters
    public int getId_reservation() {
        return id_reservation;
    }

    public void setId_reservation(int id_reservation) {
        this.id_reservation = id_reservation;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDateTime getDate_reservation() {
        return date_reservation;
    }

    public void setDate_reservation(LocalDateTime date_reservation) {
        this.date_reservation = date_reservation;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public LocalDateTime getDate_confirmation() {
        return date_confirmation;
    }

    public void setDate_confirmation(LocalDateTime date_confirmation) {
        this.date_confirmation = date_confirmation;
    }

    public LocalDateTime getDate_annulation() {
        return date_annulation;
    }

    public void setDate_annulation(LocalDateTime date_annulation) {
        this.date_annulation = date_annulation;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id_reservation=" + id_reservation +
                ", service=" + service +
                ", utilisateur=" + utilisateur +
                ", date_reservation=" + date_reservation +
                ", quantite=" + quantite +
                ", statut=" + statut +
                ", date_confirmation=" + date_confirmation +
                ", date_annulation=" + date_annulation +
                '}';
    }
}
