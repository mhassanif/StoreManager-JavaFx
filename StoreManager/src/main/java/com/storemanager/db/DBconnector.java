package com.storemanager.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnector {

    // JDBC URL, username, and password for the database
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=StoreManagerDB;encrypt=true;trustServerCertificate=true";
    private static final String USER = "storemanager";
    private static final String PASSWORD = "password";

    // Connection method to establish a connection to the database
    public static Connection getConnection() throws SQLException {
        try {
            // Load the JDBC driver (optional for newer versions of Java)
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Create and return the connection object
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Error loading SQL Server driver", e);
        }
    }

    // Test method to check the database connection
    public static void testConnection() {
        try (Connection conn = DBconnector.getConnection()) {
            if (conn != null) {
                System.out.println("Connected to the database successfully!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Unable to establish a connection to the database.");
        }
    }
}
