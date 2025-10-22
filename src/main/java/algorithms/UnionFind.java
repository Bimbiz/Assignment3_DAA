package algorithms;

/**
 * Union-Find (Disjoint Set) data structure with path compression and union by rank.
 * Used in Kruskal's algorithm to detect cycles efficiently.
 *
 * Time Complexity:
 * - find(): O(α(n)) amortized, where α is the inverse Ackermann function
 * - union(): O(α(n)) amortized
 *
 * Space Complexity: O(n)
 *
 * @author Assignment 3 - MST
 * @version 1.0
 */
public class UnionFind {
    private final int[] parent;
    private final int[] rank;
    private int components;
    private int operationCount;

    /**
     * Initializes the Union-Find structure with n elements.
     * Initially, each element is in its own set.
     *
     * @param n Number of elements (vertices)
     */
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

    /**
     * Finds the root of the set containing x with path compression.
     * Path compression flattens the structure for faster future queries.
     *
     * @param x Element to find
     * @return Root of the set containing x
     */
    public int find(int x) {
        operationCount++;

        if (x < 0 || x >= parent.length) {
            throw new IllegalArgumentException("Element index out of bounds");
        }

        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }

    /**
     * Unites the sets containing x and y using union by rank.
     * Union by rank attaches the shorter tree under the taller tree.
     *
     * @param x First element
     * @param y Second element
     * @return true if union was performed, false if already in same set
     */
    public boolean union(int x, int y) {
        operationCount++;

        int rootX = find(x);
        int rootY = find(y);

        // Already in the same set - would create a cycle
        if (rootX == rootY) {
            return false;
        }

        // Union by rank: attach smaller tree under larger tree
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

    /**
     * Checks if x and y are in the same set (connected).
     *
     * @param x First element
     * @param y Second element
     * @return true if x and y are in the same set
     */
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    /**
     * Gets the total number of operations performed (find and union calls).
     * Used for performance analysis.
     *
     * @return total operation count
     */
    public int getOperationCount() {
        return operationCount;
    }

    /**
     * Resets the operation counter to zero.
     */
    public void resetOperationCount() {
        operationCount = 0;
    }

    /**
     * Gets the number of disjoint sets (connected components).
     *
     * @return number of components
     */
    public int getComponentCount() {
        return components;
    }

    /**
     * Gets the size of the Union-Find structure.
     *
     * @return number of elements
     */
    public int size() {
        return parent.length;
    }

    @Override
    public String toString() {
        return String.format("UnionFind: %d elements, %d components, %d operations",
                parent.length, components, operationCount);
    }
}