package utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DataBaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/eventus";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection con;
    public DataBaseConnection(){
        try{
            con = DriverManager.getConnection(URL,USER,PASSWORD);
            System.out.println("Connected to Data Base");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }
    public static DataBaseConnection getDatabaseConnection(){
        if(con == null){
            new DataBaseConnection();
        }
        return new DataBaseConnection();
    }

    public static Connection getConnection(){
        return con;
    }

    public void closeConnection() throws SQLException {
        con.close();
    }
}
