package org.plugin.rProfile;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.plugin.rProfile.TabCompleter.LanguageTabCompleter;
import org.plugin.rProfile.TabCompleter.GenderTabCompleter;
import org.plugin.rProfile.commands.*;
import org.plugin.rProfile.bukkit.Metrics;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;

import java.io.File;
import java.io.IOException;

public final class RProfile extends JavaPlugin {

    private LuckPerms luckPerms;
    private FileConfiguration config;
    private MiniMessage miniMessage;

    @Override
    public void onEnable() {
        String green = "\u001B[32m";
        String red = "\u001B[31m";
        String reset = "\u001B[0m";

        // Load bStats
        int pluginId = 24425;
        Metrics metrics = new Metrics(this, pluginId);
        getLogger().info(green + "bStats loaded successfully." + reset);

        miniMessage = MiniMessage.miniMessage();

        initializeLuckPerms();

        reloadPluginConfig();

        registerCommands();

        checkAndCreateFiles();

        String currentVersion = getDescription().getVersion();
        UpdateChecker updateChecker = new UpdateChecker(this, currentVersion);
        updateChecker.checkForUpdates();

        displayAsciiArt();

        getLogger().info(green + "The plugin has been successfully enabled!" + reset);
    }

    private void checkAndCreateFiles() {
        File pluginFolder = getDataFolder();
        File idsFile = new File(pluginFolder, "ids.yml");
        File configFile = new File(pluginFolder, "config.yml");
        String green = "\u001B[32m";
        String red = "\u001B[31m";
        String reset = "\u001B[0m";

        if (!pluginFolder.exists() && pluginFolder.mkdirs()) {
            getLogger().info(green + "The plugin folder was created successfully.");
        }

        if (!idsFile.exists()) {
            try {
                if (idsFile.createNewFile()) {
                    getLogger().info(green + "The ids.yml file was created successfully.");
                }
            } catch (IOException e) {
                getLogger().severe(red + "Failed to create ids.yml file: " + e.getMessage());
            }
        } else {
            getLogger().info(green + "The ids.yml file is already present.");
        }

        if (!configFile.exists()) {
            saveDefaultConfig();
            getLogger().info(green + "The config.yml file was created successfully.");
        } else {
            getLogger().info(green + "The config.yml file is already present.");
        }
    }

    @Override
    public void onDisable() {
        String red = "\u001B[31m";
        String reset = "\u001B[0m";
        getLogger().info(red + "The plugin is shutting down." + reset);
    }

    public void reloadPluginConfig() {
        reloadConfig();
        config = getConfig();

        getLogger().info("Config reloaded successfully!");

        String reloadMessage = config.getString("reload.Message");
        if (reloadMessage != null) {
            getLogger().info("Reload message from config: " + reloadMessage);
        }
    }

    public FileConfiguration getPluginConfig() {
        return config;
    }

    private void initializeLuckPerms() {
        String green = "\u001B[32m";
        String red = "\u001B[31m";
        String reset = "\u001B[0m";
        try {
            luckPerms = LuckPermsProvider.get();
            if (luckPerms == null) {
                throw new IllegalStateException("LuckPerms instance is null!");
            }
        } catch (Exception e) {
            getLogger().severe(red + "LuckPerms initialization failed: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void registerCommands() {

        PluginCommand profileCommand = getCommand("profile");
        PlayerInfoCommand profileCommandExecutor = new PlayerInfoCommand(luckPerms, config, this);
        profileCommand.setExecutor(profileCommandExecutor);

        PluginCommand languageCommand = getCommand("language");
        LanguageCommand languageCommandExecutor = new LanguageCommand(this);
        languageCommand.setExecutor(languageCommandExecutor);
        languageCommand.setTabCompleter(new LanguageTabCompleter());

        PluginCommand genderCommand = getCommand("gender");
        GenderCommand genderCommandExecutor = new GenderCommand(this);
        genderCommand.setExecutor(genderCommandExecutor);
        genderCommand.setTabCompleter(new GenderTabCompleter());
    }


    private void displayAsciiArt() {
        String green = "\u001B[32m";
        String reset = "\u001B[0m";
        String asciiArt = green +
                "-                                                                             \n " +
                " _                            _ _                                   __  _____ \n" +
                "| |__  _   _   _ __ ___  __ _| | |_ __ ___   ___ _ __ _ __ _   _   / / |___ / \n" +
                "| '_ \\| | | | | '__/ _ \\/ _ | | | '_  _ \\ / _ \\ '__| '__| | | | / /    |_ \\ \n" +
                "| |_) | |_| | | | |  __/ (_| | | | | | | | |  __/ |  | |  | |_| | \\ \\   ___) |\n" +
                "|_.__/ \\__, | |_|  \\___|\\__,_|_|_|_| |_| |_|\\___|_|  |_|   \\__, |  \\_\\ |____/ \n" +
                "       |___/                                               |___/\n" +
                reset;
        getLogger().info(asciiArt);
    }

    public FileConfiguration getConfigFile() {
        return config;
    }
}