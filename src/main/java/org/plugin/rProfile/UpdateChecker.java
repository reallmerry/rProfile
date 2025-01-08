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

    // ANSI escape codes для цветов
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
                    String latestVersion = reader.readLine().trim();  // Чтение версии из файла

                    // Логируем полученную версию
                    plugin.getLogger().info(GREEN + "Получена версия из файла: " + latestVersion + RESET);

                    // Сравниваем текущую версию с последней
                    if (!currentVersion.equals(latestVersion)) {
                        // Если версии не совпадают, уведомляем о доступности обновления
                        notifyUpdate(latestVersion);
                    } else {
                        plugin.getLogger().info(GREEN + "Плагин обновлен до последней версии!" + RESET);
                    }

                } catch (Exception e) {
                    plugin.getLogger().info(RED + "Не удалось проверить обновления: " + e.getMessage() + RESET);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private void notifyUpdate(String latestVersion) {
        plugin.getLogger().info(GREEN + "===================================================");
        plugin.getLogger().info(YELLOW + "> Доступно обновление плагина!");
        plugin.getLogger().info(YELLOW + ">    Ваша версия: " + RED + currentVersion + RESET);
        plugin.getLogger().info(YELLOW + "> Новая версия: " + GREEN + latestVersion + RESET);
        plugin.getLogger().info(YELLOW + ">");
        plugin.getLogger().info(YELLOW + "> Пожалуйста, скачайте новую версию!");
        plugin.getLogger().info(YELLOW + ">");

        // Формат для ссылок, как вы просили
        plugin.getLogger().info(AQUA + "> Скачать (Modrinth)" + RESET);
        plugin.getLogger().info("https://modrinth.com/plugin/rprofiles");

        plugin.getLogger().info(AQUA + "> Скачать (Github)" + RESET);
        plugin.getLogger().info("https://github.com/reallmerry/rProfile/releases");

        plugin.getLogger().info(GREEN + "===================================================");
    }
}