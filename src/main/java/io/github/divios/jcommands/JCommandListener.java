package io.github.divios.jcommands;

import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.maptree.Node;
import io.github.divios.jcommands.maptree.TreeMap;
import io.github.divios.jcommands.utils.Value;
import io.github.divios.jcommands.utils.ValueMap;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class JCommandListener implements TabCompleter, CommandExecutor {

    private final TreeMap commandMap = new TreeMap();

    JCommandListener() {
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        System.out.println("Listener working");
        Node node = commandMap.search(command.getLabel(), Arrays.copyOfRange(args, 0, args.length - 1));  // do not include the
        if (node == null) return null;

        List<String> tabCompletes = new ArrayList<>();
        for (Node child : node.getChildren()) {
            if (!meetsCommandRequirements(sender, child.getCommand())) continue;

            for (String suggestion : child.getLabel().getSuggestions())
                if (suggestion.startsWith(args[args.length - 1]))
                    tabCompletes.add(suggestion);
        }

        return tabCompletes.isEmpty() ? null : tabCompletes;
    }

    private boolean meetsCommandRequirements(CommandSender sender, JCommand command) {
        return meetsPerms(sender, command) &&
                command.getRequirements().stream()
                        .allMatch(commandSenderPredicate -> commandSenderPredicate.test(sender));
    }

    private boolean meetsPerms(CommandSender sender, JCommand command) {
        if (command.getPermission() == null || command.getPermission().getName().isEmpty()) return true;
        return sender.hasPermission(command.getPermission());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Node node = commandMap.search(command.getLabel(), args);
        if (node == null) return false;
        if (!meetsCommandRequirements(sender, node.getCommand())) {
            node.getCommand().getInvalidPermissionAction().accept(sender);
            return false;
        }

        // TODO: get last valid parent and send usage

        if (sender instanceof Player)
            node.getCommand().getPlayerExecutor().accept((Player) sender, wrapArgs(node.getCommand(), args));
        else if (sender instanceof ConsoleCommandSender)
            node.getCommand().getConsoleExecutor().accept((ConsoleCommandSender) sender, wrapArgs(node.getCommand(), args));
        else
            node.getCommand().getDefaultExecutor().accept(sender, wrapArgs(node.getCommand(), args));

        return true;
    }

    private ValueMap wrapArgs(JCommand command, String[] args) {

        Map<String, Value> valueMap = new LinkedHashMap<>();
        List<Argument> arguments = command.getArguments();

        for (int i = 0; i < arguments.size(); i++) {
            valueMap.put(arguments.get(i).getName(), Value.ofString(args[i]));
        }

        return ValueMap.of(valueMap);
    }

}