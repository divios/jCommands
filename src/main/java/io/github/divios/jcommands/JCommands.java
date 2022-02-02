package io.github.divios.jcommands;

import com.google.common.base.Preconditions;
import io.github.divios.jcommands.util.CommandMapUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class JCommands {

    private static JavaPlugin providerPlugin;
    private static JCommandListener listener = null;

    public static void register(@NotNull JavaPlugin providerPlugin) {
        JCommands.providerPlugin = providerPlugin;
        listener = new JCommandListener();
    }

    static void registerCommand(JCommand command) {
        Preconditions.checkNotNull(providerPlugin, "No provider plugin was registered on load");
        CommandMapUtil.registerCommand(providerPlugin, listener, command.getAliases());
        listener.registerCommand(command);
    }

}
