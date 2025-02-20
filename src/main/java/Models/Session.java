package Models;

public class Session {
    private static Session instance;
    private User user;

    private Session(User user) {
        this.user = user;
    }

    public static void setUser(User user) {
        if (instance == null) {
            instance = new Session(user);
        }
    }

    public static User getUser() {
        return instance != null ? instance.user : null;
    }

    public static void clear() {
        instance = null;
    }
    public static void afficherSession() {
        User user = Session.getUser();
        if (user != null) {
            System.out.println("Utilisateur connecté : ");
            System.out.println("ID : " + user.getId());
            System.out.println("Nom d'utilisateur : " + user.getUsername());
            System.out.println("Email : " + user.getEmail());
            System.out.println("Rôle : " + user.getRole());
            System.out.println("Statut : " + user.getIsActive());
            System.out.println("Numéro de téléphone : " + user.getNumTel());
            System.out.println("Date de naissance : " + user.getDateDeNaissance());
        } else {
            System.out.println("Aucun utilisateur connecté.");
        }
    }

}

