package cli;

import algorithms.Kruskal;
import algorithms.Prim;
import graph.Graph;
import metrics.Result;
import utils.CSVExporter;
import utils.JSONReader;
import utils.JSONWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MSTRunner {

    public static void main(String[] args) {
        String inputFile = "data/input/input.json";
        String outputFile = "data/output/results.json";
        String comparisonCSV = "data/output/comparison.csv";
        String summaryCSV = "data/output/summary.csv";

        if (args.length > 0) {
            inputFile = args[0];
        }
        if (args.length > 1) {
            outputFile = args[1];
        }

        try {
            System.out.println("Reading graphs from: " + inputFile);
            List<Graph> graphs = JSONReader.readGraphs(inputFile);
            System.out.println("Loaded " + graphs.size() + " graph(s)\n");

            // Initialize algorithms
            Prim prim = new Prim();
            Kruskal kruskal = new Kruskal();

            // Store all results
            List<Result> allResults = new ArrayList<>();
            List<Result> primResults = new ArrayList<>();
            List<Result> kruskalResults = new ArrayList<>();

            // Process each graph
            for (int i = 0; i < graphs.size(); i++) {
                Graph graph = graphs.get(i);

                System.out.println(String.format("PROCESSING GRAPH %d/%d: %s",
                        i + 1, graphs.size(), graph.getName()));
                System.out.println(graph.toString());

                // Run Prim's Algorithm
                System.out.println("\nRunning Prim's Algorithm...");
                Result primResult = prim.findMST(graph);
                primResults.add(primResult);
                allResults.add(primResult);
                primResult.printSummary();

                // Run Kruskal's Algorithm
                System.out.println("\nRunning Kruskal's Algorithm...");
                Result kruskalResult = kruskal.findMST(graph);
                kruskalResults.add(kruskalResult);
                allResults.add(kruskalResult);
                kruskalResult.printSummary();

                // Compare results
                System.out.println("COMPARISON");
                compareResults(primResult, kruskalResult);
            }

            JSONWriter.writeComparisonResults(primResults, kruskalResults, outputFile);

            JSONWriter.writeDetailedResults(allResults, "data/output/detailed_results.json");

            CSVExporter.exportComparison(primResults, kruskalResults, comparisonCSV);
            CSVExporter.exportSummary(allResults, summaryCSV);

            printFinalSummary(primResults, kruskalResults);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void compareResults(Result primResult, Result kruskalResult) {
        boolean sameCost = primResult.getTotalCost() == kruskalResult.getTotalCost();
        boolean sameEdgeCount = primResult.getMstEdges().size() == kruskalResult.getMstEdges().size();

        System.out.println(String.format("%-30s: %s", "Same MST Cost",
                sameCost ? "YES (" + primResult.getTotalCost() + ")" : "NO"));

        System.out.println(String.format("%-30s: %s", "Same Edge Count",
                sameEdgeCount ? "YES (" + primResult.getMstEdges().size() + ")" : "NO"));

        System.out.println(String.format("%-30s: Prim=%d, Kruskal=%d",
                "Operations", primResult.getOperations(), kruskalResult.getOperations()));

        System.out.println(String.format("%-30s: Prim=%dms, Kruskal=%dms",
                "Execution Time", primResult.getExecutionTimeMs(), kruskalResult.getExecutionTimeMs()));

        if (primResult.getExecutionTimeMs() < kruskalResult.getExecutionTimeMs()) {
            long diff = kruskalResult.getExecutionTimeMs() - primResult.getExecutionTimeMs();
            System.out.println(String.format("%-30s: Prim (faster by %dms)", "Winner (Time)", diff));
        } else if (kruskalResult.getExecutionTimeMs() < primResult.getExecutionTimeMs()) {
            long diff = primResult.getExecutionTimeMs() - kruskalResult.getExecutionTimeMs();
            System.out.println(String.format("%-30s: Kruskal (faster by %dms)", "Winner (Time)", diff));
        } else {
            System.out.println(String.format("%-30s: Tie", "Winner (Time)"));
        }

        if (primResult.getOperations() < kruskalResult.getOperations()) {
            int diff = kruskalResult.getOperations() - primResult.getOperations();
            System.out.println(String.format("%-30s: Prim (fewer by %d)", "Winner (Operations)", diff));
        } else if (kruskalResult.getOperations() < primResult.getOperations()) {
            int diff = primResult.getOperations() - kruskalResult.getOperations();
            System.out.println(String.format("%-30s: Kruskal (fewer by %d)", "Winner (Operations)", diff));
        } else {
            System.out.println(String.format("%-30s: Tie", "Winner (Operations)"));
        }

        System.out.println(String.format("%-30s: %s", "Both Valid",
                (primResult.isValid() && kruskalResult.isValid()) ? "YES" : "NO"));
    }

    private static void printFinalSummary(List<Result> primResults, List<Result> kruskalResults) {
        int totalGraphs = primResults.size();
        System.out.println("Total Graphs Processed: " + totalGraphs);

        double primAvgTime = primResults.stream()
                .mapToLong(Result::getExecutionTimeMs)
                .average()
                .orElse(0);

        double kruskalAvgTime = kruskalResults.stream()
                .mapToLong(Result::getExecutionTimeMs)
                .average()
                .orElse(0);

        System.out.println(String.format("Average Execution Time - Prim: %.2fms, Kruskal: %.2fms",
                primAvgTime, kruskalAvgTime));

        // Average operations
        double primAvgOps = primResults.stream()
                .mapToInt(Result::getOperations)
                .average()
                .orElse(0);

        double kruskalAvgOps = kruskalResults.stream()
                .mapToInt(Result::getOperations)
                .average()
                .orElse(0);

        System.out.println(String.format("Average Operations - Prim: %.0f, Kruskal: %.0f",
                primAvgOps, kruskalAvgOps));

        int primWins = 0;
        int kruskalWins = 0;
        int ties = 0;

        for (int i = 0; i < primResults.size(); i++) {
            long primTime = primResults.get(i).getExecutionTimeMs();
            long kruskalTime = kruskalResults.get(i).getExecutionTimeMs();

            if (primTime < kruskalTime) primWins++;
            else if (kruskalTime < primTime) kruskalWins++;
            else ties++;
        }

        System.out.println(String.format("\nPerformance Summary (based on execution time):"));
        System.out.println(String.format("  Prim wins: %d", primWins));
        System.out.println(String.format("  Kruskal wins: %d", kruskalWins));
        System.out.println(String.format("  Ties: %d", ties));

        // All costs match?
        boolean allCostsMatch = true;
        for (int i = 0; i < primResults.size(); i++) {
            if (primResults.get(i).getTotalCost() != kruskalResults.get(i).getTotalCost()) {
                allCostsMatch = false;
                break;
            }
        }

        System.out.println(String.format("\nAll MST costs match: %s",
                allCostsMatch ? "YES (Correctness verified!)" : "NO (Check implementation!)"));

    }
}