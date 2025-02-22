package Models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    private String statut;  // "actif", "inactif", "archivé"
    private List<String> services;

    // Constructeur par défaut
    public Pack() {
    }

    // Constructeur sans ID (utile lors de la création avant insertion en BDD)
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

    // Constructeur complet incluant l'ID
    public Pack(int id, String nom, String type, String description, BigDecimal prix,
                Integer nbreInvitesMax, BigDecimal budgetPrevu, LocalDate dateEvenement,
                String lieu, String statut) {
        this.id = id;
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
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getType() {
        return type;
    }public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getPrix() {
        return prix;
    }
    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }
    public Integer getNbreInvitesMax() {
        return nbreInvitesMax;
    }
    public void setNbreInvitesMax(Integer nbreInvitesMax) {
        this.nbreInvitesMax = nbreInvitesMax;
    }
    public BigDecimal getBudgetPrevu() {
        return budgetPrevu;
    }
    public void setBudgetPrevu(BigDecimal budgetPrevu) {
        this.budgetPrevu = budgetPrevu;
    }
    public LocalDate getDateEvenement() {
        return dateEvenement;
    }
    public void setDateEvenement(LocalDate dateEvenement) {
        this.dateEvenement = dateEvenement;
    }
    public String getLieu() {
        return lieu;
    }
    public void setLieu(String lieu) {
        this.lieu = lieu;
    }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public List<String> getServices() {
        return services;
    }
    public void setServices(List<String> services) {
        this.services = services;
    }

    public boolean isActive() {
        // Remplacer "active" par "actif" pour correspondre au français
        return statut != null && statut.equalsIgnoreCase("actif");
    }
    public void setType(String type) {
        this.type = type;
    }

    private boolean isValidType(String type) {
        return type != null &&
                (type.equals("Mariage") ||
                        type.equals("Conférence") ||
                        type.equals("Fête") ||
                        type.equals("Autre"));
    }


    private boolean isValidStatut(String statut) {
        return statut != null &&
                (statut.equals("actif") ||
                        statut.equals("inactif") ||
                        statut.equals("archivé"));
    }

}
