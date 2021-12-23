package io.github.divios.jcommands.arguments.types;

import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.utils.Primitives;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IntegerArgument extends abstractArgument<Integer> {

    public IntegerArgument() {
        super(() -> null);
    }

    @Override
    public boolean isValidArgument(String s) {
        return Primitives.isInteger(s);
    }

    @Override
    public Argument overrideSuggestions(@NotNull Supplier<List<Integer>> integerSuggestions) {
        super.setSuggestions(() -> integerSuggestions.get().stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));
        return this;
    }
}
