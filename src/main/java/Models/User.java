package Models;

public class User {
    private int id_user;
    private String nom;
    private String prenom;
    private String email;
    private String mot_de_passe;

    // Constructeur avec 4 paramètres
    public User(int idUtilisateur, String nom, String prenom, String email) {
        this.id_user = idUtilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mot_de_passe = ""; // ou null selon vos besoins
    }

    // Constructeur avec 5 paramètres
    public User(int id_user, String nom, String prenom, String email, String mot_de_passe) {
        this.id_user = id_user;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mot_de_passe = mot_de_passe;
    }

    public User() {}

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMot_de_passe() {
        return mot_de_passe;
    }

    public void setMot_de_passe(String mot_de_passe) {
        this.mot_de_passe = mot_de_passe;
    }

    @Override
    public String toString() {
        return "User{" +
                "id_user=" + id_user +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", mot_de_passe='" + mot_de_passe + '\'' +
                '}';
    }

    public int getId_utilisateur() {
        return id_user;
    }
}
