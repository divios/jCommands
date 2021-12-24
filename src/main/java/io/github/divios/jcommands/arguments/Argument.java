package io.github.divios.jcommands.arguments;

import java.util.List;

public interface Argument {

    String getName();
    boolean isValidArgument(String o);
    List<String> getSuggestions();

}
