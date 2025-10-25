package metrics;

import graph.Edge;
import java.util.ArrayList;
import java.util.List;

public class Result {
    private final String algorithmName;
    private final List<Edge> mstEdges;
    private final int totalCost;
    private final int operationCount;
    private final double executionTimeMs;  // Changed from long to double
    private final int vertices;
    private final int edges;
    private final boolean isValid;
    private final String graphName;

    public Result(String algorithmName, List<Edge> mstEdges, int totalCost,
                  int operationCount, double executionTimeMs, int vertices,  // Changed parameter type
                  int edges, boolean isValid, String graphName) {
        this.algorithmName = algorithmName;
        this.mstEdges = new ArrayList<>(mstEdges);
        this.totalCost = totalCost;
        this.operationCount = operationCount;
        this.executionTimeMs = executionTimeMs;
        this.vertices = vertices;
        this.edges = edges;
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

    public int getOperationCount() {
        return operationCount;
    }

    public double getExecutionTimeMs() {  // Changed return type
        return executionTimeMs;
    }

    public int getVertices() {
        return vertices;
    }

    public int getEdges() {
        return edges;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getGraphName() {
        return graphName;
    }

    @Override
    public String toString() {
        return String.format("%s: Cost=%d, Edges=%d, Operations=%d, Time=%.3fms, Valid=%s",
                algorithmName, totalCost, mstEdges.size(), operationCount, 
                executionTimeMs, isValid);
    }
}