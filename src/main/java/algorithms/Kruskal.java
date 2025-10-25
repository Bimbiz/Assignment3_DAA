package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Result;

import java.util.*;

public class Kruskal {
    public Result findMST(Graph graph) {
        long startTime = System.nanoTime();

        int vertices = graph.getVertices();
        int edgeCount = graph.getEdgeCount();
        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;
        int operations = 0;
        String graphName = graph.getName();

        if (!graph.isConnected()) {
            long endTime = System.nanoTime();
            long executionTime = (endTime - startTime) / 1_000_000;
            System.err.println("WARNING: Graph is disconnected. Computing minimum spanning forest.");

            return computeMSF(graph, startTime);
        }

        List<Edge> edges = new ArrayList<>(graph.getEdges());
        Collections.sort(edges);
        operations += edges.size();

        UnionFind uf = new UnionFind(vertices);

        for (Edge edge : edges) {
            operations++;

            int source = edge.getSource();
            int dest = edge.getDestination();

            if (!uf.connected(source, dest)) {
                mstEdges.add(edge);
                totalCost += edge.getWeight();
                uf.union(source, dest);

                if (mstEdges.size() == vertices - 1) {
                    break;
                }
            }
        }

        operations += uf.getOperationCount();

        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0; // Changed to double division

        Result result = new Result("Kruskal's Algorithm", mstEdges, totalCost,
                operations, executionTimeMs, vertices, edgeCount,
                true, graphName);

        boolean isValid = validateMST(graph, result);
        
        // Update validation status if needed
        if (!isValid) {
            result = new Result("Kruskal's Algorithm", mstEdges, totalCost,
                    operations, executionTimeMs, vertices, edgeCount,
                    false, graphName);
        }

        return result;
    }

    private Result computeMSF(Graph graph, long startTime) {
        int vertices = graph.getVertices();
        int edgeCount = graph.getEdgeCount();
        List<Edge> msfEdges = new ArrayList<>();
        int totalCost = 0;
        int operations = 0;

        List<Edge> edges = new ArrayList<>(graph.getEdges());
        Collections.sort(edges);
        operations += edges.size();

        UnionFind uf = new UnionFind(vertices);

        for (Edge edge : edges) {
            operations++;

            if (!uf.connected(edge.getSource(), edge.getDestination())) {
                msfEdges.add(edge);
                totalCost += edge.getWeight();
                uf.union(edge.getSource(), edge.getDestination());
            }
        }

        operations += uf.getOperationCount();
        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1_000_000;

        return new Result("Kruskal's Algorithm (MSF)", msfEdges, totalCost,
                operations, executionTime, vertices, edgeCount,
                false, graph.getName());
    }

    public boolean validateMST(Graph graph, Result result) {
        List<Edge> mstEdges = result.getMstEdges();
        int vertices = graph.getVertices();

        // 1. Check edge count
        if (mstEdges.size() != vertices - 1) {
            System.err.println("Validation FAILED: Expected " + (vertices - 1) +
                    " edges, got " + mstEdges.size());
            return false;
        }

        // 2. Check total weight consistency
        if (!validateWeightConsistency(mstEdges, result.getTotalCost())) {
            return false;
        }

        // 3. Check for cycles and spanning property
        if (!validateConnectivity(graph, mstEdges, vertices)) {
            return false;
        }

        // 4. Validate cut optimality (simplified for performance)
        // Only check for small graphs to avoid O(V^2 * E) complexity
        if (vertices <= 100) {
            if (!validateCutOptimality(graph, mstEdges, vertices)) {
                return false;
            }
        } else {
            System.out.println("Validation: Skipping cut optimality check for large graph (V=" + vertices + ")");
        }

        System.out.println("Validation PASSED: MST satisfies all optimality conditions");
        return true;
    }

    private boolean validateWeightConsistency(List<Edge> mstEdges, int expectedCost) {
        int totalWeight = 0;
        for (Edge e : mstEdges) {
            totalWeight += e.getWeight();
        }
        if (totalWeight != expectedCost) {
            System.err.println("Validation FAILED: Weight mismatch. Calculated: " +
                    totalWeight + ", Reported: " + expectedCost);
            return false;
        }
        return true;
    }

    private boolean validateConnectivity(Graph graph, List<Edge> mstEdges, int vertices) {
        UnionFind uf = new UnionFind(vertices);
        
        // Check for cycles
        for (Edge e : mstEdges) {
            int v = e.getSource();
            int w = e.getDestination();

            if (uf.connected(v, w)) {
                System.err.println("Validation FAILED: MST contains a cycle at edge " + e);
                return false;
            }
            uf.union(v, w);
        }

        // Check spanning property
        for (Edge e : graph.getEdges()) {
            int v = e.getSource();
            int w = e.getDestination();

            if (!uf.connected(v, w)) {
                System.err.println("Validation FAILED: MST is not spanning. Vertices " +
                        v + " and " + w + " are not connected.");
                return false;
            }
        }
        
        return true;
    }

    private boolean validateCutOptimality(Graph graph, List<Edge> mstEdges, int vertices) {
        // For each MST edge, verify it's the minimum weight edge crossing some cut
        for (Edge mstEdge : mstEdges) {
            UnionFind uf = new UnionFind(vertices);
            
            // Build MST without this edge
            for (Edge e : mstEdges) {
                if (e != mstEdge) {
                    uf.union(e.getSource(), e.getDestination());
                }
            }

            // Check all graph edges that cross the same cut
            for (Edge graphEdge : graph.getEdges()) {
                int v = graphEdge.getSource();
                int w = graphEdge.getDestination();

                // If this edge crosses the cut
                if (!uf.connected(v, w)) {
                    // The MST edge should have minimum weight
                    if (graphEdge.getWeight() < mstEdge.getWeight()) {
                        System.err.println("Validation FAILED: Edge " + graphEdge +
                                " violates cut optimality conditions for MST edge " + mstEdge);
                        return false;
                    }
                }
            }
        }
        
        return true;
    }

    public boolean quickValidate(Graph graph, Result result) {
        List<Edge> mstEdges = result.getMstEdges();
        int vertices = graph.getVertices();

        // Check edge count
        if (mstEdges.size() != vertices - 1) {
            return false;
        }

        // Check for cycles using Union-Find
        UnionFind uf = new UnionFind(vertices);
        int totalWeight = 0;
        
        for (Edge e : mstEdges) {
            // Check for cycle
            if (uf.connected(e.getSource(), e.getDestination())) {
                return false;
            }
            uf.union(e.getSource(), e.getDestination());
            
            // Accumulate weight
            totalWeight += e.getWeight();
        }

        // Verify total weight
        return totalWeight == result.getTotalCost();
    }
}