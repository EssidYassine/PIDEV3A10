package Models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Reservation {

    private int reservationId;        // ID unique de la réservation
    private int packId;               // ID du pack réservé
    private User user;                // Utilisateur qui effectue la réservation
    private int nbreInvites;          // Nombre d'invités pour la réservation
    private BigDecimal budgetAlloue;  // Budget alloué à la réservation
    private String qrCodeUrl;         // URL du QR code associé à la réservation
    private Timestamp dateReservation; // Date et heure de la réservation
    private StatutReservation statutReservation; // Statut de la réservation
    private String commentaire;        // Commentaire optionnel de l'utilisateur
    private Locaux lieu;               // Lieu associé au pack réservé
    private List<Service> services;    // Liste des services associés à la réservation

    public Reservation() {}
    // Constructeur
    public Reservation(int packId, User user, int nbreInvites, BigDecimal budgetAlloue,
                       String qrCodeUrl, Timestamp dateReservation, StatutReservation statutReservation,
                       String commentaire, Locaux lieu, List<Service> services) {
        this.packId = packId;
        this.user = user;
        this.nbreInvites = nbreInvites;
        this.budgetAlloue = budgetAlloue;
        this.qrCodeUrl = qrCodeUrl;
        this.dateReservation = dateReservation;
        this.statutReservation = statutReservation;
        this.commentaire = commentaire;
        this.lieu = lieu;
        this.services = services;
    }

    // Constructeur
    public Reservation(User user, int nbreInvites, BigDecimal budgetAlloue,
                       String qrCodeUrl, Timestamp dateReservation, StatutReservation statutReservation,
                       String commentaire, Locaux lieu, List<Service> services) {
        this.user = user;
        this.nbreInvites = nbreInvites;
        this.budgetAlloue = budgetAlloue;
        this.qrCodeUrl = qrCodeUrl;
        this.dateReservation = dateReservation;
        this.statutReservation = statutReservation;
        this.commentaire = commentaire;
        this.lieu = lieu;
        this.services = services;
    }

    // Getters et setters
    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getPackId() {
        return packId;
    }

    public void setPackId(int packId) {
        this.packId = packId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNbreInvites() {
        return nbreInvites;
    }

    public void setNbreInvites(int nbreInvites) {
        this.nbreInvites = nbreInvites;
    }

    public BigDecimal getBudgetAlloue() {
        return budgetAlloue;
    }

    public void setBudgetAlloue(BigDecimal budgetAlloue) {
        this.budgetAlloue = budgetAlloue;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public Timestamp getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Timestamp dateReservation) {
        this.dateReservation = dateReservation;
    }

    public StatutReservation getStatutReservation() {
        return statutReservation;
    }


    public void setStatutReservation(StatutReservation statutReservation) {
        this.statutReservation = statutReservation;
    }


    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Locaux getLieu() {
        return lieu;
    }

    public void setLieu(Locaux lieu) {
        this.lieu = lieu;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public enum StatutReservation {
        EN_ATTENTE("en attente"),
        CONFIRMÉE("confirmée"),    // Accent ajouté
        ANNULÉE("annulée");         // Orthographe corrigée
        private final String dbValue;

        StatutReservation(String dbValue) {
            this.dbValue = dbValue;
        }

        public String getDbValue() {
            return dbValue;
        }

        // Conversion depuis la valeur de la BDD vers la constante enum
        public static StatutReservation fromDbValue(String value) {
            for (StatutReservation statut : values()) {
                if (statut.getDbValue().equalsIgnoreCase(value)) {
                    return statut;
                }
            }
            throw new IllegalArgumentException("Valeur de statut inconnue: " + value);
        }
    }
}
