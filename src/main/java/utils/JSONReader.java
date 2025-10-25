package utils;

import com.google.gson.*;
import graph.Graph;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONReader {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static List<Graph> readGraphs(String filepath) throws IOException {
        List<Graph> graphs = new ArrayList<>();

        try (FileReader reader = new FileReader(filepath)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            if (root.has("graphs")) {
                JsonArray graphsArray = root.getAsJsonArray("graphs");
                for (JsonElement element : graphsArray) {
                    JsonObject graphJson = element.getAsJsonObject();
                    graphs.add(parseGraph(graphJson));
                }
            } else if (root.has("datasets")) {
                JsonArray datasets = root.getAsJsonArray("datasets");
                for (JsonElement element : datasets) {
                    graphs.add(parseOldFormat(element.getAsJsonObject()));
                }
            } else {
                graphs.add(parseGraph(root));
            }
        }

        return graphs;
    }

    private static Graph parseGraph(JsonObject graphJson) {
        int graphId = graphJson.has("id") ? graphJson.get("id").getAsInt() : -1;

        JsonArray nodesArray = graphJson.getAsJsonArray("nodes");
        List<String> nodeNames = new ArrayList<>();
        for (JsonElement node : nodesArray) {
            nodeNames.add(node.getAsString());
        }

        String name = "Graph " + graphId;
        String description = graphJson.has("description") ?
                graphJson.get("description").getAsString() : "";

        Graph graph = new Graph(nodeNames, name, description, graphId);

        JsonArray edgesArray = graphJson.getAsJsonArray("edges");
        for (JsonElement edgeElement : edgesArray) {
            JsonObject edgeObj = edgeElement.getAsJsonObject();

            String from = edgeObj.get("from").getAsString();
            String to = edgeObj.get("to").getAsString();
            int weight = edgeObj.get("weight").getAsInt();

            graph.addEdge(from, to, weight);
        }

        return graph;
    }

    private static Graph parseOldFormat(JsonObject jsonObject) {
        int vertices = jsonObject.get("vertices").getAsInt();
        String name = jsonObject.has("name") ? jsonObject.get("name").getAsString() : "Unnamed";
        String description = jsonObject.has("description") ? jsonObject.get("description").getAsString() : "";

        Graph graph = new Graph(vertices);
        graph.setName(name);
        graph.setDescription(description);

        if (jsonObject.has("edges")) {
            JsonArray edgesArray = jsonObject.getAsJsonArray("edges");
            for (JsonElement edgeElement : edgesArray) {
                JsonObject edgeObj = edgeElement.getAsJsonObject();
                int source = edgeObj.get("source").getAsInt();
                int destination = edgeObj.get("destination").getAsInt();
                int weight = edgeObj.get("weight").getAsInt();
                graph.addEdge(source, destination, weight);
            }
        }

        return graph;
    }

    public static boolean validateJSONStructure(String filepath) {
        try (FileReader reader = new FileReader(filepath)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            if (root.has("graphs")) {
                JsonArray graphs = root.getAsJsonArray("graphs");
                for (JsonElement element : graphs) {
                    if (!validateGraphStructure(element.getAsJsonObject())) {
                        return false;
                    }
                }
            } else {
                return validateGraphStructure(root);
            }

            return true;
        } catch (Exception e) {
            System.err.println("Invalid JSON structure: " + e.getMessage());
            return false;
        }
    }

    private static boolean validateGraphStructure(JsonObject graphJson) {
        if (!graphJson.has("nodes")) {
            System.err.println("Missing 'nodes' field");
            return false;
        }

        if (!graphJson.has("edges")) {
            System.err.println("Missing 'edges' field");
            return false;
        }

        JsonArray edges = graphJson.getAsJsonArray("edges");
        for (JsonElement edgeElement : edges) {
            JsonObject edge = edgeElement.getAsJsonObject();
            if (!edge.has("from") || !edge.has("to") || !edge.has("weight")) {
                System.err.println("Edge missing required fields (from, to, weight)");
                return false;
            }
        }

        return true;
    }

    public static void printJSONSummary(String filepath) {
        try {
            List<Graph> graphs = readGraphs(filepath);

            System.out.println("JSON FILE SUMMARY: " + filepath);
            System.out.println("Total graphs: " + graphs.size());
            System.out.println();

            for (Graph g : graphs) {
                System.out.println(String.format("[ID: %d] %s", g.getGraphId(), g.getName()));
                System.out.println(String.format("    Nodes: %s", g.getNodeNames()));
                System.out.println(String.format("    Vertices: %d, Edges: %d, Connected: %s",
                        g.getVertices(), g.getEdgeCount(),
                        g.isConnected() ? "Yes" : "No"));
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}