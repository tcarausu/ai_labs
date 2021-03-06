package com.ai.lab1;

import com.ai.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MainIstra {

    public static void main(String[] args) throws FileNotFoundException {
        Graph graph = new Graph();
        HashMap<Node, List<Edge>> nodes = new HashMap<>();
        Node startingNode = null;
        String startingCity = "";
        List<String> goals = new ArrayList<>();
        List<Node> endStates = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        Scanner sc = new Scanner(new File(Constant.istra));

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
        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] splitLine = line.split(":");
            String name = splitLine[0];
            String[] destinations = splitLine[1].trim().split(" ");

            Node node = new Node(name);
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

        graph.printFullReportForFile(startingCity, startingNode, endStates, nodes, edges, Constant.istraHeuristic, Constant.istraHeuristicPessimistic);
    }
}
