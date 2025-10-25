package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Result;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class PrimTest {

    private Prim prim;

    @Before
    public void setUp() {
        prim = new Prim();
    }

    @Test
    public void testSmallGraph() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        Graph graph = new Graph(nodes, "Test Graph", "Small test", 1);

        graph.addEdge("A", "B", 1);
        graph.addEdge("A", "C", 4);
        graph.addEdge("B", "C", 2);
        graph.addEdge("C", "D", 3);
        graph.addEdge("B", "D", 5);

        Result result = prim.findMST(graph);

        assertEquals("MST should have 3 edges", 3, result.getMstEdges().size());
        assertEquals("MST cost should be 6", 6, result.getTotalCost());
        assertTrue("MST should be valid", result.isValid());
    }

    @Test
    public void testMSTEdgeCount() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 4);
        graph.addEdge("A", "C", 3);
        graph.addEdge("B", "C", 2);
        graph.addEdge("B", "D", 5);
        graph.addEdge("C", "D", 7);
        graph.addEdge("C", "E", 8);
        graph.addEdge("D", "E", 6);

        Result result = prim.findMST(graph);

        int expectedEdges = graph.getVertices() - 1;
        assertEquals("MST must have V-1 edges", expectedEdges, result.getMstEdges().size());
    }

    @Test
    public void testMSTIsAcyclic() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);
        graph.addEdge("C", "D", 3);
        graph.addEdge("D", "A", 4);

        Result result = prim.findMST(graph);

        UnionFind uf = new UnionFind(graph.getVertices());
        for (Edge edge : result.getMstEdges()) {
            assertFalse("MST should not contain cycles",
                    uf.connected(edge.getSource(), edge.getDestination()));
            uf.union(edge.getSource(), edge.getDestination());
        }
    }

    @Test
    public void testMSTIsSpanning() {
        List<String> nodes = Arrays.asList("V1", "V2", "V3", "V4", "V5");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("V1", "V2", 10);
        graph.addEdge("V2", "V3", 15);
        graph.addEdge("V3", "V4", 12);
        graph.addEdge("V4", "V5", 8);
        graph.addEdge("V1", "V5", 20);

        Result result = prim.findMST(graph);

        UnionFind uf = new UnionFind(graph.getVertices());
        for (Edge edge : result.getMstEdges()) {
            uf.union(edge.getSource(), edge.getDestination());
        }

        int root = uf.find(0);
        for (int i = 1; i < graph.getVertices(); i++) {
            assertEquals("All vertices should be connected", root, uf.find(i));
        }
    }

    @Test
    public void testExecutionTimeNonNegative() {
        List<String> nodes = Arrays.asList("A", "B", "C");
        Graph graph = new Graph(nodes, "Test", "", 1);
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);

        Result result = prim.findMST(graph);

        assertTrue("Execution time should be non-negative",
                result.getExecutionTimeMs() >= 0);
    }

    @Test
    public void testOperationCountNonNegative() {
        List<String> nodes = Arrays.asList("A", "B", "C");
        Graph graph = new Graph(nodes, "Test", "", 1);
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);

        Result result = prim.findMST(graph);

        assertTrue("Operation count should be non-negative",
                result.getOperations() >= 0);
    }

    @Test
    public void testMSTCostCalculation() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 10);
        graph.addEdge("B", "C", 20);
        graph.addEdge("C", "D", 30);

        Result result = prim.findMST(graph);

        int expectedCost = 0;
        for (Edge edge : result.getMstEdges()) {
            expectedCost += edge.getWeight();
        }

        assertEquals("MST cost should match sum of edge weights",
                expectedCost, result.getTotalCost());
    }

    @Test
    public void testDifferentStartVertex() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);
        graph.addEdge("C", "D", 3);
        graph.addEdge("D", "A", 4);

        Result result1 = prim.findMST(graph);
        Result result2 = prim.findMSTFromVertex(graph, 2);

        assertEquals("MST cost should be same regardless of start vertex",
                result1.getTotalCost(), result2.getTotalCost());
    }

    @Test
    public void testDisconnectedGraph() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 1);
        graph.addEdge("C", "D", 2);

        Result result = prim.findMST(graph);

        assertEquals("MSF should have 2 edges", 2, result.getMstEdges().size());
        assertFalse("Disconnected graph MST should be invalid", result.isValid());
    }

    @Test
    public void testSingleVertex() {
        List<String> nodes = Arrays.asList("A");
        Graph graph = new Graph(nodes, "Test", "", 1);

        Result result = prim.findMST(graph);

        assertEquals("Single vertex MST should have 0 edges",
                0, result.getMstEdges().size());
        assertEquals("Single vertex MST should have 0 cost",
                0, result.getTotalCost());
    }

    @Test
    public void testDuplicateWeights() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 5);
        graph.addEdge("B", "C", 5);
        graph.addEdge("C", "D", 5);
        graph.addEdge("D", "A", 5);

        Result result = prim.findMST(graph);

        assertEquals("MST should have 3 edges", 3, result.getMstEdges().size());
        assertEquals("MST cost should be 15", 15, result.getTotalCost());
    }

    @Test
    public void testValidationMethod() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);
        graph.addEdge("C", "D", 3);
        graph.addEdge("D", "A", 4);

        Result result = prim.findMST(graph);

        boolean isValid = prim.validateMST(graph, result);
        assertTrue("Validation should pass for correct MST", isValid);
    }

    @Test
    public void testQuickValidation() {
        List<String> nodes = Arrays.asList("A", "B", "C");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);
        graph.addEdge("C", "A", 3);

        Result result = prim.findMST(graph);

        boolean isValid = prim.quickValidate(graph, result);
        assertTrue("Quick validation should pass", isValid);
    }

    @Test
    public void testNoDuplicateEdges() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);
        graph.addEdge("C", "D", 3);
        graph.addEdge("D", "A", 4);

        Result result = prim.findMST(graph);

        Set<Edge> edgeSet = new HashSet<>(result.getMstEdges());
        assertEquals("MST should not contain duplicate edges",
                result.getMstEdges().size(), edgeSet.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStartVertex() {
        List<String> nodes = Arrays.asList("A", "B", "C");
        Graph graph = new Graph(nodes, "Test", "", 1);

        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);

        prim.findMSTFromVertex(graph, 10);
    }
}