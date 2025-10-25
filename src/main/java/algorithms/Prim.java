package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Result;

import java.util.*;

public class Prim {

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

        boolean[] inMST = new boolean[vertices];

        PriorityQueue<Edge> pq = new PriorityQueue<>();

        int startVertex = 0;
        inMST[startVertex] = true;

        for (Edge edge : graph.getAdjacentEdges(startVertex)) {
            pq.offer(edge);
            operations++;
        }

        while (!pq.isEmpty() && mstEdges.size() < vertices - 1) {
            Edge edge = pq.poll();
            operations++;

            int dest = edge.getDestination();

            if (inMST[dest]) {
                continue;
            }

            mstEdges.add(edge);
            totalCost += edge.getWeight();
            inMST[dest] = true;

            for (Edge nextEdge : graph.getAdjacentEdges(dest)) {
                operations++;

                int nextDest = nextEdge.getDestination();
                if (!inMST[nextDest]) {
                    pq.offer(nextEdge);
                }
            }
        }

        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0; // Changed to double division

        // Create result and validate
        Result result = new Result("Prim's Algorithm", mstEdges, totalCost,
                operations, executionTimeMs, vertices, edgeCount,
                true, graphName);

        boolean isValid = validateMST(graph, result);

        return new Result("Prim's Algorithm", mstEdges, totalCost,
                operations, executionTime, vertices, edgeCount,
                isValid, graphName);
    }

    public Result findMSTFromVertex(Graph graph, int startVertex) {
        if (startVertex < 0 || startVertex >= graph.getVertices()) {
            throw new IllegalArgumentException("Invalid start vertex: " + startVertex);
        }

        long startTime = System.nanoTime();

        int vertices = graph.getVertices();
        int edgeCount = graph.getEdgeCount();
        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;
        int operations = 0;

        boolean[] inMST = new boolean[vertices];
        PriorityQueue<Edge> pq = new PriorityQueue<>();

        inMST[startVertex] = true;

        for (Edge edge : graph.getAdjacentEdges(startVertex)) {
            pq.offer(edge);
            operations++;
        }

        while (!pq.isEmpty() && mstEdges.size() < vertices - 1) {
            Edge edge = pq.poll();
            operations++;

            int dest = edge.getDestination();

            if (inMST[dest]) {
                continue;
            }

            mstEdges.add(edge);
            totalCost += edge.getWeight();
            inMST[dest] = true;

            for (Edge nextEdge : graph.getAdjacentEdges(dest)) {
                operations++;
                int nextDest = nextEdge.getDestination();
                if (!inMST[nextDest]) {
                    pq.offer(nextEdge);
                }
            }
        }

        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1_000_000;

        String algorithmName = "Prim's Algorithm (from vertex " + startVertex + ")";
        Result result = new Result(algorithmName, mstEdges, totalCost,
                operations, executionTime, vertices, edgeCount,
                true, graph.getName());

        boolean isValid = validateMST(graph, result);

        return new Result(algorithmName, mstEdges, totalCost,
                operations, executionTime, vertices, edgeCount,
                isValid, graph.getName());
    }

    private Result computeMSF(Graph graph, long startTime) {
        int vertices = graph.getVertices();
        int edgeCount = graph.getEdgeCount();
        List<Edge> msfEdges = new ArrayList<>();
        int totalCost = 0;
        int operations = 0;

        boolean[] visited = new boolean[vertices];

        for (int start = 0; start < vertices; start++) {
            if (!visited[start]) {
                boolean[] inMST = new boolean[vertices];
                PriorityQueue<Edge> pq = new PriorityQueue<>();

                inMST[start] = true;
                visited[start] = true;

                for (Edge edge : graph.getAdjacentEdges(start)) {
                    pq.offer(edge);
                    operations++;
                }

                while (!pq.isEmpty()) {
                    Edge edge = pq.poll();
                    operations++;

                    int dest = edge.getDestination();

                    if (inMST[dest]) {
                        continue;
                    }

                    msfEdges.add(edge);
                    totalCost += edge.getWeight();
                    inMST[dest] = true;
                    visited[dest] = true;

                    for (Edge nextEdge : graph.getAdjacentEdges(dest)) {
                        operations++;
                        int nextDest = nextEdge.getDestination();
                        if (!inMST[nextDest]) {
                            pq.offer(nextEdge);
                        }
                    }
                }
            }
        }

        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1_000_000;

        return new Result("Prim's Algorithm (MSF)", msfEdges, totalCost,
                operations, executionTime, vertices, edgeCount,
                false, graph.getName());
    }

    public boolean validateMST(Graph graph, Result result) {
        List<Edge> mstEdges = result.getMstEdges();
        int vertices = graph.getVertices();

        if (mstEdges.size() != vertices - 1) {
            System.err.println("Validation FAILED: Expected " + (vertices - 1) +
                    " edges, got " + mstEdges.size());
            return false;
        }

        int totalWeight = 0;
        for (Edge e : mstEdges) {
            totalWeight += e.getWeight();
        }
        if (totalWeight != result.getTotalCost()) {
            System.err.println("Validation FAILED: Weight mismatch");
            return false;
        }

        UnionFind uf = new UnionFind(vertices);
        for (Edge e : mstEdges) {
            if (uf.connected(e.getSource(), e.getDestination())) {
                System.err.println("Validation FAILED: MST contains a cycle");
                return false;
            }
            uf.union(e.getSource(), e.getDestination());
        }

        for (Edge e : graph.getEdges()) {
            if (!uf.connected(e.getSource(), e.getDestination())) {
                System.err.println("Validation FAILED: MST is not spanning");
                return false;
            }
        }

        for (Edge mstEdge : mstEdges) {
            uf = new UnionFind(vertices);
            for (Edge e : mstEdges) {
                if (e != mstEdge) {
                    uf.union(e.getSource(), e.getDestination());
                }
            }

            for (Edge graphEdge : graph.getEdges()) {
                if (!uf.connected(graphEdge.getSource(), graphEdge.getDestination())) {
                    if (graphEdge.getWeight() < mstEdge.getWeight()) {
                        System.err.println("Validation FAILED: Cut optimality violated");
                        return false;
                    }
                }
            }
        }

        System.out.println("Validation PASSED: MST satisfies all optimality conditions");
        return true;
    }

    public boolean quickValidate(Graph graph, Result result) {
        List<Edge> mstEdges = result.getMstEdges();
        int vertices = graph.getVertices();

        if (mstEdges.size() != vertices - 1) {
            return false;
        }

        UnionFind uf = new UnionFind(vertices);
        for (Edge e : mstEdges) {
            if (uf.connected(e.getSource(), e.getDestination())) {
                return false;
            }
            uf.union(e.getSource(), e.getDestination());
        }

        int totalWeight = 0;
        for (Edge e : mstEdges) {
            totalWeight += e.getWeight();
        }

        return totalWeight == result.getTotalCost();
    }
}