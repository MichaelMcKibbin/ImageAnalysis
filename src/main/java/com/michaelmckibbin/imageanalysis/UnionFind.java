package com.michaelmckibbin.imageanalysis;

/**
 * Union-Find (Disjoint Set Union) implementation with
 * Path Compression and Union by Rank.
 */

public class UnionFind {
    private int[] parent; // Stores the parent of each element
    private int[] rank;   // Stores the rank of each set (tree depth)

    /**
     * Constructor to initialize Union-Find data structure.
     * Each element is its own parent initially (disjoint sets),
     * and the rank of each set is initialized to 0.
     *
     * @param size The number of elements in the Union-Find structure.
     */

    public UnionFind(int size) {
        parent = new int[size];
        rank = new int[size];

        // Initialize each element to be its own parent (self-loop)
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0; // Initially, all elements have rank 0
        }
    }

    /**
     * Finds the representative (root) of the set containing x.
     * Uses path compression to make future operations more efficient.
     *
     * @param x The element to find.
     * @return The root representative of the set containing x.
     */

    public int find(int x) {
        if (x < 0 || x >= parent.length) { // input validation
            throw new IllegalArgumentException("Index out of bounds");
        }
        if (parent[x] != x) {
            // Path compression: make x's parent point directly to root
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    /**
     * Merges the sets containing x and y.
     * Uses union by rank to keep the tree balanced.
     *
     * @param x An element in the first set.
     * @param y An element in the second set.
     */

    public void union(int x, int y) {
        int rootX = find(x); // Find root of x
        int rootY = find(y); // Find root of y

        // Only merge if they are in different sets
        if (rootX != rootY) {
            // Attach the smaller tree to the larger tree
            if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX; // rootX becomes the root
            } else if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY; // rootY becomes the root
            } else {
                // If ranks are equal, arbitrarily choose one as root
                parent[rootY] = rootX;
                rank[rootX]++; // Increase rank since tree height increases
            }
        }
    }

        /**
         * Checks if two elements are in the same set.
         *
         * @param x The first element.
         * @param y The second element.
         * @return True if x and y are in the same set, false otherwise.
         */

        public boolean connected(int x, int y) { // Are x and y connected?
        return find(x) == find(y);
    }

    /**
     * Counts the number of disjoint sets.
     *
     * @return The number of disjoint sets.
     */

    public int countSets() { // Count the number of disjoint sets.
        int count = 0;
        for (int i = 0; i < parent.length; i++) {
            if (parent[i] == i) {
                count++;
            }
        }
        return count;
    }
}

