package utils;

import metrics.Result;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CSVExporter {

    public static void exportResults(List<Result> results, String filepath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println(Result.getCSVHeader());

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

    public static void exportSummary(List<Result> allResults, String filepath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println("Metric,Prim Average,Kruskal Average,Difference");

            // Separate Prim and Kruskal results
            List<Result> primResults = new java.util.ArrayList<>();
            List<Result> kruskalResults = new java.util.ArrayList<>();

            for (Result r : allResults) {
                if (r.getAlgorithmName().contains("Prim")) {
                    primResults.add(r);
                } else if (r.getAlgorithmName().contains("Kruskal")) {
                    kruskalResults.add(r);
                }
            }

            if (primResults.isEmpty() || kruskalResults.isEmpty()) {
                writer.println("Insufficient data for comparison");
                return;
            }

            // Calculate averages
            double primAvgTime = primResults.stream()
                    .mapToLong(Result::getExecutionTimeMs)
                    .average()
                    .orElse(0);

            double kruskalAvgTime = kruskalResults.stream()
                    .mapToLong(Result::getExecutionTimeMs)
                    .average()
                    .orElse(0);

            double primAvgOps = primResults.stream()
                    .mapToInt(Result::getOperations)
                    .average()
                    .orElse(0);

            double kruskalAvgOps = kruskalResults.stream()
                    .mapToInt(Result::getOperations)
                    .average()
                    .orElse(0);

            double primAvgCost = primResults.stream()
                    .mapToInt(Result::getTotalCost)
                    .average()
                    .orElse(0);

            double kruskalAvgCost = kruskalResults.stream()
                    .mapToInt(Result::getTotalCost)
                    .average()
                    .orElse(0);

            // Write summary
            writer.println(String.format("Execution Time (ms),%.2f,%.2f,%.2f",
                    primAvgTime, kruskalAvgTime, Math.abs(primAvgTime - kruskalAvgTime)));
            writer.println(String.format("Operations,%.0f,%.0f,%.0f",
                    primAvgOps, kruskalAvgOps, Math.abs(primAvgOps - kruskalAvgOps)));
            writer.println(String.format("MST Cost,%.0f,%.0f,%.0f",
                    primAvgCost, kruskalAvgCost, Math.abs(primAvgCost - kruskalAvgCost)));
        }

        System.out.println("Summary CSV exported to: " + filepath);
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