package io.github.divios.jcommands.arguments.types;

import io.github.divios.jcommands.utils.Primitives;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IntegerArgument extends abstractArgument<Integer> {

    public IntegerArgument() {
        super(ArrayList::new);
    }

    @Override
    protected boolean isValidArgumentAbstract(String s) {
        return Primitives.isInteger(s);
    }

    @Override
    public abstractArgument<Integer> overrideSuggestions(@NotNull Supplier<List<Integer>> integerSuggestions, boolean imperative) {
        super.setSuggestions(() -> integerSuggestions.get().stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));
        super.imperative = imperative;
        return this;
    }
}
