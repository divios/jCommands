package io.github.divios.jcommands;

import com.google.common.base.Preconditions;
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

        JCommand.create("test")
                .withAliases("txt", "tx")
                .withArguments(new StringArgument().overrideSuggestions(() -> Arrays.asList("oke", "things")))
                .register();

    }

}
