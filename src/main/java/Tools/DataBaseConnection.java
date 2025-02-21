package Tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/eventus";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection con = null;

    // Constructeur privé pour empêcher l'instanciation directe
    public DataBaseConnection() {}

    // Méthode pour obtenir une connexion unique
    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connected to Database");
            }
        } catch (SQLException e) {
            System.out.println("❌ Database Connection Error: " + e.getMessage());
        }
        return con;
    }

    // Méthode pour fermer la connexion proprement
    public static void closeConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("🔴 Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error closing connection: " + e.getMessage());
        }
    }
}
