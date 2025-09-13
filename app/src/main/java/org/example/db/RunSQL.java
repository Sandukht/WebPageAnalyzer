package com.example.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RunSQL {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void runSQL() throws SQLException, IOException {
        try (InputStream is = RunSQL.class.getResourceAsStream("/db/schema.sql")) {
            if (is == null) {
                System.err.println("Cannot find schema.sql in resources!");
            }

            String sql = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            String[] statements = sql.split(";");


            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement()) {
                    for(String statement : statements) {
                        if(!statement.trim().isEmpty()) {
                            stmt.execute(statement);
                        }
                    }
                stmt.execute(sql);
                System.out.println("Database and tables created!");
            }

        } catch (SQLException | IOException e) {
        System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }
}
