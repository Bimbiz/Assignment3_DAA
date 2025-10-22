package graph;

import java.util.*;

/**
 * Represents an undirected weighted graph for the city transportation network.
 * This implementation supports both Prim's and Kruskal's algorithms.
 *
 * Provides both edge list (for Kruskal's) and adjacency list (for Prim's).
 *
 * @author Assignment 3 - MST
 * @version 1.0
 */
public class Graph {
    private final int vertices;
    private final List<Edge> edges;
    private final List<List<Edge>> adjacencyList;
    private String name;
    private String description;

    /**
     * Creates a new graph with the specified number of vertices.
     * @param vertices Number of districts in the city
     */
    public Graph(int vertices) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("Number of vertices must be positive");
        }
        this.vertices = vertices;
        this.edges = new ArrayList<>();
        this.adjacencyList = new ArrayList<>(vertices);

        for (int i = 0; i < vertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    /**
     * Creates a graph with name and description.
     * @param vertices Number of vertices
     * @param name Name of the graph
     * @param description Description of the graph
     */
    public Graph(int vertices, String name, String description) {
        this(vertices);
        this.name = name;
        this.description = description;
    }

    /**
     * Adds an undirected edge to the graph.
     * @param source Source vertex
     * @param destination Destination vertex
     * @param weight Edge weight (construction cost)
     */
    public void addEdge(int source, int destination, int weight) {
        if (source < 0 || source >= vertices || destination < 0 || destination >= vertices) {
            throw new IllegalArgumentException("Invalid vertex index");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Edge weight cannot be negative");
        }
        if (source == destination) {
            throw new IllegalArgumentException("Self-loops are not allowed");
        }

        Edge edge = new Edge(source, destination, weight);
        edges.add(edge);

        // Since the graph is undirected, add to both adjacency lists
        adjacencyList.get(source).add(edge);
        adjacencyList.get(destination).add(new Edge(destination, source, weight));
    }

    /**
     * Gets all edges in the graph (used by Kruskal's algorithm).
     * @return List of all edges
     */
    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    /**
     * Gets all edges adjacent to a specific vertex (used by Prim's algorithm).
     * @param vertex The vertex
     * @return List of adjacent edges
     */
    public List<Edge> getAdjacentEdges(int vertex) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException("Invalid vertex index");
        }
        return new ArrayList<>(adjacencyList.get(vertex));
    }

    /**
     * Gets the number of vertices in the graph.
     * @return number of vertices
     */
    public int getVertices() {
        return vertices;
    }

    /**
     * Gets the number of edges in the graph.
     * @return number of edges
     */
    public int getEdgeCount() {
        return edges.size();
    }

    /**
     * Gets the graph name.
     * @return graph name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the graph description.
     * @return graph description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the graph name.
     * @param name graph name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the graph description.
     * @param description graph description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Checks if the graph is connected using BFS.
     * A connected graph means all vertices are reachable from any vertex.
     *
     * @return true if all vertices are reachable from vertex 0
     */
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

    /**
     * Counts the number of connected components in the graph.
     * @return number of connected components
     */
    public int countComponents() {
        boolean[] visited = new boolean[vertices];
        int components = 0;

        for (int v = 0; v < vertices; v++) {
            if (!visited[v]) {
                components++;
                // BFS from this vertex
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

    /**
     * Validates the graph structure.
     * @return true if graph is valid
     */
    public boolean validate() {
        // Check for negative weights
        for (Edge edge : edges) {
            if (edge.getWeight() < 0) {
                System.err.println("Invalid graph: negative edge weight found");
                return false;
            }
        }

        // Check for invalid vertex indices
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
        if (description != null) {
            sb.append("Description: ").append(description).append("\n");
        }
        sb.append(String.format("Vertices: %d, Edges: %d\n", vertices, edges.size()));
        sb.append(String.format("Connected: %s, Components: %d\n",
                isConnected() ? "Yes" : "No", countComponents()));
        sb.append("Edge List:\n");
        for (Edge edge : edges) {
            sb.append("  ").append(edge).append("\n");
        }
        return sb.toString();
    }
}