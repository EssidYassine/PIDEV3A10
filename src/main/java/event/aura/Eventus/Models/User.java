package event.aura.Eventus.Models;



public class User {
    private int id_user;
    private String username;
    private String email;
    private String password;

    public User() {}

    public User(int id_user, String username, String email, String password) {
        this.id_user = id_user;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getId_user() { return id_user; }
    public void setId_user(int id_user) { this.id_user = id_user; }

    public String getUsername() { return username; }
    public void setUsername(int username) { this.username = String.valueOf(username); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "User{" +
                "id_user=" + id_user +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public int getId() {
        return id_user;
    }
}
