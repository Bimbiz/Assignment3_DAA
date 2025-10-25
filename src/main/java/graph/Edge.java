package graph;

public class Edge implements Comparable<Edge> {
    private final int source;        // Internal integer index
    private final int destination;   // Internal integer index
    private final int weight;
    private final String fromNode;   // String name (e.g., "A")
    private final String toNode;     // String name (e.g., "B")

    public Edge(int source, int destination, int weight) {
        this(source, destination, weight, String.valueOf(source), String.valueOf(destination));
    }

    public Edge(int source, int destination, int weight, String fromNode, String toNode) {
        if (weight < 0) {
            throw new IllegalArgumentException("Edge weight cannot be negative");
        }
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    public int getSource() {
        return source;
    }

    public int getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public String getFromNode() {
        return fromNode;
    }

    public String getToNode() {
        return toNode;
    }

    public int either() {
        return source;
    }

    public int other(int vertex) {
        if (vertex == source) return destination;
        else if (vertex == destination) return source;
        else throw new IllegalArgumentException("Invalid endpoint");
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public String toString() {
        return String.format("%s--%s (weight: %d)", fromNode, toNode, weight);
    }

    public String toJSON() {
        return String.format("{\"from\": \"%s\", \"to\": \"%s\", \"weight\": %d}",
                fromNode, toNode, weight);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Edge)) return false;
        Edge other = (Edge) obj;
        return (source == other.source && destination == other.destination && weight == other.weight) ||
                (source == other.destination && destination == other.source && weight == other.weight);
    }

    @Override
    public int hashCode() {
        int min = Math.min(source, destination);
        int max = Math.max(source, destination);
        return 31 * (31 * min + max) + weight;
    }
}