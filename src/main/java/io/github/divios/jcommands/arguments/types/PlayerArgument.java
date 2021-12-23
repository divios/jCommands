package io.github.divios.jcommands.arguments.types;

import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.utils.Primitives;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PlayerArgument extends abstractArgument<Player> {

    public PlayerArgument() {
        super(() -> Bukkit.getOnlinePlayers().stream()
                .map(Player::getDisplayName)
                .collect(Collectors.toList()));
    }

    @Override
    public boolean isValidArgument(String s) {
        return Primitives.isPlayer(s);
    }

    @Override
    public Argument overrideSuggestions(@NotNull Supplier<List<Player>> playerSuggestions) {
        super.setSuggestions(() -> playerSuggestions.get().stream()
                .map(Player::getDisplayName)
                .collect(Collectors.toList())
        );
        return this;
    }

}
