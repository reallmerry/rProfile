package org.plugin.rProfile.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.plugin.rProfile.enums.Language;

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
        MiniMessage miniMessage = MiniMessage.miniMessage();

        if (!(sender instanceof Player)) {
            sender.sendMessage(miniMessage.deserialize(config.getString("language.usage")));
            return true;
        }

        Player player = (Player) sender;

        // Load ids.yml
        File idsFile = new File(plugin.getDataFolder(), "ids.yml");
        FileConfiguration idsConfig = YamlConfiguration.loadConfiguration(idsFile);

        // Check if the argument is provided
        if (args.length != 1) {
            player.sendMessage(miniMessage.deserialize(config.getString("language.usage")));
            return false;
        }

        String languageInput = args[0];
        Language language = Language.fromString(languageInput);

        if (language == null) {
            player.sendMessage(miniMessage.deserialize(config.getString("language.invalid")));
            return false;
        }

        String uuid = player.getUniqueId().toString();
        idsConfig.set(uuid + ".language", language.getLanguageName());

        try {
            idsConfig.save(idsFile);
            String successMessage = config.getString("language.changed").replace("%language%", language.getLanguageName());
            player.sendMessage(miniMessage.deserialize(successMessage));
        } catch (IOException e) {
            player.sendMessage(miniMessage.deserialize(config.getString("language.error")));
            e.printStackTrace();
        }

        return true;
    }
}