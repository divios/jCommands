package io.github.divios.jcommands.maptree;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.divios.jcommands.JCommand;
import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.arguments.types.StringArgument;
import io.github.divios.jcommands.maptree.util.CollectionUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class TreeMap {

    private final TreeCache cache = TreeCache.create();
    private final HashMap<String, Node> rootNodes;

    public TreeMap() {
        this.rootNodes = new HashMap<>();
    }

    public Node search(String commandLabel, String[] args) {
        Node root;
        if ((root = rootNodes.get(commandLabel)) == null) return null;

        Node cached;
        if ((cached = cache.get(commandLabel, args)) != null)
            return cached;

        Node search;
        if ((search = innerSearch(root, args)) != null)
            cache.put(commandLabel, args, search);

        return search;
    }

    private Node innerSearch(Node node, String[] args) {
        if (node == null) return null;
        if (args.length == 0) return node;      // Finish search

        Node search = null;
        for (Node child : node.getChildren())
            if (child.getLabel().isValidArgument(args[0]))
                if ((search = innerSearch(child, Arrays.copyOfRange(args, 1, args.length))) != null)
                    break;

        return search;
    }

    public void put(JCommand command) {
        Node root = new Node(new StringArgument(command.getName()), command);
        root.addPermission(command.getPermission());        // Add first permission

        processChildren(root, command);
        reduceNode(root);
        rootNodes.put(command.getName().toLowerCase(), root);

        for (String alias : command.getAliases())      // Register aliases
            rootNodes.put(alias.toLowerCase(), root);
    }

    public void remove(String commandName) {
        rootNodes.remove(commandName);
    }

    public void clear() {
        rootNodes.clear();
    }

    public int height() {
        int height = 0;
        for (Node value : rootNodes.values())
            height = Math.max(height, value.getSize());

        return height;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        for (Node value : rootNodes.values())
            print(value, buffer, "", "");
        return buffer.toString();
    }

    private void print(Node node, StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(node.getLabel().getName());
        buffer.append('\n');
        for (Iterator<Node> it = node.getChildren().iterator(); it.hasNext(); ) {
            Node next = it.next();
            if (it.hasNext()) {
                print(next, buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                print(next, buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }


    // UTILS //

    private void processChildren(Node node, JCommand command) {
        if (!command.getArguments().isEmpty())      // Process arguments if any
            node = processArguments(node, command.getArguments());

        for (JCommand subCommand : command.getSubCommands()) {
            Node child = new Node(new StringArgument(subCommand.getName())
                    .overrideSuggestions(() -> Collections.singletonList(subCommand.getName()), true),
                    subCommand);
            child.addPermission(node.getPermissions());     // Add parent perms
            node.addChildren(child);
            processChildren(child, subCommand);
        }
    }

    private void reduceNode(Node node) {
        List<Node> finalChildren = new ArrayList<>();
        Map<String, List<Node>> similarNodes = new HashMap<>();

        for (Node child : node.getChildren())       // Populate similarNodes
            similarNodes.compute(child.getLabel().getName(), (s, strings) ->
                    CollectionUtil.concat((strings == null) ? new ArrayList<>() : strings, child)
            );

        /*similarNodes.values().forEach(nodes -> {      // Debug method to print values
            System.out.println(Arrays.toString(nodes.stream()
                    .map(node1 -> node1.getLabel().getName())
                    .toArray()));
        }); */

        for (List<Node> value : similarNodes.values()) {
            Node root;
            if (value.size() == 1) {        // If no similar children then just add the node
                root = value.get(0);
            } else {
                root = minHeightNode(value);
                for (Node node1 : CollectionUtil.remove(value, root))
                    root.addChildren(node1.getChildren());
            }
            reduceNode(root);
            finalChildren.add(root);
        }

        node.setChildren(finalChildren);
    }

    private Node minHeightNode(List<Node> nodes) {
        Node minNode = nodes.get(0);
        for (int i = 1; i < nodes.size(); i++)
            if (nodes.get(i).getSize() < minNode.getSize())
                minNode = nodes.get(i);

        return minNode;
    }

    private Node processArguments(Node to, List<Argument> arguments) {
        Node current = to;
        for (Argument argument : arguments) {
            Node child = new Node(argument, cloneWithOutAction(current.getCommand()));  // Remove actions
            child.addPermission(current.getPermissions());
            current.addChildren(child);
            current = child;
        }
        cloneActions(current.getCommand(), to.getCommand());  // Set the action to the last node
        // clearActions(to.getCommand()); Does not work for some reason
        return current;     // Returns the last node on the chain
    }

    private void cloneActions(JCommand to, JCommand from) {
        if (to == from) return;
        to.executes(from.getDefaultExecutor());
        to.executesPlayer(from.getPlayerExecutor());
        to.executesConsole(from.getConsoleExecutor());
    }

    private void clearActions(JCommand command) {
        System.out.println("Cleaned actions of " + command.getName());
        command.executes((sender, valueMap) -> {});
        command.executesPlayer((player, valueMap) -> {});
        command.executesConsole((consoleCommandSender, valueMap) -> {});
    }

    private JCommand cloneWithOutAction(JCommand command) {
        JCommand clone = JCommand.create(command.getName());

        clone.withSubcommands(command.getSubCommands());
        clone.withArguments(command.getArguments());
        clone.assertPermission(command.getPermission());
        clone.assertRequirements(command.getRequirements());

        return clone;
    }

    private static final class TreeCache {

        private final Cache<String, Node> cache;

        public static TreeCache create() {
            return new TreeCache();
        }

        private TreeCache() {
            cache = CacheBuilder.newBuilder()
                    .expireAfterAccess(3, TimeUnit.MINUTES)     // 3 minutes to expire
                    .build();
        }

        public Node get(String root, String[] args) {
            return cache.getIfPresent(appendArray(root, args));
        }

        public void put(String root, String[] args, Node node) {
            cache.put(appendArray(root, args), node);
        }

        private String appendArray(String root, String[] args) {
            StringBuilder stringBuilder = new StringBuilder(root);
            for (String arg : args)
                stringBuilder.append(arg);

            return stringBuilder.toString();
        }


    }


}
