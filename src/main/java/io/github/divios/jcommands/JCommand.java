package io.github.divios.jcommands;

import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.util.ValueMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class JCommand {

    private final String name;
    private boolean isSubcommand = false;
    private final List<String> aliases = new ArrayList<>();
    private String permission = null;
    private Consumer<CommandSender> invalidPermission = player -> {
    };
    private final LinkedList<Argument> arguments = new LinkedList<>();
    private final List<JCommand> subCommands = new ArrayList<>();
    private String usage;
    private final List<Predicate<CommandSender>> requirements = new ArrayList<>();
    private BiConsumer<CommandSender, ValueMap> defaultExecutor = (commandSender, args) -> {
    };
    private BiConsumer<Player, ValueMap> playerExecutor = (player, args) -> defaultExecutor.accept(player, args);
    private BiConsumer<ConsoleCommandSender, ValueMap> consoleExecutor = (sender, args) -> defaultExecutor.accept(sender, args);


    public static JCommand create(String name) {
        return new JCommand(name);
    }

    public JCommand(String name) {
        this.name = name;
        aliases.add(name);
    }

    private JCommand isSubcommand() {
        isSubcommand = true;
        return this;
    }

    public JCommand withAliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }
    public JCommand assertPermission(String permission) {
        return assertPermission(permission, sender -> {
        });
    }

    public JCommand assertPermission(String permission, Consumer<CommandSender> invalidPermission) {
        this.permission = permission;
        this.invalidPermission = invalidPermission;
        return this;
    }

    public JCommand withArguments(Collection<? extends Argument> arguments) {
        this.arguments.addAll(arguments);
        return this;
    }

    public JCommand withArguments(Argument... arguments) {
        return withArguments(Arrays.asList(arguments));
    }

    public JCommand withSubcommands(Collection<? extends JCommand> subCommands) {
        for (JCommand subCommand : subCommands) {
            this.subCommands.add(flatCommand(subCommand).isSubcommand());
        }
        return this;
    }

    public JCommand withSubcommands(JCommand... commands) {
        return withSubcommands(Arrays.asList(commands));
    }

    public JCommand assertUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public JCommand assertRequirements(Collection<Predicate<CommandSender>> requirements) {
        this.requirements.addAll(requirements);
        return this;
    }

    @SafeVarargs
    public final JCommand assertRequirements(Predicate<CommandSender>... requirements) {
        return assertRequirements(Arrays.asList(requirements));
    }

    public JCommand executesConsole(BiConsumer<ConsoleCommandSender, ValueMap> consoleExecutor) {
        this.consoleExecutor = consoleExecutor;
        return this;
    }

    public JCommand executesPlayer(BiConsumer<Player, ValueMap> playerExecutor) {
        this.playerExecutor = playerExecutor;
        return this;
    }

    public JCommand executes(BiConsumer<CommandSender, ValueMap> defaultExecutor) {
        this.defaultExecutor = defaultExecutor;
        return this;
    }

    public void register() {
        JCommands.registerCommand(this);
    }

    public String getName() {
        return name;
    }

    boolean isSubCommand() {
        return isSubcommand;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getPermission() {
        return permission;
    }

    public Consumer<CommandSender> getInvalidPermissionAction() {
        return invalidPermission == null ? sender -> {
        } : invalidPermission;
    }

    public List<Argument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public List<JCommand> getSubCommands() {
        return Collections.unmodifiableList(subCommands);
    }

    public String getUsage() {
        return usage;
    }

    public List<Predicate<CommandSender>> getRequirements() {
        return Collections.unmodifiableList(requirements);
    }

    public BiConsumer<Player, ValueMap> getPlayerExecutor() {
        return playerExecutor;
    }

    public BiConsumer<ConsoleCommandSender, ValueMap> getConsoleExecutor() {
        return consoleExecutor;
    }

    public BiConsumer<CommandSender, ValueMap> getDefaultExecutor() {
        return defaultExecutor;
    }


    /**
     * Adds the name of a subcommand as an argument.
     *
     * @param command The subcommand to execute this operation on.
     * @return the new subcommand with a new StringArgument.
     */
    private JCommand flatCommand(JCommand command) {
        command.requirements.addAll(this.requirements);         // Adds the requirements of the father
        /*command.arguments.addFirst(new StringArgument("commandName")
                .setAsImperative()
                .overrideSuggestions(() -> Collections.singletonList(command.name), true)
        ); */
        return command;
    }

    @Override
    public String toString() {
        return "JCommand{" +
                "name='" + name + '\'' +
                ", aliases=" + aliases +
                ", permission=" + permission +
                ", invalidPermission=" + invalidPermission +
                ", arguments=" + arguments +
                ", subCommands=" + subCommands +
                ", usage='" + usage + '\'' +
                ", requirements=" + requirements +
                ", playerExecutor=" + playerExecutor +
                ", consoleExecutor=" + consoleExecutor +
                ", defaultExecutor=" + defaultExecutor +
                '}';
    }

}
