package org.plugin.rProfile.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class LanguageCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public LanguageCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(config.getString("language.usage"));
            return true;
        }

        Player player = (Player) sender;

        // Load ids.yml
        File idsFile = new File(plugin.getDataFolder(), "ids.yml");
        FileConfiguration idsConfig = YamlConfiguration.loadConfiguration(idsFile);

        // Check if the argument is provided
        if (args.length != 1) {
            player.sendMessage(config.getString("language.usage"));
            return false;
        }

        String language = capitalizeFirstLetter(args[0].toLowerCase());

        if (!isValidLanguage(language)) {
            player.sendMessage(config.getString("language.invalid"));
            return false;
        }

        String uuid = player.getUniqueId().toString();
        idsConfig.set(uuid + ".language", language);

        try {
            idsConfig.save(idsFile);
            String successMessage = config.getString("language.changed").replace("%language%", language);
            player.sendMessage(successMessage);
        } catch (IOException e) {
            player.sendMessage(config.getString("language.error"));
            e.printStackTrace();
        }

        return true;
    }

    private boolean isValidLanguage(String language) {
        for (String validLanguage : getValidLanguages()) {
            if (validLanguage.equalsIgnoreCase(language)) {
                return true;
            }
        }
        return false;
    }

    private String[] getValidLanguages() {
        return new String[]{"Русский", "English", "Українська"};
    }

    private String capitalizeFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}