package algorithms;

import graph.Graph;
import metrics.Result;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit tests comparing Prim's and Kruskal's algorithms.
 * Ensures both algorithms produce identical MST costs.
 *
 * @author Assignment 3 - MST
 * @version 1.0
 */
public class AlgorithmComparisonTest {

    private Prim prim;
    private Kruskal kruskal;

    @Before
    public void setUp() {
        prim = new Prim();
        kruskal = new Kruskal();
    }

    /**
     * Test 1: Both algorithms should produce same MST cost
     */
    @Test
    public void testSameMSTCost() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 1);
        graph.addEdge("A", "C", 4);
        graph.addEdge("B", "C", 2);
        graph.addEdge("C", "D", 3);
        graph.addEdge("B", "D", 5);

        Result primResult = prim.findMST(graph);
        Result kruskalResult = kruskal.findMST(graph);

        assertEquals("Both algorithms should produce same MST cost",
                primResult.getTotalCost(), kruskalResult.getTotalCost());
    }

    /**
     * Test 2: Both algorithms should produce same edge count
     */
    @Test
    public void testSameEdgeCount() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 4);
        graph.addEdge("A", "C", 3);
        graph.addEdge("B", "C", 2);
        graph.addEdge("B", "D", 5);
        graph.addEdge("C", "D", 7);
        graph.addEdge("C", "E", 8);
        graph.addEdge("D", "E", 6);

        Result primResult = prim.findMST(graph);
        Result kruskalResult = kruskal.findMST(graph);

        assertEquals("Both algorithms should produce same edge count",
                primResult.getMstEdges().size(), kruskalResult.getMstEdges().size());
    }

    /**
     * Test 3: Both should produce valid MSTs
     */
    @Test
    public void testBothValid() {
        List<String> nodes = Arrays.asList("V1", "V2", "V3", "V4", "V5");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("V1", "V2", 10);
        graph.addEdge("V2", "V3", 15);
        graph.addEdge("V3", "V4", 12);
        graph.addEdge("V4", "V5", 8);
        graph.addEdge("V1", "V5", 20);
        graph.addEdge("V2", "V4", 25);

        Result primResult = prim.findMST(graph);
        Result kruskalResult = kruskal.findMST(graph);

        assertTrue("Prim's MST should be valid", primResult.isValid());
        assertTrue("Kruskal's MST should be valid", kruskalResult.isValid());
    }

    /**
     * Test 4: Test on larger graph
     */
    @Test
    public void testLargerGraph() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H");
        Graph graph = new Graph(nodes, "Large Test", "", 1);

        graph.addEdge("A", "B", 5);
        graph.addEdge("A", "C", 7);
        graph.addEdge("B", "C", 3);
        graph.addEdge("B", "D", 8);
        graph.addEdge("C", "D", 4);
        graph.addEdge("C", "E", 6);
        graph.addEdge("D", "E", 10);
        graph.addEdge("D", "F", 9);
        graph.addEdge("E", "F", 2);
        graph.addEdge("E", "G", 11);
        graph.addEdge("F", "G", 7);
        graph.addEdge("F", "H", 12);
        graph.addEdge("G", "H", 5);

        Result primResult = prim.findMST(graph);
        Result kruskalResult = kruskal.findMST(graph);

        assertEquals("Both should produce same cost on larger graph",
                primResult.getTotalCost(), kruskalResult.getTotalCost());
        assertEquals("Both should have V-1 edges",
                graph.getVertices() - 1, primResult.getMstEdges().size());
        assertEquals("Both should have V-1 edges",
                graph.getVertices() - 1, kruskalResult.getMstEdges().size());
    }

    /**
     * Test 5: Test with duplicate weights
     */
    @Test
    public void testDuplicateWeights() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 5);
        graph.addEdge("B", "C", 5);
        graph.addEdge("C", "D", 5);
        graph.addEdge("D", "E", 5);
        graph.addEdge("A", "E", 10);

        Result primResult = prim.findMST(graph);
        Result kruskalResult = kruskal.findMST(graph);

        assertEquals("Both should handle duplicate weights correctly",
                primResult.getTotalCost(), kruskalResult.getTotalCost());
    }

    /**
     * Test 6: Both should handle disconnected graphs
     */
    @Test
    public void testDisconnectedGraphs() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E", "F");
        Graph graph = new Graph(nodes, "Test", "", 1);

        // Component 1: A-B-C
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);

        // Component 2: D-E-F
        graph.addEdge("D", "E", 3);
        graph.addEdge("E", "F", 4);

        Result primResult = prim.findMST(graph);
        Result kruskalResult = kruskal.findMST(graph);

        // Both should produce MSF with same total cost
        assertEquals("Both should produce same MSF cost",
                primResult.getTotalCost(), kruskalResult.getTotalCost());

        // Both should be marked as invalid (not spanning tree)
        assertFalse("Prim's result should be invalid for disconnected graph",
                primResult.isValid());
        assertFalse("Kruskal's result should be invalid for disconnected graph",
                kruskalResult.isValid());
    }

    /**
     * Test 7: Results should be reproducible
     */
    @Test
    public void testReproducibility() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);
        graph.addEdge("C", "D", 3);
        graph.addEdge("D", "A", 4);

        // Run Prim multiple times
        Result prim1 = prim.findMST(graph);
        Result prim2 = prim.findMST(graph);

        // Run Kruskal multiple times
        Result kruskal1 = kruskal.findMST(graph);
        Result kruskal2 = kruskal.findMST(graph);

        assertEquals("Prim should produce same result",
                prim1.getTotalCost(), prim2.getTotalCost());
        assertEquals("Kruskal should produce same result",
                kruskal1.getTotalCost(), kruskal2.getTotalCost());
    }

    /**
     * Test 8: Complete graph (all vertices connected)
     */
    @Test
    public void testCompleteGraph() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        Graph graph = new Graph(nodes, "Complete", "", 1);

        // All possible edges
        graph.addEdge("A", "B", 1);
        graph.addEdge("A", "C", 2);
        graph.addEdge("A", "D", 3);
        graph.addEdge("B", "C", 4);
        graph.addEdge("B", "D", 5);
        graph.addEdge("C", "D", 6);

        Result primResult = prim.findMST(graph);
        Result kruskalResult = kruskal.findMST(graph);

        assertEquals("Both should produce same MST on complete graph",
                primResult.getTotalCost(), kruskalResult.getTotalCost());

        // Expected MST: 1 + 2 + 3 = 6 (edges A-B, A-C, A-D)
        assertEquals("MST cost should be 6", 6, primResult.getTotalCost());
    }

    /**
     * Test 9: Linear graph (path)
     */
    @Test
    public void testLinearGraph() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E");
        Graph graph = new Graph(nodes, "Linear", "", 1);

        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);
        graph.addEdge("C", "D", 3);
        graph.addEdge("D", "E", 4);

        Result primResult = prim.findMST(graph);
        Result kruskalResult = kruskal.findMST(graph);

        // Linear graph - MST is the graph itself
        assertEquals("Both should produce same cost", 10, primResult.getTotalCost());
        assertEquals("Both should produce same cost", 10, kruskalResult.getTotalCost());
    }

    /**
     * Test 10: Performance consistency
     */
    @Test
    public void testPerformanceMetrics() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);
        graph.addEdge("C", "D", 3);
        graph.addEdge("D", "E", 4);
        graph.addEdge("A", "E", 5);

        Result primResult = prim.findMST(graph);
        Result kruskalResult = kruskal.findMST(graph);

        // Both should have valid performance metrics
        assertTrue("Prim operations should be positive",
                primResult.getOperations() > 0);
        assertTrue("Kruskal operations should be positive",
                kruskalResult.getOperations() > 0);
        assertTrue("Prim time should be non-negative",
                primResult.getExecutionTimeMs() >= 0);
        assertTrue("Kruskal time should be non-negative",
                kruskalResult.getExecutionTimeMs() >= 0);
    }
}