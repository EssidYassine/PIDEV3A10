package event.aura.Eventus.Services;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author WINDOWS 10
 */
public class DBConnection {

    public static Connection connectDB() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/Eventus", "root", "");
            return connect;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static Connection connect = null;

    public static Connection getInstance() {
        if (connect == null) {
            connect = connectDB();
        }
        return connect;
    }


}
