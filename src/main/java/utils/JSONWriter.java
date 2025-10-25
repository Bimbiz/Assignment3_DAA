package utils;

import com.google.gson.*;
import graph.Edge;
import metrics.Result;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JSONWriter {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void writeComparisonResults(List<Result> primResults, List<Result> kruskalResults, String filepath) throws IOException {
        if (primResults.size() != kruskalResults.size()) {
            throw new IllegalArgumentException("Result lists must have same size");
        }

        JsonObject root = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        for (int i = 0; i < primResults.size(); i++) {
            Result primResult = primResults.get(i);
            Result kruskalResult = kruskalResults.get(i);

            JsonObject resultObj = new JsonObject();

            // Graph ID (extract from graph name or use index)
            int graphId = i + 1;
            if (primResult.getGraphName() != null && primResult.getGraphName().contains("Graph ")) {
                try {
                    String idStr = primResult.getGraphName().replace("Graph ", "").trim();
                    graphId = Integer.parseInt(idStr);
                } catch (NumberFormatException e) {
                }
            }
            resultObj.addProperty("graph_id", graphId);

            JsonObject inputStats = new JsonObject();
            inputStats.addProperty("vertices", primResult.getGraphVertices());
            inputStats.addProperty("edges", primResult.getGraphEdges());
            resultObj.add("input_stats", inputStats);

            resultObj.add("prim", createAlgorithmResult(primResult));

            resultObj.add("kruskal", createAlgorithmResult(kruskalResult));

            resultsArray.add(resultObj);
        }

        root.add("results", resultsArray);

        try (FileWriter writer = new FileWriter(filepath)) {
            gson.toJson(root, writer);
        }

        System.out.println("Results written to: " + filepath);
    }

    private static JsonObject createAlgorithmResult(Result result) {
        JsonObject obj = new JsonObject();
        JsonArray edgesArray = new JsonArray();
        for (Edge edge : result.getMstEdges()) {
            JsonObject edgeObj = new JsonObject();
            edgeObj.addProperty("from", edge.getFromName());
            edgeObj.addProperty("to", edge.getToName());
            edgeObj.addProperty("weight", edge.getWeight());
            edgesArray.add(edgeObj);
        }
        
        obj.add("mst_edges", edgesArray);
        obj.addProperty("total_cost", result.getTotalCost());
        obj.addProperty("operations_count", result.getOperationCount());
        obj.addProperty("execution_time_ms", 
                    Math.round(result.getExecutionTimeMs() * 1000.0) / 1000.0); // Round to 3 decimal places
        
        return obj;
    }

    public static void writeDetailedResults(List<Result> results, String filepath) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        for (Result result : results) {
            JsonObject resultObj = createDetailedResultObject(result);
            resultsArray.add(resultObj);
        }

        root.add("results", resultsArray);
        root.addProperty("total_tests", results.size());
        root.addProperty("timestamp", System.currentTimeMillis());

        try (FileWriter writer = new FileWriter(filepath)) {
            gson.toJson(root, writer);
        }

        System.out.println("Detailed results written to: " + filepath);
    }

    private static JsonObject createDetailedResultObject(Result result) {
        JsonObject obj = new JsonObject();

        obj.addProperty("algorithm", result.getAlgorithmName());
        obj.addProperty("graph_name", result.getGraphName());
        obj.addProperty("total_cost", result.getTotalCost());
        obj.addProperty("mst_edge_count", result.getMstEdges().size());
        obj.addProperty("operations", result.getOperations());
        obj.addProperty("execution_time_ms", result.getExecutionTimeMs());
        obj.addProperty("valid", result.isValid());

        JsonArray edgesArray = new JsonArray();
        for (Edge edge : result.getMstEdges()) {
            JsonObject edgeObj = new JsonObject();
            edgeObj.addProperty("from", edge.getFromNode());
            edgeObj.addProperty("to", edge.getToNode());
            edgeObj.addProperty("weight", edge.getWeight());
            edgesArray.add(edgeObj);
        }
        obj.add("mst_edges", edgesArray);

        return obj;
    }

    public static void appendResult(Result result, String filepath) throws IOException {
        JsonObject root;

        try (FileReader reader = new java.io.FileReader(filepath)) {
            root = gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            root = new JsonObject();
            root.add("results", new JsonArray());
        }

        JsonArray results = root.getAsJsonArray("results");
        results.add(createDetailedResultObject(result));

        root.addProperty("total_tests", results.size());
        root.addProperty("timestamp", System.currentTimeMillis());

        try (FileWriter writer = new FileWriter(filepath)) {
            gson.toJson(root, writer);
        }
    }
}