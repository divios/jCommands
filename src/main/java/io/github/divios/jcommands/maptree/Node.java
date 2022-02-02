package io.github.divios.jcommands.maptree;

import io.github.divios.jcommands.JCommand;
import io.github.divios.jcommands.arguments.Argument;

import org.bukkit.permissions.Permission;
import java.util.*;

public class Node {

    private final Argument label;
    private final JCommand command;
    private Node parent;
    private List<Node> children;
    private final Set<Permission> permissions;

    Node(Argument label, JCommand command) {
        this.label = label;
        this.command = command;
        this.children = new ArrayList<>();
        this.permissions = new HashSet<>();
    }

    Node(Argument label, JCommand command, Node parent) {
        this.label = label;
        this.command = command;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.permissions = new HashSet<>();
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void addChildren(Node child) {
        children.add(child);
    }

    public void addChildren(Collection<Node> children) {
        this.children.addAll(children);
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public void addPermission(Collection<Permission> permissions) {
        this.permissions.addAll(permissions);
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public Node getParent() {
        return parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Argument getLabel() {
        return label;
    }

    public JCommand getCommand() {
        return command;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public int getSize() {
        int size = 1;

        int aux = 0;
        for (Node child : children)
            aux = Math.max(aux, child.getSize());

        return size + aux;
    }



}
