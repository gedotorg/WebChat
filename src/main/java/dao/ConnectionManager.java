package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final String URL = "jdbc:postgresql://localhost:3000/webapp";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "networksize2203";

    // Получение соединения с базой данных
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
