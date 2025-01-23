package org.plugin.rProfile.TabCompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class LanguageTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {

            String[] languages = {"English", "Русский", "Українська"};
            for (String language : languages) {
                if (language.toLowerCase().startsWith(args[0].toLowerCase())) {
                    suggestions.add(language);
                }
            }
        }
        return suggestions;
    }
}
