package org.plugin.rProfile.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class DataManager {
    private final JavaPlugin plugin;
    private Connection connection;

    public DataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        connectToDatabase();
        createProfilesTable();
    }

    private void connectToDatabase() {
        try {
            // Указываем путь к базе данных
            File dbFile = new File(plugin.getDataFolder(), "rProfile.db");

            // Проверяем, существует ли файл базы данных, если нет - создаем его
            if (!dbFile.exists()) {
                dbFile.createNewFile(); // Создаем файл базы данных
                plugin.getLogger().info("SQLite database file created at: " + dbFile.getAbsolutePath());
            }

            // Подключаемся к базе данных SQLite
            String dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(dbUrl);
            plugin.getLogger().info("Successfully connected to SQLite database.");

        } catch (SQLException | IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to SQLite database.", e);
        }
    }

    private void createProfilesTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS profiles (
                playername TEXT NOT NULL,
                id TEXT PRIMARY KEY,
                gender TEXT DEFAULT 'Unspecified',
                language TEXT DEFAULT 'Unselected',
                RegistrationDate TEXT NOT NULL
            );
        """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create 'profiles' table.", e);
        }
    }

    public void addPlayerIfNotExists(String playerName, String uuid) {
        String checkQuery = "SELECT * FROM profiles WHERE id = ?";
        String insertQuery = """
            INSERT INTO profiles (playername, id, RegistrationDate)
            VALUES (?, ?, ?);
        """;

        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            checkStmt.setString(1, uuid);
            ResultSet resultSet = checkStmt.executeQuery();

            if (!resultSet.next()) {
                String registrationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                insertStmt.setString(1, playerName);
                insertStmt.setString(2, uuid);
                insertStmt.setString(3, registrationDate);
                insertStmt.executeUpdate();

                plugin.getLogger().info("Added new player to database: " + playerName);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to add player to database.", e);
        }
    }

    public void migrateFromIdsFile() {
        File idsFile = new File(plugin.getDataFolder(), "ids.yml");

        if (!idsFile.exists()) {
            plugin.getLogger().warning("No 'ids.yml' file found for migration.");
            return;
        }

        FileConfiguration idsConfig = YamlConfiguration.loadConfiguration(idsFile);

        for (String uuid : idsConfig.getKeys(false)) {
            String playerName = idsConfig.getString(uuid + ".name");
            String id = idsConfig.getString(uuid + ".id");
            String registrationDate = idsConfig.getString(uuid + ".registrationDate");
            String gender = idsConfig.getString(uuid + ".gender", "Unspecified");
            String language = idsConfig.getString(uuid + ".language", "Unselected");

            if (playerName != null && id != null && registrationDate != null) {
                String insertQuery = """
                    INSERT OR IGNORE INTO profiles (playername, id, gender, language, RegistrationDate)
                    VALUES (?, ?, ?, ?, ?);
                """;

                try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                    statement.setString(1, playerName);
                    statement.setString(2, id);
                    statement.setString(3, gender);
                    statement.setString(4, language);
                    statement.setString(5, registrationDate);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to migrate player data for " + playerName, e);
                }
            }
        }

        plugin.getLogger().info("Migration from 'ids.yml' to database completed.");
    }

    public boolean updateLanguage(String uuid, String language) {
        String updateQuery = "UPDATE profiles SET language = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, language);
            statement.setString(2, uuid);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update language for UUID: " + uuid, e);
            return false;
        }
    }

    public boolean updateGender(String uuid, String gender) {
        String updateQuery = "UPDATE profiles SET gender = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, gender);
            statement.setString(2, uuid);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update gender for UUID: " + uuid, e);
            return false;
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                plugin.getLogger().info("Database connection closed.");
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to close database connection.", e);
            }
        }
    }
}