package utils;

import metrics.Result;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CSVExporter {

    public static void exportResults(List<Result> results, String filepath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            // Write header
            writer.println(Result.getCSVHeader());

            // Write data rows
            for (Result result : results) {
                writer.println(result.toCSVLine());
            }
        }

        System.out.println("CSV exported to: " + filepath);
    }

    public static void exportComparison(List<Result> primResults, List<Result> kruskalResults, String filepath) throws IOException {
        if (primResults.size() != kruskalResults.size()) {
            throw new IllegalArgumentException("Result lists must have the same size");
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println("Graph,Vertices,Edges,Algorithm,MST Cost,Operations,Time(ms),Faster,Fewer Ops");

            for (int i = 0; i < primResults.size(); i++) {
                Result prim = primResults.get(i);
                Result kruskal = kruskalResults.get(i);

                writer.println(String.format("%s,%d,%d,Prim,%d,%d,%d,%s,%s",
                        prim.getGraphName(),
                        prim.getGraphVertices(),
                        prim.getGraphEdges(),
                        prim.getTotalCost(),
                        prim.getOperations(),
                        prim.getExecutionTimeMs(),
                        prim.getExecutionTimeMs() < kruskal.getExecutionTimeMs() ? "YES" : "NO",
                        prim.getOperations() < kruskal.getOperations() ? "YES" : "NO"
                ));

                writer.println(String.format("%s,%d,%d,Kruskal,%d,%d,%d,%s,%s",
                        kruskal.getGraphName(),
                        kruskal.getGraphVertices(),
                        kruskal.getGraphEdges(),
                        kruskal.getTotalCost(),
                        kruskal.getOperations(),
                        kruskal.getExecutionTimeMs(),
                        kruskal.getExecutionTimeMs() < prim.getExecutionTimeMs() ? "YES" : "NO",
                        kruskal.getOperations() < prim.getOperations() ? "YES" : "NO"
                ));

                if (i < primResults.size() - 1) {
                    writer.println();
                }
            }
        }

        System.out.println("Comparison CSV exported to: " + filepath);
    }

    public static void exportComparison(Result primResult, Result kruskalResult, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Metric,Prim,Kruskal,Difference");
            
            writer.printf("Execution Time (ms),%.3f,%.3f,%.3f%n",  // Updated format
                    primResult.getExecutionTimeMs(),
                    kruskalResult.getExecutionTimeMs(),
                    Math.abs(primResult.getExecutionTimeMs() - kruskalResult.getExecutionTimeMs()));
            
            writer.printf("Operations,%d,%d,%d%n",
                    primResult.getOperationCount(),
                    kruskalResult.getOperationCount(),
                    Math.abs(primResult.getOperationCount() - kruskalResult.getOperationCount()));
            
            writer.printf("MST Cost,%d,%d,%d%n",
                    primResult.getTotalCost(),
                    kruskalResult.getTotalCost(),
                    Math.abs(primResult.getTotalCost() - kruskalResult.getTotalCost()));

            System.out.println("Comparison exported to: " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting comparison: " + e.getMessage());
        }
    }

    public static void exportSummary(List<Result> primResults, List<Result> kruskalResults, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Metric,Prim Average,Kruskal Average,Difference");
            
            double primAvgTime = primResults.stream()
                    .mapToDouble(Result::getExecutionTimeMs)
                    .average()
                    .orElse(0.0);
            
            double kruskalAvgTime = kruskalResults.stream()
                    .mapToDouble(Result::getExecutionTimeMs)
                    .average()
                    .orElse(0.0);
            
            writer.printf("Execution Time (ms),%.3f,%.3f,%.3f%n",  // Updated format
                    primAvgTime, kruskalAvgTime, Math.abs(primAvgTime - kruskalAvgTime));
            
            double primAvgOps = primResults.stream()
                    .mapToInt(Result::getOperationCount)
                    .average()
                    .orElse(0.0);
            
            double kruskalAvgOps = kruskalResults.stream()
                    .mapToInt(Result::getOperationCount)
                    .average()
                    .orElse(0.0);
            
            writer.printf("Operations,%.0f,%.0f,%.0f%n",
                    primAvgOps, kruskalAvgOps, Math.abs(primAvgOps - kruskalAvgOps));
            
            double primAvgCost = primResults.stream()
                    .mapToInt(Result::getTotalCost)
                    .average()
                    .orElse(0.0);
            
            double kruskalAvgCost = kruskalResults.stream()
                    .mapToInt(Result::getTotalCost)
                    .average()
                    .orElse(0.0);
            
            writer.printf("MST Cost,%.0f,%.0f,%.0f%n",
                    primAvgCost, kruskalAvgCost, Math.abs(primAvgCost - kruskalAvgCost));

            System.out.println("Summary exported to: " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting summary: " + e.getMessage());
        }
    }

    public static void exportPerformanceBySize(List<Result> results, String filepath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println("Vertices,Algorithm,Avg Time (ms),Avg Operations,Test Count");

            java.util.Map<String, java.util.List<Result>> grouped = new java.util.HashMap<>();

            for (Result r : results) {
                String key = r.getGraphVertices() + "-" +
                        (r.getAlgorithmName().contains("Prim") ? "Prim" : "Kruskal");
                grouped.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(r);
            }

            // Write grouped data
            grouped.forEach((key, group) -> {
                String[] parts = key.split("-");
                int vertices = Integer.parseInt(parts[0]);
                String algorithm = parts[1];

                double avgTime = group.stream().mapToLong(Result::getExecutionTimeMs).average().orElse(0);
                double avgOps = group.stream().mapToInt(Result::getOperations).average().orElse(0);

                writer.println(String.format("%d,%s,%.2f,%.0f,%d",
                        vertices, algorithm, avgTime, avgOps, group.size()));
            });
        }

        System.out.println("Performance by size CSV exported to: " + filepath);
    }
}