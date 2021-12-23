package io.github.divios.jcommands.arguments.types;

import io.github.divios.jcommands.arguments.Argument;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public abstract class abstractArgument<T> implements Argument {

    protected Supplier<List<String>> suggestions;
    protected boolean imperative = false;

    abstractArgument(Supplier<List<String>> suggestions) {
        this.suggestions = suggestions;
    }

    public boolean isValidArgument(String s) {
        if (imperative)
            return isValidArgumentAbstract(s) && suggestions.get().stream().anyMatch(s1 -> s1.equalsIgnoreCase(s));
        else
            return isValidArgumentAbstract(s);
    }

    protected abstract boolean isValidArgumentAbstract(String s);

    public List<String> getSuggestions() {
        return suggestions == null ? null : suggestions.get();
    }

    void setSuggestions(@NotNull Supplier<List<String>> suggestions) {
        this.suggestions = suggestions;
    }

    public abstractArgument<T> overrideSuggestions(@NotNull Supplier<List<T>> suggestions) {
        return overrideSuggestions(suggestions, false);
    }

    public abstract abstractArgument<T> overrideSuggestions(@NotNull Supplier<List<T>> suggestions, boolean imperative);

    public abstractArgument<T> setAsImperative() {
        imperative = true;
        return this;
    }

    public boolean isImperative() {
        return imperative;
    }

    @Override
    public String toString() {
        return "abstractArgument{" +
                "suggestions=" + suggestions +
                ", imperative=" + imperative +
                '}';
    }
}
