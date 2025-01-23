package org.plugin.rProfile.TabCompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class GenderTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            String[] genders = {"Male", "Female"};
            for (String gender : genders) {
                if (gender.toLowerCase().startsWith(args[0].toLowerCase())) {
                    suggestions.add(gender);
                }
            }
        }
        return suggestions;
    }
}
