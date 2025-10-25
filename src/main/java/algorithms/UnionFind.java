package algorithms;

public class UnionFind {
    private final int[] parent;
    private final int[] rank;
    private int components;
    private int operationCount;

    public UnionFind(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Number of elements must be positive");
        }

        parent = new int[n];
        rank = new int[n];
        components = n;
        operationCount = 0;

        // Each element is its own parent initially
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int x) {
        operationCount++;

        if (x < 0 || x >= parent.length) {
            throw new IllegalArgumentException("Element index out of bounds");
        }

        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public boolean union(int x, int y) {

        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false;
        }

        operationCount++;

        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }

        components--;
        return true;
    }

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    public int getOperationCount() {
        return operationCount;
    }

    public void resetOperationCount() {
        operationCount = 0;
    }

    public int getComponentCount() {
        return components;
    }

    public int size() {
        return parent.length;
    }

    @Override
    public String toString() {
        return String.format("UnionFind: %d elements, %d components, %d operations",
                parent.length, components, operationCount);
    }
}