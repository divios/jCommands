package io.github.divios.jcommands.maptree;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.divios.jcommands.JCommand;
import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.arguments.types.StringArgument;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

        Node search = root;
        int pos = 0;
        List<Node> children;

        while ((pos < args.length) && (search != null) && !(children = search.getChildren()).isEmpty()) {
            Node childMatch = null;
            for (Node child : children) {
                if (child.getLabel().isValidArgument(args[pos])) {
                    childMatch = child;
                    break;
                }
            }
            search = childMatch;
            pos++;
        }

        if (search != null)
            cache.put(commandLabel, args, search);

        return search;
    }

    public void put(JCommand command) {
        Node root = new Node(new StringArgument(""), command);
        processChildren(root, command);
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

    private void processChildren(Node node, JCommand command) {
        if (!command.getArguments().isEmpty())      // Process arguments if any
            node = processArguments(node, command.getArguments());

        // TODO: process subCommand with same label and reduce them
        for (JCommand subCommand : command.getSubCommands()) {
            Node child = new Node(new StringArgument(subCommand.getName())
                    .overrideSuggestions(() -> Collections.singletonList(subCommand.getName()), true),
                    subCommand);
            node.addChildren(child);
            processChildren(child, subCommand);
        }
    }

    private Node processArguments(Node to, List<Argument> arguments) {
        Node current = to;
        for (Argument argument : arguments) {
            Node child = new Node(argument, cloneWithOutAction(current.getCommand()));  // Remove actions
            current.addChildren(child);
            current = child;
        }
        cloneActions(current.getCommand(), to.getCommand());  // Set the action to the last node
        return current;     // Returns the last node on the chain
    }

    private void cloneActions(JCommand to, JCommand from) {
        to.executes(from.getDefaultExecutor());
        to.executesPlayer(from.getPlayerExecutor());
        to.executesConsole(from.getConsoleExecutor());
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
