package graph;

/**
 * Represents an edge in the transportation network.
 * Each edge connects two districts with an associated construction cost.
 *
 * @author Assignment 3 - MST
 * @version 1.0
 */
public class Edge implements Comparable<Edge> {
    private final int source;
    private final int destination;
    private final int weight;

    /**
     * Creates a new edge.
     * @param source Source vertex (district)
     * @param destination Destination vertex (district)
     * @param weight Cost of constructing this road
     */
    public Edge(int source, int destination, int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Edge weight cannot be negative");
        }
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    /**
     * Gets the source vertex.
     * @return source vertex
     */
    public int getSource() {
        return source;
    }

    /**
     * Gets the destination vertex.
     * @return destination vertex
     */
    public int getDestination() {
        return destination;
    }

    /**
     * Gets the edge weight.
     * @return edge weight (construction cost)
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Returns either endpoint of this edge (Princeton-style).
     * @return either endpoint
     */
    public int either() {
        return source;
    }

    /**
     * Returns the endpoint of this edge that is different from the given vertex (Princeton-style).
     * @param vertex one endpoint
     * @return the other endpoint
     * @throws IllegalArgumentException if vertex is not one of the endpoints
     */
    public int other(int vertex) {
        if (vertex == source) return destination;
        else if (vertex == destination) return source;
        else throw new IllegalArgumentException("Invalid endpoint");
    }

    /**
     * Compares edges by weight for sorting (used in Kruskal's algorithm).
     */
    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public String toString() {
        return String.format("%d--%d (weight: %d)", source, destination, weight);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Edge)) return false;
        Edge other = (Edge) obj;
        // Undirected: (0-1) equals (1-0)
        return (source == other.source && destination == other.destination && weight == other.weight) ||
                (source == other.destination && destination == other.source && weight == other.weight);
    }

    @Override
    public int hashCode() {
        // Symmetric hash for undirected edges
        int min = Math.min(source, destination);
        int max = Math.max(source, destination);
        return 31 * (31 * min + max) + weight;
    }
}