package io.github.divios.jcommands.arguments.types;

import io.github.divios.jcommands.arguments.Argument;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class abstractArgument<T> implements Argument {

    protected final String name;
    protected Function<CommandSender, List<String>> suggestions;
    protected boolean imperative = false;

    abstractArgument(String name, Supplier<List<String>> suggestions) {
        this.name = name;
        this.suggestions = sender -> suggestions.get();
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isValidArgument(String s) {
        if (imperative)
            return isValidArgumentAbstract(s) && suggestions.apply(null).stream().anyMatch(s1 -> s1.equalsIgnoreCase(s));
        else
            return isValidArgumentAbstract(s);
    }

    protected abstract boolean isValidArgumentAbstract(String s);

    public List<String> getSuggestions(CommandSender sender) {
        return suggestions == null ? null : suggestions.apply(sender);
    }

    void setSuggestions(@NotNull Supplier<List<String>> suggestions) {
        this.suggestions = sender -> suggestions.get();
    }

    void setSuggestions(@NotNull Function<CommandSender, List<String>> suggestions) {
        this.suggestions = suggestions;
    }

    public abstractArgument<T> overrideSuggestions(@NotNull Supplier<List<T>> suggestions) {
        return overrideSuggestions(suggestions, false);
    }

    public abstractArgument<T> overrideSuggestions(@NotNull Supplier<List<T>> suggestions, boolean imperative) {
        return overrideSuggestions(sender -> suggestions.get(), imperative);
    }

    public abstractArgument<T> overrideSuggestions(@NotNull Function<CommandSender, List<T>> suggestions) {
        return overrideSuggestions(suggestions, false);
    }

    public abstract abstractArgument<T> overrideSuggestions(@NotNull Function<CommandSender, List<T>> suggestions, boolean imperative);

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
