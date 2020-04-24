package com.ai.lab1;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Graph {
    private int locationsVisited;
    AtomicInteger uniformCostCounter = new AtomicInteger();
    private boolean isHeuristicOptimistic = true;
    private boolean isHeuristicConsistent = true;
    private Node endState;

    public Node getEndState() {
        return endState;
    }

    public boolean isHeuristicOptimistic() {
        return isHeuristicOptimistic;
    }

    public boolean isHeuristicConsistent() {
        return isHeuristicConsistent;
    }

    public void addParentNodeToEdges(HashMap<Node, List<Edge>> nodes, List<Edge> edges) {
        LocalDateTime then = LocalDateTime.now();

        for (Edge edge : edges) {
            for (Node node : nodes.keySet()) {
                if (node.name.equals(edge.name)) {
                    edge.setParent(node);
                    break;
                }
            }
            if (ChronoUnit.SECONDS.between(then, LocalDateTime.now()) >= 60) {
                System.out.println("Code took too long time to execute");
                System.exit(1);
            }
        }
    }

    public void bfs(Node start, List<Node> endStates, HashMap<Node, List<Edge>> nodes) {
        Queue<String> queue = new Queue<>();
        queue.enqueue(start.name);

        List<String> visited = new ArrayList<>();
        visited.add(start.name);

        uniformCostCounter.getAndIncrement();
        boolean found = false;

        while (!queue.isEmpty() && !found) {
            String nodeName = queue.dequeue();
            Node node = null;
            for (Node node1 : nodes.keySet()) {
                if (nodeName.equals(node1.name)) {
                    node = node1;
                }
            }
            assert node != null;
            for (Edge edge : node.edges) {
                for (Node nodee : endStates) {
                    if (visited.contains(nodee.name)) {
                        endState = nodee;
                        found = true;
                        break;
                    }
                }
                if (found) {
                    break;
                }

                if (!visited.contains(edge.name)) {
                    queue.enqueue(edge.name);
                    visited.add(edge.name);
                    int i = 0;
                    List<Node> nodeList = new ArrayList<>(nodes.keySet());
                    for (Node node1 : nodeList) {
                        if (edge.name.equals(node1.name)) {
                            i = nodeList.indexOf(node1);
                        }
                    }
                    uniformCostCounter.getAndIncrement();
                    nodeList.get(i).parent = node;
                    nodeList.get(i).cost = edge.cost;
                }
            }
        }
        locationsVisited = uniformCostCounter.get();
        uniformCostCounter.set(0);
    }

    public void uniformCost(Node start, List<Node> endStates) {
        List<Node> list = new ArrayList<>();
        start.uniformCost = 0;
        boolean found = false;
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(i -> i.uniformCost));

        queue.add(start);
        List<Node> path = new ArrayList<>();
        Set<Node> explored = new HashSet<>();

        while (!queue.isEmpty()) {
            path.clear();
            // get node with the lowest fScore
            Node current = queue.poll();
            explored.add(current);
            assert current != null;
            for (Node node = current; node != null; node = node.parent) {
                path.add(node);
            }

            for (Node node : endStates) {
                if (node.name.equals(current.name)) {
                    node.parent = current.parent;
                    node.cost = current.uniformCost;
                    endState = node;
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }

            for (Edge e : current.edges) {
                Node child = e.parent;
                double cost = e.cost;
                if ((queue.contains(child) || explored.contains(child)) && !path.contains(child)) {
                    Node n = new Node(child);
                    list.add(n);
                    list.get(list.size() - 1).uniformCost = current.uniformCost + cost;
                    list.get(list.size() - 1).parent = current;
                    queue.add(list.get(list.size() - 1));
                } else if (!path.contains(child)) {
                    uniformCostCounter.getAndIncrement();
                    child.uniformCost = current.uniformCost + cost;
                    child.parent = current;
                    queue.add(child);
                }
            }
        }
        locationsVisited = uniformCostCounter.get();
        uniformCostCounter.set(0);
    }

    public void aStar(Node source, List<Node> endStates) {
        Set<Node> explored = new HashSet<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(i -> i.fScore));
        source.gScore = 0;
        boolean found = false;
        queue.add(source);

        while (!queue.isEmpty()) {
            // get node with the lowest fScore
            Node current = queue.poll();
            explored.add(current);
            uniformCostCounter.getAndIncrement();

            // goal found
            for (Node node : endStates) {
                if (node.name.equals(current.name)) {
                    endState = node;
                    found = true;
                    break;
                }
            }
            // end while loop as well
            if (found) {
                break;
            }

            for (Edge e : current.edges) {
                Node child = e.parent;
                double cost = e.cost;
                double gScoreTemp = current.gScore + cost;
                double fScoreTemp = gScoreTemp + child.heuristicScore;

                if (explored.contains(child) && fScoreTemp >= child.fScore) {
                    continue;
                } else if (!queue.contains(child) || fScoreTemp < child.fScore) {
                    child.parent = current;
                    child.gScore = gScoreTemp;
                    child.fScore = fScoreTemp;

                    queue.remove(child);
                    child.cost = e.cost;
                    queue.add(child);
                }
            }
        }
        locationsVisited = uniformCostCounter.get();
        uniformCostCounter.set(0);
    }

    public void addHeuristics(String file, HashMap<Node, List<Edge>> nodes) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(file));
        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] splitLine = line.split(":");
            String name = splitLine[0].trim();
            double heuristic = Double.parseDouble(splitLine[1].trim());

            nodes.forEach((key, value) -> {
                if (key.name.equals(name)) {
                    key.setHeuristicScore(heuristic);
                }
            });
        }
    }

    public void isHeuristicOptimistic(Node start, Node target) {
        int cost = 0;
        for (Node node = target; node != null; node = node.parent) {
            cost += node.cost;
        }

        if (start.heuristicScore > cost) {
            isHeuristicOptimistic = false;
            System.out.println("[ERR] h(" + start.name + ") > h*: " + start.heuristicScore + " > " + cost);
        }
    }

    public void isHeuristicConsistent(Node start, Node target, Edge edge) {
        if (start.heuristicScore > target.heuristicScore + edge.cost) {
            isHeuristicConsistent = false;
            System.out.println("[ERR] h(" + start.name + ") > h(" + edge.name + ") + c: "
                    + start.heuristicScore + " > " + target.heuristicScore + " + " + edge.cost);
        }
    }

    public void checkHeuristic(HashMap<Node, List<Edge>> nodes, List<Node> endStates) {
        System.out.println("Checking if heuristic is optimistic.");
        restartPath(nodes);
        for (Node node : nodes.keySet()) {
            aStar(node, endStates);
            isHeuristicOptimistic(node, getEndState());
            restartPath(nodes);
        }
        if (isHeuristicOptimistic()) {
            System.out.println("Heuristic is optimistic");
        } else {
            System.out.println("Heuristic is not optimistic");
        }
        System.out.println();

        System.out.println("Checking if heuristic is consistent.");
        restartPath(nodes);
        for (Node node : nodes.keySet()) {
            for (Edge edge : node.edges) {
                isHeuristicConsistent(node, edge.parent, edge);
            }
        }
        if (isHeuristicConsistent()) {
            System.out.println("Heuristic is consistent");
        } else {
            System.out.println("Heuristic is not consistent");
        }
        System.out.println();
    }

    public void printPath(Node target) {
        int cost = 0;
        List<Node> path = new ArrayList<>();
        for (Node node = target; node != null; node = node.parent) {
            path.add(node);
            cost += node.cost;
        }

        Collections.reverse(path);
        System.out.println("States visited = " + locationsVisited);
        System.out.println("Found path of length " + path.size() + " with total cost " + cost);
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i).name);
            if (i != path.size() - 1) {
                System.out.println(" =>");
            }
        }
    }

    public void restartPath(HashMap<Node, List<Edge>> nodes) {
        for (Node node : nodes.keySet()) {
            node.parent = null;
            node.cost = 0;
            node.fScore = 0;
            node.gScore = 0;
        }
    }

    public void printGraph(HashMap<Node, List<Edge>> nodes) {
        nodes.forEach((key, value) -> {
            System.out.print(key + " ");
            for (Edge edge : value) {
                System.out.print(edge.parent.name + " ");
            }
            System.out.println();
        });
    }

    public void printData(String startingCity, List<Node> endStates, HashMap<Node, List<Edge>> nodes, List<Edge> edges) {
        System.out.println("============ Data loading =============");
        System.out.println("Start state: " + startingCity);
        System.out.println("End state(s): " + Arrays.toString(endStates.toArray()));
        System.out.println("State space size: " + nodes.size());
        System.out.println("Total transitions: " + edges.size());
        System.out.println();
    }

    public void printBfs(Node start, List<Node> endStates, HashMap<Node, List<Edge>> nodes) {
        System.out.println("============ BFS ================");
        bfs(Objects.requireNonNull(start), endStates, nodes);
        printPath(getEndState());
        System.out.println("\n");
    }

    public void printUniform(Node start, List<Node> endStates, HashMap<Node, List<Edge>> nodes) {
        System.out.println("============ Uniform ================");
        restartPath(nodes);
        uniformCost(start, endStates);
        printPath(getEndState());
        System.out.println("\n");
    }

    public void printAstar(Node start, List<Node> endStates, HashMap<Node, List<Edge>> nodes, String heuristicFile) throws FileNotFoundException {
        System.out.println("============ A* ================");
        restartPath(nodes);
        if (heuristicFile != null) {
            addHeuristics(heuristicFile, nodes);
        }
        aStar(start, endStates);
        printPath(getEndState());
        System.out.println("\n");
    }

    public void printFullReportForFile(String startingNodeName, Node start, List<Node> endStates, HashMap<Node, List<Edge>> nodes, List<Edge> edges, String heuristicFile, String pessimisticHeuristic) throws FileNotFoundException {
        addParentNodeToEdges(nodes, edges);
        printData(startingNodeName, endStates, nodes, edges);
        printBfs(start, endStates, nodes);
        printUniform(start, endStates, nodes);
        printAstar(start, endStates, nodes, heuristicFile);
        checkHeuristic(nodes, endStates);
        printAstar(start, endStates, nodes,  pessimisticHeuristic);
        checkHeuristic(nodes, endStates);
    }
}
