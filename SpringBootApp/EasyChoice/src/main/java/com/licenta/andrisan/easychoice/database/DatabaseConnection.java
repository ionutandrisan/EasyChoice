package com.licenta.andrisan.easychoice.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private Connection oracleConnection = null;

    public DatabaseConnection() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            String username = "system";
            String password = "root";
            String dbName = "jdbc:oracle:thin:@localhost:1521:xe";
            oracleConnection = DriverManager.getConnection(dbName, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DisconnectFromDatabase() throws SQLException {
        try {
            oracleConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getOracleConnection() {
        return oracleConnection;
    }

}
