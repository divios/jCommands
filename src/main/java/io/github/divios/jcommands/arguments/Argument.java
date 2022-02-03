package io.github.divios.jcommands.arguments;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface Argument {

    String getName();
    boolean isValidArgument(String o);
    default List<String> getSuggestions() { return getSuggestions(null); }
    List<String> getSuggestions(CommandSender sender);

}
