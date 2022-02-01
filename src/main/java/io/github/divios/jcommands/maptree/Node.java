package io.github.divios.jcommands.maptree;

import io.github.divios.jcommands.JCommand;
import io.github.divios.jcommands.arguments.Argument;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private final Argument label;
    private final JCommand command;
    private Node parent;
    private List<Node> children;

    Node(Argument label, JCommand command) {
        this.label = label;
        this.command = command;
        this.children = new ArrayList<>();
    }

    Node(Argument label, JCommand command, Node parent) {
        this.label = label;
        this.command = command;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void addChildren(Node child) {
        children.add(child);
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

    public Argument getLabel() {
        return label;
    }

    public JCommand getCommand() {
        return command;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

}
