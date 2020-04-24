package com.ai.lab1;

import com.ai.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MainPuzzle {

    public static void main(String[] args) throws FileNotFoundException {
        Graph graph = new Graph();
        HashMap<Node, List<Edge>> nodes = new HashMap<>();
        Node startingNode = null;
        String startingCity = "";
        List<String> goals = new ArrayList<>();
        List<Node> endStates = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        Scanner sc = new Scanner(new File(Constant.puzzle));
        Scanner sc2 = new Scanner(new File(Constant.puzzleHeuristic));

        if (sc.hasNext("#")) {
            sc.nextLine();
        }
        startingCity = sc.nextLine();
        if (sc.hasNext(" ")) {
            String[] endStateNames = sc.nextLine().split(" ");
            goals.addAll(Arrays.asList(endStateNames));
        } else {
            goals.add(sc.nextLine());
        }
        while (sc.hasNext() && sc2.hasNext()) {

            String line = sc.nextLine();
            String[] splitLine = line.split(":");
            String name = splitLine[0];
            String[] destinations = splitLine[1].trim().split(" ");

            String line2 = sc2.nextLine();
            String[] splitLine2 = line2.split(":");
            double heuristic = Double.parseDouble(splitLine2[1].trim());

            Node node = new Node(name, heuristic);
            if (name.equals(startingCity)) {
                startingNode = node;
            } else if (goals.contains(name)) {
                endStates.add(node);
            }

            //Add edges
            List<Edge> nodeEdges = new ArrayList<>();
            for (String dest : destinations) {
                String[] cityAndValue = dest.split(",");
                String childCity = cityAndValue[0].trim();
                double cost = Double.parseDouble(cityAndValue[1]);

                Edge edge = new Edge(null, childCity, cost);
                nodeEdges.add(edge);
                edges.add(edge);
                node.addEdge(edge);
                edge.setParent(node);
            }
            nodes.put(node, nodeEdges);
        }
        graph.addParentNodeToEdges(nodes, edges);
        graph.printData(startingCity, endStates, nodes, edges);
        graph.printAstar(startingNode, endStates, nodes, null);
        graph.checkHeuristic(nodes, endStates);
    }
}
