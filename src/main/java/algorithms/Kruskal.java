package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Result;

import java.util.*;

/**
 * Implementation of Kruskal's algorithm for finding Minimum Spanning Tree.
 * Uses Union-Find data structure to detect cycles efficiently.
 *
 * Algorithm:
 * 1. Sort all edges by weight (ascending)
 * 2. Initialize Union-Find with all vertices
 * 3. For each edge (in sorted order):
 *    - If endpoints are in different sets: add edge to MST and union sets
 *    - Otherwise: skip (would create cycle)
 * 4. Stop when MST has V-1 edges
 *
 * Time Complexity: O(E log E) where E is the number of edges
 * Space Complexity: O(V + E) where V is the number of vertices
 *
 */
public class Kruskal {
    private static final double EPSILON = 1E-12;

    /**
     * Finds the Minimum Spanning Tree using Kruskal's algorithm.
     *
     * @param graph The input graph
     * @return Result containing MST edges, cost, operations, and execution time
     */
    public Result findMST(Graph graph) {
        long startTime = System.nanoTime();

        int vertices = graph.getVertices();
        int edgeCount = graph.getEdgeCount();
        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;
        int operations = 0;
        String graphName = graph.getName();

        // Check if graph is connected
        if (!graph.isConnected()) {
            long endTime = System.nanoTime();
            long executionTime = (endTime - startTime) / 1_000_000;
            System.err.println("WARNING: Graph is disconnected. Computing minimum spanning forest.");

            // For disconnected graphs, still compute MSF
            return computeMSF(graph, startTime);
        }

        // Step 1: Get all edges and sort them by weight
        List<Edge> edges = new ArrayList<>(graph.getEdges());
        Collections.sort(edges);
        operations += edges.size(); // Count sort operations (simplified)

        // Step 2: Initialize Union-Find
        UnionFind uf = new UnionFind(vertices);

        // Step 3: Process edges in sorted order (greedy approach)
        for (Edge edge : edges) {
            operations++; // Count edge examination

            int source = edge.getSource();
            int dest = edge.getDestination();

            // Check if adding this edge creates a cycle
            if (!uf.connected(source, dest)) {
                // No cycle - add edge to MST
                mstEdges.add(edge);
                totalCost += edge.getWeight();
                uf.union(source, dest);

                // Stop when we have V-1 edges (spanning tree complete)
                if (mstEdges.size() == vertices - 1) {
                    break;
                }
            }
        }

        // Add Union-Find operations to total count
        operations += uf.getOperationCount();

        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1_000_000; // Convert to milliseconds

        // Create result
        Result result = new Result("Kruskal's Algorithm", mstEdges, totalCost,
                operations, executionTime, vertices, edgeCount,
                true, graphName);

        // Validate the MST using Princeton-style checks
        boolean isValid = validateMST(graph, result);

        return new Result("Kruskal's Algorithm", mstEdges, totalCost,
                operations, executionTime, vertices, edgeCount,
                isValid, graphName);
    }

    /**
     * Computes Minimum Spanning Forest for disconnected graphs.
     *
     * @param graph The input graph
     * @param startTime Start time for timing
     * @return Result containing MSF edges
     */
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

    /**
     * Validates the MST using Princeton-style optimality conditions.
     *
     * Checks:
     * 1. MST has exactly V-1 edges
     * 2. MST is acyclic (no cycles)
     * 3. MST is a spanning tree (connects all vertices)
     * 4. Total weight is correctly calculated
     * 5. Cut optimality: each edge in MST is minimum weight crossing some cut
     *
     * @param graph Original graph
     * @param result MST result to validate
     * @return true if MST is valid
     */
    public boolean validateMST(Graph graph, Result result) {
        List<Edge> mstEdges = result.getMstEdges();
        int vertices = graph.getVertices();

        // Check 1: MST should have exactly V-1 edges
        if (mstEdges.size() != vertices - 1) {
            System.err.println("Validation FAILED: Expected " + (vertices - 1) +
                    " edges, got " + mstEdges.size());
            return false;
        }

        // Check 2: Verify total weight
        int totalWeight = 0;
        for (Edge e : mstEdges) {
            totalWeight += e.getWeight();
        }
        if (totalWeight != result.getTotalCost()) {
            System.err.println("Validation FAILED: Weight mismatch. Calculated: " +
                    totalWeight + ", Reported: " + result.getTotalCost());
            return false;
        }

        // Check 3: MST must be acyclic (no cycles)
        UnionFind uf = new UnionFind(vertices);
        for (Edge e : mstEdges) {
            int v = e.getSource();
            int w = e.getDestination();

            if (uf.connected(v, w)) {
                System.err.println("Validation FAILED: MST contains a cycle at edge " + e);
                return false;
            }
            uf.union(v, w);
        }

        // Check 4: MST must be spanning (connects all vertices)
        for (Edge e : graph.getEdges()) {
            int v = e.getSource();
            int w = e.getDestination();

            if (!uf.connected(v, w)) {
                System.err.println("Validation FAILED: MST is not spanning. Vertices " +
                        v + " and " + w + " are not connected.");
                return false;
            }
        }

        // Check 5: Cut optimality - each MST edge is minimum weight crossing some cut
        // This is the most rigorous check (Princeton-style)
        for (Edge mstEdge : mstEdges) {
            // Remove this edge from MST temporarily
            uf = new UnionFind(vertices);
            for (Edge e : mstEdges) {
                if (e != mstEdge) {
                    uf.union(e.getSource(), e.getDestination());
                }
            }

            // Check if mstEdge is minimum weight edge crossing the cut
            for (Edge graphEdge : graph.getEdges()) {
                int v = graphEdge.getSource();
                int w = graphEdge.getDestination();

                // If this edge crosses the same cut as mstEdge
                if (!uf.connected(v, w)) {
                    // It should not have weight less than mstEdge
                    if (graphEdge.getWeight() < mstEdge.getWeight()) {
                        System.err.println("Validation FAILED: Edge " + graphEdge +
                                " violates cut optimality conditions for MST edge " + mstEdge);
                        return false;
                    }
                }
            }
        }

        System.out.println("Validation PASSED: MST satisfies all optimality conditions");
        return true;
    }

    /**
     * Quick validation without cut optimality check (faster).
     *
     * @param graph Original graph
     * @param result MST result to validate
     * @return true if MST passes basic checks
     */
    public boolean quickValidate(Graph graph, Result result) {
        List<Edge> mstEdges = result.getMstEdges();
        int vertices = graph.getVertices();

        // Check edge count
        if (mstEdges.size() != vertices - 1) {
            return false;
        }

        // Check for cycles
        UnionFind uf = new UnionFind(vertices);
        for (Edge e : mstEdges) {
            if (uf.connected(e.getSource(), e.getDestination())) {
                return false;
            }
            uf.union(e.getSource(), e.getDestination());
        }

        // Check weight
        int totalWeight = 0;
        for (Edge e : mstEdges) {
            totalWeight += e.getWeight();
        }

        return totalWeight == result.getTotalCost();
    }
}