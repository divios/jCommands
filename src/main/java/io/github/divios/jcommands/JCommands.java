package io.github.divios.jcommands;

import com.google.common.base.Preconditions;
import io.github.divios.jcommands.arguments.types.BooleanArgument;
import io.github.divios.jcommands.arguments.types.PlayerArgument;
import io.github.divios.jcommands.arguments.types.StringArgument;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@SuppressWarnings("unused")
public final class JCommands extends JavaPlugin{

    private static JavaPlugin providerPlugin;

    public static void register(JavaPlugin providerPlugin) {
        JCommands.providerPlugin = providerPlugin;
    }

    static void registerCommand(JCommand command) {
        Preconditions.checkNotNull(providerPlugin, "No provider plugin was registered on load");
        new JCommandListener(providerPlugin, command);
    }

    @Override
    public void onEnable() {
        register(this);

        JCommand.create("jcommand")
                .withAliases("jcmd", "jc")
                .assertPermission("jcommands.admin")
                .assertUsage("<nothing>")
                .withArguments(new BooleanArgument())
                .withSubcommands(JCommand.create("spawn")
                        .withArguments(new PlayerArgument())
                        .assertUsage("<player>")
                        .executesPlayer((player, values) -> values.get(1).getAsPlayer().sendMessage("spawn was executed"))
                )
                .withSubcommands(JCommand.create("open")
                        .assertUsage("<shop>")
                        .withArguments(new StringArgument()
                                .overrideSuggestions(() -> Arrays.asList("drops", "block", "equipment"), true)
                        )
                        .executesPlayer((player, values) -> player.sendMessage(values.get(0).getAsString()))
                )
                .executesPlayer((player, values) -> {
                    player.sendMessage("default was executed");
                })
                .register();

    }

}
