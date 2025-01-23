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

public class GenderCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public GenderCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        File idsFile = new File(plugin.getDataFolder(), "ids.yml");
        FileConfiguration idsConfig = YamlConfiguration.loadConfiguration(idsFile);

        if (args.length != 1) {
            player.sendMessage(getMessage("gender.usage", "Usage: /gender <male|female>"));
            return false;
        }

        String gender = args[0].toLowerCase();

        if (!isValidGender(gender)) {
            player.sendMessage(getMessage("gender.invalid", "Invalid gender! Use: male or female."));
            return false;
        }

        String formattedGender = formatGender(gender);

        String uuid = player.getUniqueId().toString();
        idsConfig.set(uuid + ".gender", formattedGender);

        try {
            idsConfig.save(idsFile);
            String message = getMessage("gender.changed", "Your gender has been updated to %gender%.")
                    .replace("%gender%", formattedGender);
            player.sendMessage(message);
        } catch (IOException e) {
            player.sendMessage(getMessage("gender.error", "Error saving the gender."));
            e.printStackTrace();
        }

        return true;
    }

    private boolean isValidGender(String gender) {
        return gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("Female");
    }

    private String formatGender(String gender) {
        if (gender.equalsIgnoreCase("male")) {
            return "Male";
        } else if (gender.equalsIgnoreCase("female")) {
            return "Female";
        }
        return gender;
    }

    private String getMessage(String path, String defaultMessage) {
        String message = config.getString(path, defaultMessage);
        return applyHexColors(message);
    }
    
    private String applyHexColors(String message) {
        return message.replaceAll("(?i)#([a-f0-9]{6})", "§x§$1");
    }
}
