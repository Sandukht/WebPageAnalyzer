package com.example.db;

import java.sql.*;

public class DBManager {
    private static final String URL = "jdbc:mysql://localhost:3306/webpage_analyzer";
    private static final String USER = "root";         
    private static final String PASSWORD = "root";     

    public static void insertWordStats(String pageUrl, String word, int count, String lang) {
        String sql = "INSERT INTO words_statistics (url, word, count, lang) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pageUrl);
            stmt.setString(2, word);
            stmt.setInt(3, count);
            stmt.setString(4, lang);
            stmt.executeUpdate();

        } catch (SQLException e) {
            // Check for duplicate entry error code (MySQL 1062)
            if (e.getErrorCode() == 1062) {
                System.out.println("Skipping duplicate word '" + word + "' for URL " + pageUrl);
            } else {
                e.printStackTrace();
            }
        }
    }
}
