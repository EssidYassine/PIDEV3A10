package event.aura.Eventus.Models;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Post {
    private int id_post;
    private String title;
    private String description;
    private String image;
    private int nb_Likes;
    private LocalDateTime date;
    private User id_user;

    // Constructor Fix: Removed redundant assignments and initialized nb_Likes
    public Post(String title, String description, String image, int nb_Likes, LocalDateTime date, User user) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.date = date;
        this.id_user = user;
        this.nb_Likes = 0; // Default to 0 since it's not passed as a parameter
    }

    public Post(String image, boolean b) {


        this.image = image;
        this.nb_Likes = 0;

    }

    // Getters & Setters
    public int getId_post() {
        return id_post;
    }

    public void setId_post(int id_post) {
        this.id_post = id_post;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getNb_Likes() {
        return nb_Likes;
    }

    public void setNb_Likes(int nb_Likes) {
        this.nb_Likes = nb_Likes;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getId_user() {
        return id_user;
    }

    public void setId_user(User id_user) {
        this.id_user = id_user;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id_post=" + id_post +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", nb_Likes=" + nb_Likes +
                ", date=" + date +
                ", user=" + (id_user != null ? id_user.getId() : "null") +
                '}';
    }
}
