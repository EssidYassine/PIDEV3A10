package Models;

import java.time.LocalDate;

public class User {

    private int id;                  // Correspond à la colonne `id`
    private String username;         // Correspond à la colonne `username`
    private String email;            // Correspond à la colonne `email`
    private String password;         // Correspond à la colonne `password`
    private String role;             // Correspond à la colonne `role` (valeurs possibles: "admin" ou "user")
    private boolean isActive;        // Correspond à la colonne `is_active` (0 ou 1 en base)
    private String numTel;           // Correspond à la colonne `num_tel`
    private LocalDate dateDeNaissance; // Correspond à la colonne `date_de_naissance` (type DATE)

    // Constructeur vide
    public User() {
    }

    // Constructeur complet
    public User(int id, String username, String email, String password,
                String role, boolean isActive, String numTel, LocalDate dateDeNaissance) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.numTel = numTel;
        this.dateDeNaissance = dateDeNaissance;
    }

    // Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getNumTel() {
        return numTel;
    }

    public void setNumTel(String numTel) {
        this.numTel = numTel;
    }

    public LocalDate getDateDeNaissance() {
        return dateDeNaissance;
    }

    public void setDateDeNaissance(LocalDate dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                ", numTel='" + numTel + '\'' +
                ", dateDeNaissance=" + dateDeNaissance +
                '}';
    }
}
