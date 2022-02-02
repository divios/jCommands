package maptree;

import io.github.divios.jcommands.JCommand;
import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.arguments.types.IntegerArgument;
import io.github.divios.jcommands.arguments.types.StringArgument;
import io.github.divios.jcommands.maptree.Node;
import io.github.divios.jcommands.maptree.TreeMap;
import io.github.divios.jcommands.utils.Value;
import io.github.divios.jcommands.utils.ValueMap;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class mapTests {

    @Test
    public void creation() {
        TreeMap treeMap = new TreeMap();
    }

    @Test
    public void testPut() {
        TreeMap treeMap = new TreeMap();
        JCommand test = new JCommand("test")
                .withArguments(new StringArgument("strings"), new IntegerArgument("integer")
                        .overrideSuggestions(() -> Arrays.asList(3, 4, 5, 6))
                );
        treeMap.put(test);
    }

    @Test
    public void testArguments() {
        TreeMap treeMap = new TreeMap();
        JCommand test = new JCommand("test")
                .withArguments(new StringArgument("strings"), new IntegerArgument("integer")
                        .overrideSuggestions(() -> Arrays.asList(3, 4, 5, 6), true));
        treeMap.put(test);

        String label = "test";
        String[] args = new String[]{""};

        Node node = treeMap.search(label, args);
        Assert.assertEquals(node.getLabel().getName(), "strings");
    }

    @Test
    public void testArguments2() {
        TreeMap treeMap = new TreeMap();
        JCommand test = new JCommand("test")
                .withArguments(new StringArgument("strings"), new IntegerArgument("integer")
                        .overrideSuggestions(() -> Arrays.asList(3, 4, 5, 6), true));
        treeMap.put(test);

        String label = "test";
        String[] args = new String[]{"", "3"};

        Node node = treeMap.search(label, args);
        Assert.assertEquals(node.getLabel().getName(), "integer");
    }

    @Test
    public void testArguments3() {
        TreeMap treeMap = new TreeMap();
        JCommand test = new JCommand("test")
                .withArguments(new StringArgument("strings"), new IntegerArgument("integer")
                        .overrideSuggestions(() -> Arrays.asList(3, 4, 5, 6), true));
        treeMap.put(test);

        String label = "test";
        String[] args = new String[]{"", "10"};

        Assert.assertNull(treeMap.search(label, args));
    }

    @Test
    public void testSubCommands() {
        TreeMap treeMap = new TreeMap();
        JCommand test = new JCommand("test")
                .withSubcommands(
                        JCommand.create("sub1")
                ).withSubcommands(
                        JCommand.create("sub2")
                );
        treeMap.put(test);

        String label = "test";
        String[] args = new String[]{""};

        List<String> suggestions = treeMap.search(label, Arrays.copyOfRange(args, 0, args.length - 1)).getChildren().stream()
                .map(node -> node.getLabel().getSuggestions())
                .reduce(new ArrayList<>(), (strings, strings2) -> {
                    strings.addAll(strings2);
                    return strings;
                });

        Assert.assertEquals(Arrays.asList("sub1", "sub2"), suggestions);
    }

    @Test
    public void testSubCommands2() {
        TreeMap treeMap = new TreeMap();
        JCommand test = new JCommand("test")
                .withSubcommands(
                        JCommand.create("sub1")
                                .withArguments(new StringArgument("")
                                        .overrideSuggestions(() -> Arrays.asList("ok", "ok3"))
                                )
                ).withSubcommands(
                        JCommand.create("sub2")
                );
        treeMap.put(test);

        String label = "test";
        String[] args = new String[]{"sub1", ""};

        List<String> suggestions = new ArrayList<>();

        for (Node child : treeMap.search(label, Arrays.copyOfRange(args, 0, args.length - 1)).getChildren()) {
            //if (!meetsCommandRequirements(sender, child.getCommand())) continue;

            for (String suggestion : child.getLabel().getSuggestions())
                if (suggestion.startsWith(args[args.length - 1]))
                    suggestions.add(suggestion);
        }

        Assert.assertEquals(Arrays.asList("ok", "ok3"), suggestions);
    }

    @Test
    public void testAction() {
        TreeMap treeMap = new TreeMap();
        JCommand test = new JCommand("test")
                .withSubcommands(
                        JCommand.create("sub1")
                                .withArguments(new StringArgument("")
                                        .overrideSuggestions(() -> Arrays.asList("ok", "ok3"))
                                )
                                .executes((sender, valueMap) -> System.out.println("oke"))
                ).withSubcommands(
                        JCommand.create("sub2")
                );
        treeMap.put(test);

        String label = "test";
        String[] args = new String[]{"sub1", "ok"};

        Node node = treeMap.search(label, args);
        Assert.assertNotNull(node);

        node.getCommand().getDefaultExecutor().accept(null, null);

    }

    @Test
    public void testAction2() {
        TreeMap treeMap = new TreeMap();
        JCommand test = new JCommand("test")
                .withSubcommands(
                        JCommand.create("restock")
                                .withArguments(new StringArgument("shop")
                                        .overrideSuggestions(() -> Arrays.asList("blocks", "drops", "--all", "farm"))
                                )
                                .executes((sender, valueMap) -> System.out.println(valueMap.get("shop").getAsString()))
                ).withSubcommands(
                        JCommand.create("sub2")
                );
        treeMap.put(test);

        String label = "test";
        String[] args = new String[]{"restock", "drops"};

        Node node = treeMap.search(label, args);
        Assert.assertNotNull(node);

        node.getCommand().getDefaultExecutor().accept(null, wrapArgs(node.getCommand(), args));

    }

    @Test
    public void testSize() {
        TreeMap treeMap = new TreeMap();
        JCommand test = new JCommand("test")
                .withSubcommands(
                        JCommand.create("restock")
                                .withArguments(new StringArgument("shop")
                                        .overrideSuggestions(() -> Arrays.asList("blocks", "drops", "--all", "farm"))
                                )
                                .executes((sender, valueMap) -> System.out.println(valueMap.get("shop").getAsString()))
                ).withSubcommands(
                        JCommand.create("sub2")
                );
        treeMap.put(test);

        Assert.assertEquals(treeMap.height(), 3);
    }

    private ValueMap wrapArgs(JCommand command, String[] args) {

        Map<String, Value> valueMap = new LinkedHashMap<>();
        List<Argument> arguments = command.getArguments();

        String[] validArgs = Arrays.copyOfRange(args, ArrayUtils.indexOf(args, command.getName()) + 1, args.length);

        for (int i = 0; i < arguments.size(); i++) {
            valueMap.put(arguments.get(i).getName(), Value.ofString(validArgs[i]));
        }

        return ValueMap.of(valueMap);
    }

}
