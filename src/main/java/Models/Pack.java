package Models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Pack {
    private int id;
    private String nom;
    private String type; // Mariage, Conférence, Fête, Autre
    private String description;
    private BigDecimal prix;
    private Integer nbreInvitesMax;
    private BigDecimal budgetPrevu;
    private LocalDate dateEvenement;
    private String lieu;    // On stocke directement le nom du lieu
    private String statut;  // "actif", "inactif", "archive"

    public Pack() {
    }

    public Pack(String nom, String type, String description, BigDecimal prix,
                Integer nbreInvitesMax, BigDecimal budgetPrevu, LocalDate dateEvenement,
                String lieu, String statut) {
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.prix = prix;
        this.nbreInvitesMax = nbreInvitesMax;
        this.budgetPrevu = budgetPrevu;
        this.dateEvenement = dateEvenement;
        this.lieu = lieu;
        this.statut = statut;
    }

    // Getters et Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }

    public int getNbreInvitesMax() { return nbreInvitesMax; }
    public void setNbreInvitesMax(Integer nbreInvitesMax) { this.nbreInvitesMax = nbreInvitesMax; }

    public BigDecimal getBudgetPrevu() { return budgetPrevu; }
    public void setBudgetPrevu(BigDecimal budgetPrevu) { this.budgetPrevu = budgetPrevu; }

    public LocalDate getDateEvenement() { return dateEvenement; }
    public void setDateEvenement(LocalDate dateEvenement) { this.dateEvenement = dateEvenement; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
