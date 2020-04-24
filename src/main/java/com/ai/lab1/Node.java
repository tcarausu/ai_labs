package com.ai.lab1;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public String name;
    public double cost;
    public double gScore;
    public double heuristicScore;
    public double fScore = 0;
    public double uniformCost;
    public Node parent;
    public List<Edge> edges = new ArrayList<>();

    public Node(String name, double heuristicScore) {
        this.name = name;
        this.heuristicScore = heuristicScore;
    }

    public Node(String name) {
        this.name = name;
    }

    public Node(Node node) {
        this.name = node.name;
        this.cost = node.cost;
        this.edges.addAll(node.edges);
        this.parent = node.parent;
    }

    public String toString() {
        return name;
    }

    public void setHeuristicScore(double heuristicScore) {
        this.heuristicScore = heuristicScore;
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }
}
