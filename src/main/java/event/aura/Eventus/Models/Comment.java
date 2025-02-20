package event.aura.Eventus.Models;

import java.util.Objects;

public class Comment {
    private int id_comment = -1; // Default to -1 to indicate unassigned
    private int id_post;
    private User user;
    private String content;

    public Comment() {}

    public Comment(int id_comment, int id_post, User user, String content) {
        this.id_comment = id_comment;
        this.id_post = id_post;
        this.user = user;
        this.content = content;
    }

    public Comment(int idPost, User user, String content) {
        this.id_post = idPost;
        this.user = user;
        this.content = content;
    }

    public int getId_comment() { return id_comment; }
    public void setId_comment(int id_comment) { this.id_comment = id_comment; }

    public int getId_post() { return id_post; }
    public void setId_post(int id_post) { this.id_post = id_post; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @Override
    public String toString() {
        return "Comment{" +
                "id_comment=" + id_comment +
                ", id_post=" + id_post +
                ", user=" + (user != null ? user.getId_user() : "null") +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id_comment == comment.id_comment;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_comment);
    }
}
