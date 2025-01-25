package org.plugin.rProfile.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.plugin.rProfile.enums.Gender;

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
        MiniMessage miniMessage = MiniMessage.miniMessage();

        if (!(sender instanceof Player)) {
            sender.sendMessage(miniMessage.deserialize("This command can only be used by players."));
            return true;
        }

        Player player = (Player) sender;

        File idsFile = new File(plugin.getDataFolder(), "ids.yml");
        FileConfiguration idsConfig = YamlConfiguration.loadConfiguration(idsFile);

        if (args.length != 1) {
            player.sendMessage(miniMessage.deserialize(getMessage("gender.usage", "Usage: /gender <male|female>")));
            return false;
        }

        String genderInput = args[0].toLowerCase();

        Gender gender = Gender.fromString(genderInput);
        if (gender == null) {
            player.sendMessage(miniMessage.deserialize(getMessage("gender.invalid", "Invalid gender! Use: male or female.")));
            return false;
        }

        String uuid = player.getUniqueId().toString();
        idsConfig.set(uuid + ".gender", gender.getGenderName());

        try {
            idsConfig.save(idsFile);
            String message = getMessage("gender.changed", "Your gender has been updated to %gender%.")
                    .replace("%gender%", gender.getGenderName());
            player.sendMessage(miniMessage.deserialize(message));
        } catch (IOException e) {
            player.sendMessage(miniMessage.deserialize(getMessage("gender.error", "Error saving the gender.")));
            e.printStackTrace();
        }

        return true;
    }

    private String getMessage(String path, String defaultMessage) {
        String message = config.getString(path, defaultMessage);
        return applyHexColors(message);
    }

    private String applyHexColors(String message) {
        return message.replaceAll("(?i)#([a-f0-9]{6})", "§x§$1");
    }
}