package io.github.divios.jcommands.arguments.types;

import io.github.divios.jcommands.arguments.Argument;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class StringArgument extends abstractArgument<String> {

    public StringArgument() {
        super(() -> null);
    }

    @Override
    public boolean isValidArgument(String o) {
        return true;
    }

    @Override
    public Argument overrideSuggestions(@NotNull Supplier<List<String>> stringSuggestions) {
        super.setSuggestions(stringSuggestions);
        return this;
    }


}
