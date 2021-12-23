package io.github.divios.jcommands.arguments.types;

import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.utils.Primitives;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BooleanArgument extends abstractArgument<Boolean> {

    public BooleanArgument(Supplier<List<String>> suggestions) {
        super(suggestions);
    }

    @Override
    public boolean isValidArgument(String s) {
        return Primitives.isBoolean(s);
    }

    @Override
    public Argument overrideSuggestions(@NotNull Supplier<List<Boolean>> booleanSuggestions) {
        super.setSuggestions(() -> booleanSuggestions.get().stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));
        return this;
    }
}
