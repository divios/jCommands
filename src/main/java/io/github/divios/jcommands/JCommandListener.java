package io.github.divios.jcommands;

import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.utils.CommandMapUtil;
import io.github.divios.jcommands.utils.Value;
import io.github.divios.jcommands.utils.ValueMap;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

class JCommandListener implements TabCompleter, CommandExecutor {

    private final JCommand command;

    JCommandListener(JavaPlugin plugin, JCommand command) {
        this.command = command;


        CommandMapUtil.registerCommand(plugin, this, command.getAliases());
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return callTabComplete(sender, this.command, args);
    }

    private List<String> callTabComplete(CommandSender sender, JCommand command, String[] args) {
        if (command.getPermission() != null
                && !command.getPermission().getName().isEmpty()
                && !sender.hasPermission(command.getPermission())) return null;
        if (!command.getRequirements().stream().allMatch(commandSenderPredicate -> commandSenderPredicate.test(sender)))
            return null;

        List<Argument> arguments;
        List<JCommand> subCommands = null;

        int pos = args.length - 1;
        if (pos >= (arguments = command.getArguments()).size() && (subCommands = command.getSubCommands()).isEmpty())
            return null;       // First check

        if (pos < arguments.size()) {
            return getFilteredTabComplete(arguments.get(pos).getSuggestions(), args[pos]);
        } else {
            List<String> toComplete = new ArrayList<>();
            for (JCommand subCommand : subCommands) {

                boolean isValid = true;
                for (int i = arguments.size(); i < args.length - 1; i++) {          // Check if previous arguments of subcommand are valid
                    if (i >= subCommand.getArguments().size()) break;               // Break if input have more arguments than necessary

                    Argument argument;
                    if (!((argument = subCommand.getArguments().get(i - arguments.size())) != null
                            && argument.isValidArgument(args[i]))) {
                        isValid = false;
                        break;
                    }
                }
                if (!isValid) continue;

                List<String> aux;
                if ((aux = callTabComplete(sender, subCommand, Arrays.copyOfRange(args, arguments.size(), args.length))) != null)
                    toComplete.addAll(aux);
            }
            return getFilteredTabComplete(toComplete, args[pos]);
        }
    }

    private List<String> getFilteredTabComplete(List<String> tabCompletes, String filter) {
        return tabCompletes == null ? null : tabCompletes.stream()
                .filter(s -> s.toLowerCase().startsWith(filter.toLowerCase()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return callCommand(sender, this.command, args);
    }

    private boolean callCommand(CommandSender sender, JCommand command, String[] args) {

        if (command.getPermission() != null
                && !command.getPermission().getName().isEmpty()
                && !sender.hasPermission(command.getPermission())) {    // Permissions check
            command.getInvalidPermissionAction().accept(sender);
            return false;
        }

        if (!command.getRequirements().stream().allMatch(senderPredicate -> senderPredicate.test(sender))) {  // Requirements check
            return false;
        }

        if ((args.length < command.getArguments().size())) {     // First argument check
            if (command.getUsage() != null) sender.sendMessage(command.getUsage());
            return false;
        }

        for (int i = 0; i < command.getArguments().size(); i++) {  // Check valid arguments
            if (!command.getArguments().get(i).isValidArgument(args[i])) {
                if (command.getUsage() != null) sender.sendMessage(command.getUsage());
                return false;
            }
        }

        if (args.length == command.getArguments().size()) {   // If is the command we are good to go
            if (sender instanceof Player)
                command.getPlayerExecutor().accept((Player) sender, wrapArgs(command, args));
            else if (sender instanceof ConsoleCommandSender)
                command.getConsoleExecutor().accept((ConsoleCommandSender) sender, wrapArgs(command, args));
            else
                command.getDefaultExecutor().accept(sender, wrapArgs(command, args));
            return true;
        } else {                                    // Check if the call can be passed to subCommand
            String[] newArgs = Arrays.copyOfRange(args, command.getArguments().size(), args.length);
            boolean result = false;
            for (JCommand subCommand : command.getSubCommands()) {
                if (newArgs.length < subCommand.getArguments().size()) continue;

                Argument argument;
                if ((argument = subCommand.getArguments().get(0)) != null && argument.isValidArgument(newArgs[0]))
                    result |= callCommand(sender, subCommand, newArgs);
            }
            return result;
        }

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