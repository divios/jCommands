package io.github.divios.jcommands.arguments;

import java.util.List;

public interface Argument {

    boolean isValidArgument(String o);
    List<String> getSuggestions();

}
