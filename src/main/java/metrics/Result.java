package metrics;

import graph.Edge;
import java.util.ArrayList;
import java.util.List;

public class Result {
    private final String algorithmName;
    private final List<Edge> mstEdges;
    private final int totalCost;
    private final int operations;
    private final long executionTimeMs;
    private final int graphVertices;
    private final int graphEdges;
    private final boolean isValid;
    private final String graphName;

    public Result(String algorithmName, List<Edge> mstEdges, int totalCost,
                  int operations, long executionTimeMs, int graphVertices, int graphEdges) {
        this(algorithmName, mstEdges, totalCost, operations, executionTimeMs,
                graphVertices, graphEdges, true, null);
    }

    public Result(String algorithmName, List<Edge> mstEdges, int totalCost,
                  int operations, long executionTimeMs, int graphVertices,
                  int graphEdges, boolean isValid, String graphName) {
        this.algorithmName = algorithmName;
        this.mstEdges = new ArrayList<>(mstEdges);
        this.totalCost = totalCost;
        this.operations = operations;
        this.executionTimeMs = executionTimeMs;
        this.graphVertices = graphVertices;
        this.graphEdges = graphEdges;
        this.isValid = isValid;
        this.graphName = graphName;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public List<Edge> getMstEdges() {
        return new ArrayList<>(mstEdges);
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getOperations() {
        return operations;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public int getGraphVertices() {
        return graphVertices;
    }

    public int getGraphEdges() {
        return graphEdges;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getGraphName() {
        return graphName;
    }

    public void printSummary() {
        System.out.println(algorithmName.toUpperCase() + " RESULTS");
        if (graphName != null) {
            System.out.println("Graph: " + graphName);
        }
        System.out.println(String.format("%-25s: %d vertices, %d edges",
                "Original Graph", graphVertices, graphEdges));
        System.out.println(String.format("%-25s: %d", "MST Total Cost", totalCost));
        System.out.println(String.format("%-25s: %d (expected: %d)",
                "MST Edge Count", mstEdges.size(), graphVertices - 1));
        System.out.println(String.format("%-25s: %d", "Operations Performed", operations));
        System.out.println(String.format("%-25s: %d ms", "Execution Time", executionTimeMs));
        System.out.println(String.format("%-25s: %s", "Validation Status",
                isValid ? "VALID" : "✗ INVALID"));
        System.out.println("\nMST Edges:");
        for (int i = 0; i < mstEdges.size(); i++) {
            System.out.println(String.format("  [%2d] %s", i + 1, mstEdges.get(i)));
        }
    }

    public String getCompactSummary() {
        return String.format("%s: Cost=%d, Edges=%d, Ops=%d, Time=%dms, Valid=%s",
                algorithmName, totalCost, mstEdges.size(), operations,
                executionTimeMs, isValid ? "YES" : "NO");
    }

    public String toCSVLine() {
        return String.format("%s,%s,%d,%d,%d,%d,%d,%d,%s",
                graphName != null ? graphName : "Unknown",
                algorithmName,
                graphVertices,
                graphEdges,
                totalCost,
                mstEdges.size(),
                operations,
                executionTimeMs,
                isValid ? "Valid" : "Invalid");
    }

    public static String getCSVHeader() {
        return "Graph Name,Algorithm,Vertices,Edges,MST Cost,MST Edges,Operations,Time (ms),Status";
    }
}