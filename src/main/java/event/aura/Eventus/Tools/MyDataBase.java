package event.aura.Eventus.Tools;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    public final String DATABASE = "eventus";
    public final String URL = "jdbc:mysql://localhost:3306/" + DATABASE;
    public final String USER = "root";
    public final String PASS = "";
    private Connection connection;
    private static MyDataBase myDataBase;

    private MyDataBase() {
        try {
            connection = DriverManager.getConnection(URL,USER,PASS);
            System.out.println("Connection established");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static MyDataBase getMyDataBase() {
        if(myDataBase == null) {
            return new MyDataBase();
        }
        return myDataBase;
    }

    public Connection getConnection() {
        return connection;
    }
}