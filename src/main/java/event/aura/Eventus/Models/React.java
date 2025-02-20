package event.aura.Eventus.Models;



public class React {
    private int id_react;
    private int id_post;
    private User user;
    private String reaction; // "like" or "dislike"

    public React(Post article, User currentUser, String like) {}

    public React(int id_react, int id_post, User user, String reaction) {
        this.id_react = id_react;
        this.id_post = id_post;
        this.user = user;
        this.reaction = reaction;
    }

    public React(int idPost, User user, String like) {

        this.id_post = idPost;
        this.user = user;
        this.reaction = like;

    }

    public int getId_react() { return id_react; }
    public void setId_react(int id_react) { this.id_react = id_react; }

    public int getId_post() { return id_post; }
    public void setId_post(int id_post) { this.id_post = id_post; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getReaction() { return reaction; }
    public void setReaction(String reaction) { this.reaction = reaction; }

    @Override
    public String toString() {
        return "React{" +
                "id_react=" + id_react +
                ", id_post=" + id_post +
                ", user=" + (user != null ? user.getId_user() : "null") +
                ", reaction='" + reaction + '\'' +
                '}';
    }
}

