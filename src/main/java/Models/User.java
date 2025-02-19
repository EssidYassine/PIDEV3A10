package Models;

import java.util.Date;

public class User {

    private int id;
    private String username;
    private String email;
    private String password;
    private String role;         // Rôle de l'utilisateur (ex : admin, user)
    private String isActive;     // Statut actif de l'utilisateur ("ok" ou "nok")
    private int numTel;       // Numéro de téléphone
    private Date dateDeNaissance; // Date de naissance
    private  User loggedInUser;

    public User(int id,String username, String email, String password, String role, String isActive, int numTel, Date dateDeNaissance) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.numTel = numTel;
        this.dateDeNaissance = dateDeNaissance;
    }

    public User(String username, String email, String password, String role, String isActive, int numTel, Date dateDeNaissance) {

        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.numTel = numTel;
        this.dateDeNaissance = dateDeNaissance;
    }
    public User() {

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

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public int getNumTel() {
        return numTel;
    }

    public void setNumTel(int numTel) {
        this.numTel = numTel;
    }

    public Date getDateDeNaissance() {
        return dateDeNaissance;
    }

    public void setDateDeNaissance(Date dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }

    @Override
    public String toString() {
        return "User{username='" + username + "', email='" + email + "', role='" + role +
                "', isActive='" + isActive + "', numTel='" + numTel +
                "', dateDeNaissance=" + dateDeNaissance + "}";
    }
}
