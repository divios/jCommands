package io.github.divios.jcommands.arguments.types;

import io.github.divios.jcommands.util.Primitives;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IntegerArgument extends abstractArgument<Integer> {

    public IntegerArgument(String name) {
        super(name, ArrayList::new);
    }

    @Override
    protected boolean isValidArgumentAbstract(String s) {
        return Primitives.isInteger(s);
    }

    @Override
    public abstractArgument<Integer> overrideSuggestions(@NotNull Function<CommandSender, List<Integer>> integerSuggestions, boolean imperative) {
        super.setSuggestions((sender) -> integerSuggestions.apply(sender).stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));
        super.imperative = imperative;
        return this;
    }
}
