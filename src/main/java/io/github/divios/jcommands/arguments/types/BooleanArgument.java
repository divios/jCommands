package io.github.divios.jcommands.arguments.types;

import io.github.divios.jcommands.util.Primitives;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BooleanArgument extends abstractArgument<Boolean> {

    public BooleanArgument(String name) {
        super(name, () -> Arrays.asList("true", "false"));
    }

    @Override
    protected boolean isValidArgumentAbstract(String s) {
        return Primitives.isBoolean(s);
    }

    @Override
    public abstractArgument<Boolean> overrideSuggestions(@NotNull Function<CommandSender, List<Boolean>> booleanSuggestions, boolean imperative) {
        super.setSuggestions((sender) -> booleanSuggestions.apply(sender).stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));
        super.imperative = imperative;
        return this;
    }
}
