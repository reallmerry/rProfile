package org.plugin.rProfile;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final String currentVersion;

    public static final String RESET = "\033[0m";  // Сброс
    public static final String AQUA = "\033[36m";  // Aqua (Циан)
    public static final String GREEN = "\033[32m"; // Зеленый
    public static final String YELLOW = "\033[33m"; // Желтый
    public static final String RED = "\033[31m";    // Красный

    public UpdateChecker(JavaPlugin plugin, String currentVersion) {
        this.plugin = plugin;
        this.currentVersion = currentVersion;
    }

    public void checkForUpdates() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    // URL к файлу с версией на GitHub
                    URL url = new URL("https://raw.githubusercontent.com/reallmerry/rProfile/main/update.txt");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String latestVersion = reader.readLine().trim();

                    plugin.getLogger().info(GREEN + "Getting a new version from github: " + latestVersion + RESET);

                    if (!currentVersion.equals(latestVersion)) {
                        notifyUpdate(latestVersion);
                    } else {
                        plugin.getLogger().info(GREEN + "The plugin has been updated to the latest version!" + RESET);
                    }

                } catch (Exception e) {
                    plugin.getLogger().info(RED + "Failed to check for updates: " + e.getMessage() + RESET);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private void notifyUpdate(String latestVersion) {
        plugin.getLogger().info(GREEN + "===================================================");
        plugin.getLogger().info(YELLOW + "> Plugin update available!");
        plugin.getLogger().info(YELLOW + ">    Your version: " + RED + currentVersion + RESET);
        plugin.getLogger().info(YELLOW + "> New version: " + GREEN + latestVersion + RESET);
        plugin.getLogger().info(YELLOW + ">");
        plugin.getLogger().info(YELLOW + "> Please download the new version!");
        plugin.getLogger().info(YELLOW + ">");

        plugin.getLogger().info(AQUA + "> Download (Modrinth)" + RESET);
        plugin.getLogger().info("https://modrinth.com/plugin/rprofiles");

        plugin.getLogger().info(AQUA + "> Download (Github)" + RESET);
        plugin.getLogger().info("https://github.com/reallmerry/rProfile/releases");

        plugin.getLogger().info(GREEN + "===================================================");
    }
}
