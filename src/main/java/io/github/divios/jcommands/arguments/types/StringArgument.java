package io.github.divios.jcommands.arguments.types;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class StringArgument extends abstractArgument<String> {

    public StringArgument() {
        super(ArrayList::new);
    }

    @Override
    protected boolean isValidArgumentAbstract(String o) {
        return true;
    }

    @Override
    public abstractArgument<String> overrideSuggestions(@NotNull Supplier<List<String>> stringSuggestions, boolean imperative) {
        super.setSuggestions(stringSuggestions);
        super.imperative = imperative;
        return this;
    }


}
