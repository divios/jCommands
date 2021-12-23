package io.github.divios.jcommands;

import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.utils.CommandMapUtil;
import io.github.divios.jcommands.utils.Value;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        if (!sender.hasPermission(command.getPermission())) return null;
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
                List<String> aux;
                if ((aux = callTabComplete(sender, subCommand, Arrays.copyOfRange(args, arguments.size() - 1, pos))) != null)
                    toComplete.addAll(aux);
            }
            return getFilteredTabComplete(toComplete, args[pos]);
        }
    }

    private List<String> getFilteredTabComplete(List<String> tabCompletes, String filter) {
        return tabCompletes.stream()
                .filter(s -> s.startsWith(filter))
                .collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return callCommand(sender, this.command, args);
    }

    private boolean callCommand(CommandSender sender, JCommand command, String[] args) {

        if (!sender.hasPermission(command.getPermission())) {    // Permissions check
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

        if (command.getSubCommands().isEmpty()) {   // If not subcommands we are good to go
            if (sender instanceof Player) command.getPlayerExecutor().accept((Player) sender, castArgs(args));
            else if (sender instanceof ConsoleCommandSender)
                command.getConsoleExecutor().accept((ConsoleCommandSender) sender, castArgs(args));
            else command.getDefaultExecutor().accept(sender, castArgs(args));
            return true;
        } else {                                    // Check if the call can be passed to subCommand
            String[] newArgs = Arrays.copyOfRange(args, command.getArguments().size() - 1, args.length - 1);
            for (JCommand subCommand : command.getSubCommands()) {
                if (newArgs.length < subCommand.getArguments().size()) continue;

                boolean isValid = false;
                for (int i = 0; i < subCommand.getArguments().size(); i++) {
                    if (!subCommand.getArguments().get(i).isValidArgument(newArgs[i])) {
                        isValid = true;
                        break;
                    }
                }

                if (isValid) continue;
                return callCommand(sender, subCommand, newArgs);
            }
        }

        if (command.getUsage() != null) sender.sendMessage(command.getUsage());
        return false;
    }

    private List<Value> castArgs(String[] args) {
        return Arrays.stream(args).map(Value::ofString).collect(Collectors.toList());
    }

}