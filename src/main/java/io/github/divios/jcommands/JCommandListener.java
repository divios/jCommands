package io.github.divios.jcommands;

import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.maptree.Node;
import io.github.divios.jcommands.maptree.TreeMap;
import io.github.divios.jcommands.util.Value;
import io.github.divios.jcommands.util.ValueMap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class JCommandListener implements TabCompleter, CommandExecutor {

    private final TreeMap commandMap = new TreeMap();

    JCommandListener() {
    }

    public void registerCommand(JCommand command) {
        commandMap.put(command);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        Node node = commandMap.search(command.getLabel(), Arrays.copyOfRange(args, 0, args.length - 1));  // do not include the last to get parent
        if (node == null) return null;

        List<String> tabCompletes = new ArrayList<>();
        for (Node child : node.getChildren()) {
            if (!meetsCommandRequirements(sender, child)) continue;

            for (String suggestion : child.getLabel().getSuggestions(sender))
                if (StringUtils.startsWithIgnoreCase(suggestion, (args[args.length - 1])))
                    tabCompletes.add(suggestion);
        }

        return tabCompletes;
    }

    private boolean meetsCommandRequirements(CommandSender sender, Node node) {
        return meetsPerms(sender, node) &&
                node.getCommand().getRequirements().stream()
                        .allMatch(commandSenderPredicate -> commandSenderPredicate.test(sender));
    }

    private boolean meetsPerms(CommandSender sender, Node node) {
        if (node.getPermissions() == null || node.getPermissions().isEmpty()) return true;
        return node.getPermissions().stream().filter(Objects::nonNull).allMatch(sender::hasPermission);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Node node = commandMap.search(command.getLabel(), args);
        if (node == null) return false;

        if (!meetsCommandRequirements(sender, node)) {
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

        String[] validArgs = Arrays.copyOfRange(args, ArrayUtils.indexOf(args, command.getName()) + 1, args.length);

        for (int i = 0; i < arguments.size(); i++)
            valueMap.put(arguments.get(i).getName(), Value.ofString(validArgs[i]));

        return ValueMap.of(valueMap);
    }

}