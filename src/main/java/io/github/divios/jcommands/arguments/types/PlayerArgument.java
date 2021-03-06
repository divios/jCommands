package io.github.divios.jcommands.arguments.types;

import io.github.divios.jcommands.util.Primitives;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PlayerArgument extends abstractArgument<Player> {

    public PlayerArgument(String name) {
        super(name, () -> Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList()));
        super.setAsImperative();
    }

    @Override
    protected boolean isValidArgumentAbstract(String s) {
        return Primitives.isPlayer(s);
    }

    @Override
    public abstractArgument<Player> overrideSuggestions(@NotNull Function<CommandSender, List<Player>> playerSuggestions, boolean imperative) {
        super.setSuggestions((sender) -> playerSuggestions.apply(sender).stream()
                .map(Player::getName)
                .collect(Collectors.toList())
        );
        super.imperative = imperative;
        return this;
    }

}
