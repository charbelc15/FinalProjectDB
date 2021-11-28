package com.example.finalprojectdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQL_Connector {
    private static final String OURURL= "jdbc:mysql://localhost:3307/tutoringservice";
    private static final String USERNAME="root";
    private static final String PASSWORD="root";
    private static final Logger logger = Logger.getLogger(SQL_Connector.class.getName());
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";


    public static Connection getDBConnection() throws SQLException {
        Connection connection = null;

        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException exception) {
            logger.log(Level.SEVERE, exception.getMessage());
        }

        try {
            connection = DriverManager.getConnection(OURURL, USERNAME, PASSWORD);
            return connection;
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, exception.getMessage());
            return connection;
        }
    }
    public static void closeConnection(Connection con , Statement stmt) throws SQLException {
        stmt.close();
        con.close();
        System.out.println("Connection is closed");
    }
}

