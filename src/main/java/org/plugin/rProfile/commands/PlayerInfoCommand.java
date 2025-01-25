package org.plugin.rProfile.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.clip.placeholderapi.PlaceholderAPI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class PlayerInfoCommand implements CommandExecutor {
    private final LuckPerms luckPerms;
    private final FileConfiguration config;
    private final JavaPlugin plugin;

    public PlayerInfoCommand(LuckPerms luckPerms, FileConfiguration config, JavaPlugin plugin) {
        this.luckPerms = luckPerms;
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        if (!config.getBoolean("profile.enabled", true)) {
            sender.sendMessage(miniMessage.deserialize(config.getString("profile.message", "The command is disabled")));
            return true;
        }

        if (!sender.hasPermission("rProfile.profile")) {
            sender.sendMessage(miniMessage.deserialize(config.getString("lang.profile", "You do not have permission to execute this command.")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(miniMessage.deserialize("Please use /profile <player's nickname>"));
            return false;
        }

        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(playerName);

        if (targetPlayer == null) {
            sender.sendMessage(miniMessage.deserialize("Player " + playerName + " is not found or is offline."));
            return true;
        }

        String uuid = targetPlayer.getUniqueId().toString();
        User user = luckPerms.getUserManager().getUser(targetPlayer.getUniqueId());

        File idsFile = new File(plugin.getDataFolder(), "ids.yml");
        FileConfiguration idsConfig = YamlConfiguration.loadConfiguration(idsFile);

        String playerId = idsConfig.getString(uuid + ".id", null);
        String registrationDate = idsConfig.getString(uuid + ".registrationDate", null);

        if (playerId == null || registrationDate == null) {
            playerId = generateUniqueID(idsConfig);
            registrationDate = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());

            idsConfig.set(uuid + ".id", playerId);
            idsConfig.set(uuid + ".registrationDate", registrationDate);

            try {
                idsConfig.save(idsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String prefix = "Data loading error. You may not have LuckPerms.";
        if (user != null) {
            QueryOptions queryOptions = luckPerms.getContextManager().getQueryOptions(targetPlayer);
            var userData = user.getCachedData().getMetaData(queryOptions);
            prefix = userData.getPrefix() != null ? userData.getPrefix() : "No prefix for the group";
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            prefix = PlaceholderAPI.setPlaceholders(targetPlayer, config.getString("profile.prefix", "%luckperms_prefix%"));
        }

        String language = idsConfig.getString(uuid + ".language", "Unselected");
        String gender = idsConfig.getString(uuid + ".gender", "Unspecified");

        sender.sendMessage(miniMessage.deserialize("<#FBB05B>—————————————————————————————"));
        sender.sendMessage(miniMessage.deserialize("<#FBB05B>Player: <#FFF499>" + targetPlayer.getName()));
        sender.sendMessage(miniMessage.deserialize("<#FBB05B>ID: <#FFF499>" + playerId));
        sender.sendMessage(miniMessage.deserialize("<#FBB05B>UUID: <#FFF499>" + uuid));
        sender.sendMessage(miniMessage.deserialize("<#FBB05B>Selected language: <#FFF499>" + language));
        sender.sendMessage(miniMessage.deserialize("<#FBB05B>Gender: <#FFF499>" + gender));
        sender.sendMessage(miniMessage.deserialize("<#FBB05B>Date of registration: <#FFF499>" + registrationDate));
        sender.sendMessage(miniMessage.deserialize("<#FBB05B>Group: " + prefix));
        sender.sendMessage(miniMessage.deserialize("<#FBB05B>—————————————————————————————"));

        return true;
    }

    private String generateUniqueID(FileConfiguration config) {
        Random random = new Random();
        String id;

        do {
            id = String.format("%06d", random.nextInt(1000000));
        } while (config.contains(id));

        return id;
    }
}
