package com.codenjoy.dojo.snake.client;

import java.util.*;

class Graph {

    private Integer V;   // No. of vertices
    private LinkedList<Integer> adj[]; //Adjacency Lists
    boolean visited[]; // visited nodes

    // Constructor
    Graph(Integer v) {
        V = v;
        visited = new boolean[V];
        adj = new LinkedList[v];               // initialize empty array, v = number of vertices
        for (int i = 0; i < v; ++i)
            adj[i] = new LinkedList();         // fill array with 25 empty LinkedLists<Integer>
    }

    // Function to add an edge into the graph
    void addEdge(int v, int w) {
        adj[v].add(w);                         // fill linked lists with connections
    }

    void addBarriers(int nodeNum){
        visited[nodeNum] = true;
    }

    // prints BFS traversal from a given source s
    List<Integer> BFS(Integer start, int dest) {
        // Mark all the vertices as not visited(By default
        // set as false)

        // ---------------- !!!  Add step to mark unpassable vertices as visited !!! ---------------------

        // Create a queue for BFS
        LinkedList<Integer> queue = new LinkedList<Integer>();

        // Map for parent nodes
        Map<Integer, Integer> parentNodes = new HashMap<>();

        // Mark the current node as visited and enqueue it
        visited[start] = true;
        queue.add(start);

        while (queue.size() != 0) {
            // Dequeue a vertex from queue and print it
            start = queue.poll();
            //System.out.print(start + " ");

            // Get all adjacent vertices of the dequeued vertex s
            // If a adjacent has not been visited, then mark it
            // visited and enqueue it
            for (Integer n : adj[start]) {
                if (!visited[n]) {
                    visited[n] = true;
                    parentNodes.put(n, start);
                    queue.add(n);
                }

                if (n == dest) {
                    /*System.out.print(n + " ");*/
                    List<Integer> shortestPath = new ArrayList<>();
                    Integer node = dest;
                    while (node != null) {
                        shortestPath.add(node);
                        node = parentNodes.get(node);
                    }
                    Collections.reverse(shortestPath);
                    shortestPath.remove(0);
                    return shortestPath;
                }
            }
        }
        return null;
    }
}