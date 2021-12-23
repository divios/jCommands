import io.github.divios.jcommands.JCommand;
import io.github.divios.jcommands.JCommands;
import io.github.divios.jcommands.arguments.types.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JCommands.class, Bukkit.class})
public class testCommands {

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(JCommands.class);
        JavaPlugin plugin = mock(JavaPlugin.class);

        JCommands.register(plugin);
    }

    @Test
    public void testNotNull() {
        /*JCommand.create("test")
                .assertPermission("custom.permission")
                .withAliases("tx", "txt")
                .withArguments(new StringArgument(), new StringArgument())
                .assertUsage("<txt>")
                .assertRequirements(ServerOperator::isOp)
                .register(); */
    }

}
