package graph;

import java.util.*;

public class Graph {
    private final int vertices;
    private final List<Edge> edges;
    private final List<List<Edge>> adjacencyList;
    private String name;
    private String description;
    private int graphId;

    private final List<String> nodeNames;                    // Index to name mapping
    private final Map<String, Integer> nodeIndexMap;         // Name to index mapping

    public Graph(int vertices) {
        this(vertices, null, null, -1);
    }

    public Graph(List<String> nodeNames, String name, String description, int graphId) {
        this(nodeNames.size(), name, description, graphId);

        for (int i = 0; i < nodeNames.size(); i++) {
            String nodeName = nodeNames.get(i);
            this.nodeNames.set(i, nodeName);
            this.nodeIndexMap.put(nodeName, i);
        }
    }

    private Graph(int vertices, String name, String description, int graphId) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("Number of vertices must be positive");
        }
        this.vertices = vertices;
        this.edges = new ArrayList<>();
        this.adjacencyList = new ArrayList<>(vertices);
        this.name = name;
        this.description = description;
        this.graphId = graphId;

        this.nodeNames = new ArrayList<>(vertices);
        this.nodeIndexMap = new HashMap<>();
        for (int i = 0; i < vertices; i++) {
            adjacencyList.add(new ArrayList<>());
            nodeNames.add(String.valueOf(i));
            nodeIndexMap.put(String.valueOf(i), i);
        }
    }

    public void addEdge(int source, int destination, int weight) {
        String fromNode = nodeNames.get(source);
        String toNode = nodeNames.get(destination);
        addEdge(source, destination, weight, fromNode, toNode);
    }

    public void addEdge(String fromNode, String toNode, int weight) {
        Integer sourceIdx = nodeIndexMap.get(fromNode);
        Integer destIdx = nodeIndexMap.get(toNode);

        if (sourceIdx == null || destIdx == null) {
            throw new IllegalArgumentException("Node name not found: " +
                    (sourceIdx == null ? fromNode : toNode));
        }

        addEdge(sourceIdx, destIdx, weight, fromNode, toNode);
    }

    private void addEdge(int source, int destination, int weight, String fromNode, String toNode) {
        if (source < 0 || source >= vertices || destination < 0 || destination >= vertices) {
            throw new IllegalArgumentException("Invalid vertex index");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Edge weight cannot be negative");
        }
        if (source == destination) {
            throw new IllegalArgumentException("Self-loops are not allowed");
        }

        Edge edge = new Edge(source, destination, weight, fromNode, toNode);
        edges.add(edge);

        adjacencyList.get(source).add(edge);
        adjacencyList.get(destination).add(new Edge(destination, source, weight, toNode, fromNode));
    }

    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    public List<Edge> getAdjacentEdges(int vertex) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException("Invalid vertex index");
        }
        return new ArrayList<>(adjacencyList.get(vertex));
    }

    public int getVertices() {
        return vertices;
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getGraphId() {
        return graphId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGraphId(int graphId) {
        this.graphId = graphId;
    }

    public String getNodeName(int index) {
        return nodeNames.get(index);
    }

    public Integer getNodeIndex(String name) {
        return nodeIndexMap.get(name);
    }

    public List<String> getNodeNames() {
        return new ArrayList<>(nodeNames);
    }

    public boolean isConnected() {
        if (vertices == 0) return true;
        if (edges.isEmpty()) return vertices == 1;

        boolean[] visited = new boolean[vertices];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(0);
        visited[0] = true;
        int visitedCount = 1;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (Edge edge : adjacencyList.get(current)) {
                int neighbor = edge.getDestination();
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                    visitedCount++;
                }
            }
        }

        return visitedCount == vertices;
    }

    public int countComponents() {
        boolean[] visited = new boolean[vertices];
        int components = 0;

        for (int v = 0; v < vertices; v++) {
            if (!visited[v]) {
                components++;
                Queue<Integer> queue = new LinkedList<>();
                queue.offer(v);
                visited[v] = true;

                while (!queue.isEmpty()) {
                    int current = queue.poll();
                    for (Edge edge : adjacencyList.get(current)) {
                        int neighbor = edge.getDestination();
                        if (!visited[neighbor]) {
                            visited[neighbor] = true;
                            queue.offer(neighbor);
                        }
                    }
                }
            }
        }

        return components;
    }

    public boolean validate() {
        for (Edge edge : edges) {
            if (edge.getWeight() < 0) {
                System.err.println("Invalid graph: negative edge weight found");
                return false;
            }
        }

        for (Edge edge : edges) {
            if (edge.getSource() < 0 || edge.getSource() >= vertices ||
                    edge.getDestination() < 0 || edge.getDestination() >= vertices) {
                System.err.println("Invalid graph: vertex index out of bounds");
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append("Graph: ").append(name).append("\n");
        }
        if (graphId >= 0) {
            sb.append("ID: ").append(graphId).append("\n");
        }
        if (description != null) {
            sb.append("Description: ").append(description).append("\n");
        }
        sb.append(String.format("Nodes: %s\n", nodeNames));
        sb.append(String.format("Vertices: %d, Edges: %d\n", vertices, edges.size()));
        sb.append(String.format("Connected: %s\n", isConnected() ? "Yes" : "No"));
        sb.append("Edge List:\n");
        for (Edge edge : edges) {
            sb.append("  ").append(edge).append("\n");
        }
        return sb.toString();
    }
}