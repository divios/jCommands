package maptree;

import io.github.divios.jcommands.JCommand;
import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.arguments.types.IntegerArgument;
import io.github.divios.jcommands.arguments.types.StringArgument;
import io.github.divios.jcommands.maptree.Node;
import io.github.divios.jcommands.maptree.TreeMap;
import io.github.divios.jcommands.util.Value;
import io.github.divios.jcommands.util.ValueMap;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

        System.out.println(treeMap);

        List<String> suggestions = treeMap.search(label, Arrays.copyOfRange(args, 0, args.length - 1)).getChildren().stream()
                .map(node -> node.getLabel().getSuggestions())
                .reduce(new ArrayList<>(), (strings, strings2) -> {
                    strings.addAll(strings2);
                    return strings;
                });

        Assert.assertEquals(Arrays.asList("sub2", "sub1"), suggestions);
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
    public void testSimilarSubcommands() {
        TreeMap treeMap = new TreeMap();
        AtomicInteger testValue = new AtomicInteger();
        JCommand test = new JCommand("test")
                .withSubcommands(
                        JCommand.create("open")
                                .executes((sender, valueMap) -> testValue.set(1))
                )
                .withSubcommands(
                        JCommand.create("open")
                                .withArguments(new StringArgument("shop"))
                                .executes((sender, valueMap) -> testValue.set(2))
                )
                .withSubcommands(
                        JCommand.create("open")
                                .withArguments(new StringArgument("shop"), new StringArgument("player"))
                                .executes((sender, valueMap) -> testValue.set(3))
                );
        treeMap.put(test);

        treeMap.search("test", new String[]{"open"}).getCommand().getDefaultExecutor().accept(null, null);
        Assert.assertEquals(1, testValue.get());

        treeMap.search("test", new String[]{"open", "shop"}).getCommand().getDefaultExecutor().accept(null, null);
        Assert.assertEquals(2, testValue.get());

        treeMap.search("test", new String[]{"open", "shop", "divios"}).getCommand().getDefaultExecutor().accept(null, null);
        Assert.assertEquals(3, testValue.get());

    }

    @Test
    public void testAction() {
        TreeMap treeMap = new TreeMap();
        JCommand test = new JCommand("test")
                .withSubcommands(
                        JCommand.create("sub1")
                                .withArguments(new StringArgument("okes")
                                        .overrideSuggestions(() -> Arrays.asList("ok", "ok3"))
                                )
                                .executes((sender, valueMap) -> System.out.println("oke"))
                ).withSubcommands(
                        JCommand.create("sub2")
                );
        treeMap.put(test);

        String label = "test";
        String[] args = new String[]{"sub1", "ok"};

        System.out.println(treeMap);

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

    @Test
    public void testAliases() {
        TreeMap treeMap = new TreeMap();
        JCommand test = new JCommand("test")
                .withAliases("txt", "ts", "tst")
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

        String[] labels = new String[]{"test", "txt", "ts", "tst"};
        String[] args = new String[]{"restock", "drops"};

        for (String label : labels)
            Assert.assertNotNull(treeMap.search(label, args));

    }

    @Test
    public void testPermissions() {
        TreeMap treeMap = new TreeMap();
        JCommand test = new JCommand("test")
                .assertPermission("test.perm")
                .withSubcommands(
                        JCommand.create("restock")
                                .assertPermission("test.perm.restock")
                                .withArguments(new StringArgument("shop")
                                        .overrideSuggestions(() -> Arrays.asList("blocks", "drops", "--all", "farm"))
                                )
                                .executes((sender, valueMap) -> System.out.println(valueMap.get("shop").getAsString()))
                ).withSubcommands(
                        JCommand.create("sub2")
                                .assertPermission("test.perm.sub2")
                );

        Assert.assertEquals("test.perm", test.getPermission());
        treeMap.put(test);

        Node node = treeMap.search("test", new String[]{"restock", "shop"});
        Assert.assertNotNull(node);
        Assert.assertEquals(new HashSet<>(Arrays.asList("test.perm.restock", "test.perm")), node.getPermissions());

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
