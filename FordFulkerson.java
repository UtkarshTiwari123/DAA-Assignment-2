//create a 2d array of pairs graph
import java.util.*;

public class FordFulkerson {
    public static int maxFlow(int[][][] graph, int source, int sink) {
        // Create a copy of the graph to use as the residual graph
        int[][] residualGraph = new int[graph.length][graph[0].length];
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[0].length; j++) {
                residualGraph[i][j] = graph[i][j][1];
            }
        }

        int[] parent = new int[graph.length]; // Stores the parent of each node in the augmenting path
        int maxFlow = 0;

        // Repeat until there are no more augmenting paths
        while (bfs(residualGraph, source, sink, parent)) {
            // Find the bottleneck capacity of the augmenting path
            int bottleneck = Integer.MAX_VALUE;
            for (int i = sink; i != source; i = parent[i]) {
                int j = parent[i];
                bottleneck = Math.min(bottleneck, residualGraph[j][i]);
            }

            // Update the flow and residual graph along the augmenting path
            for (int i = sink; i != source; i = parent[i]) {
                int j = parent[i];
                graph[j][i][0] += bottleneck; // Increase flow in the original graph
                residualGraph[j][i] -= bottleneck; // Decrease capacity in the residual graph
                residualGraph[i][j] += bottleneck; // Increase capacity in the residual graph
            }

            // Add the bottleneck capacity to the maximum flow
            maxFlow += bottleneck;
        }

        return maxFlow;
    }

    // Implements a breadth-first search to find an augmenting path
    private static boolean bfs(int[][] residualGraph, int source, int sink, int[] parent) {
        boolean[] visited = new boolean[residualGraph.length];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v = 0; v < residualGraph.length; v++) {
                if (!visited[v] && residualGraph[u][v] > 0) {
                    queue.offer(v);
                    visited[v] = true;
                    parent[v] = u;
                    if (v == sink) {
                        return true; // Found an augmenting path
                    }
                }
            }
        }

        return false; // No augmenting path found
    }

    public static void main(String[] args){
        //{flow, capacity}
        int graph[][][] = new int[][][]{{ {0, 0}, {0, 16}, {0, 13}, {0, 0}, {0, 0}, {0, 0} }, { {0,0}, {0,0}, {0,10}, {0,12}, {0,0}, {0,0} },
            { {0,0}, {0,4}, {0,0}, {0,0}, {0,14}, {0,0} },  { {0,0}, {0,0}, {0,9}, {0,0}, {0,0}, {0,20} },
            { {0,0}, {0,0}, {0,0}, {0,7}, {0,0}, {0,4} },   { {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }
        };

        //FordFulkerson obj = new FordFulkerson();
        int source = 0, sink = 5;
        int maxflow = maxFlow(graph, source, sink);
        System.out.println(maxflow);
        for(int i = 0; i < 6; i++){
            System.out.println(graph[0][i][0]);
        }
    }

}
