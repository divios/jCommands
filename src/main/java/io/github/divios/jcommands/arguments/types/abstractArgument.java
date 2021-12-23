package io.github.divios.jcommands.arguments.types;

import io.github.divios.jcommands.arguments.Argument;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public abstract class abstractArgument<T> implements Argument {

    Supplier<List<String>> suggestions;

    abstractArgument(Supplier<List<String>> suggestions) {
        this.suggestions = suggestions;
    }

    public abstract boolean isValidArgument(String s);

    public List<String> getSuggestions() {
        return suggestions == null ? null : suggestions.get();
    }

    void setSuggestions(@NotNull Supplier<List<String>> suggestions) {
        this.suggestions = suggestions;
    }

    public abstract Argument overrideSuggestions(@NotNull Supplier<List<T>> suggestions);



}
