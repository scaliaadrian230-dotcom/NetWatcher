package com.netwatcher.dao;

import java.sql.*;

public final class Database {
    private static final String URL = "jdbc:sqlite:netwatcher.db";
    private Database() { }
    public static Connection connect() throws SQLException { Connection c=DriverManager.getConnection(URL); try (Statement s=c.createStatement()) { s.execute("PRAGMA foreign_keys = ON"); } return c; }
    public static void initialize() throws SQLException {
        try (Connection c=connect(); Statement s=c.createStatement()) {
            s.executeUpdate("PRAGMA foreign_keys = ON");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS devices (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, ipAddress TEXT NOT NULL, macAddress TEXT NOT NULL, type TEXT NOT NULL, notes TEXT)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS networkLogs (id INTEGER PRIMARY KEY AUTOINCREMENT, deviceId INTEGER NOT NULL, date TEXT NOT NULL, issueType TEXT NOT NULL, resolution TEXT NOT NULL, severity INTEGER NOT NULL CHECK(severity BETWEEN 1 AND 5), FOREIGN KEY(deviceId) REFERENCES devices(id) ON DELETE CASCADE)");
        }
    }
}
