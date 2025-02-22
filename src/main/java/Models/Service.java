package Models;

// Import de la classe Utilisateur
import Models.User;

public class Service {
    private int id_service;
    private String nom_service;
    private String description;
    private int prix;
    private TypeService type_service;
    private int disponibilite;
    private User utilisateur; // Remplacement par un objet
    private String image_url;
    private int quantite_materiel;
    private String role_staff;
    private String experience;

    public enum TypeService {
        Matériel, Staff;


    }

    public Service() {}

    // Constructeur complet avec Utilisateur
    public Service(int id_service, String nom_service, String description, int prix, TypeService type_service,
                   int disponibilite, User utilisateur, String image_url, int quantite_materiel,
                   String role_staff, String experience) {
        this.id_service = id_service;
        this.nom_service = nom_service;
        this.description = description;
        this.prix = prix;
        this.type_service = type_service;
        this.disponibilite = disponibilite;
        this.utilisateur = utilisateur;
        this.image_url = image_url;
        this.quantite_materiel = quantite_materiel;
        this.role_staff = role_staff;
        this.experience = experience;
    }

    public int getId_service() {
        return id_service;
    }

    public void setId_service(int id_service) {
        this.id_service = id_service;
    }

    public String getNom_service() {
        return nom_service;
    }

    public void setNom_service(String nom_service) {
        this.nom_service = nom_service;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public TypeService getType_service() {
        return type_service;
    }

    public void setType_service(TypeService type_service) {
        this.type_service = type_service;
    }

    public int getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(int disponibilite) {
        this.disponibilite = disponibilite;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getQuantite_materiel() {
        return quantite_materiel;
    }

    public void setQuantite_materiel(int quantite_materiel) {
        this.quantite_materiel = quantite_materiel;
    }

    public String getRole_staff() {
        return role_staff;
    }

    public void setRole_staff(String role_staff) {
        this.role_staff = role_staff;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id_service=" + id_service +
                ", nom_service='" + nom_service + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", type_service=" + type_service +
                ", disponibilite=" + disponibilite +
                ", utilisateur=" + utilisateur +
                ", image_url='" + image_url + '\'' +
                ", quantite_materiel=" + quantite_materiel +
                ", role_staff='" + role_staff + '\'' +
                ", experience='" + experience + '\'' +
                '}';
    }

}
