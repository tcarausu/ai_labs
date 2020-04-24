package com.ai.lab1;

public class Edge {
    public Node parent;
    public String name;
    public double cost;

    public Edge(Node parent, String name, double cost) {
        this.parent = parent;
        this.name = name;
        this.cost = cost;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
