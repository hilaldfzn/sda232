import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lab8 {
    private static InputReader in;
    private static PrintWriter out;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int N = in.nextInt();
        int E = in.nextInt();
        Graph graph = new Graph(N);

        for (int i = 0; i < E; i++) {
            int A = in.nextInt();
            int B = in.nextInt();
            long W = in.nextLong();
            graph.addEdge(A, B, W);
        }

        graph.computeAllPairsShortestPaths();
        
        int H = in.nextInt(); 
        ArrayList<Integer> treasureNodes = new ArrayList<Integer>();
        for (int i = 0; i < H; i++) {
            int K = in.nextInt();
            treasureNodes.add(K);
        }

        int Q = in.nextInt();
        int O = in.nextInt();
        
        while (Q-- > 0) {
            int T = in.nextInt();
            int currentPosition = 1;
            boolean isRouteSafe = isRouteValid(graph, currentPosition, T, O);

            out.println(isRouteSafe ? 1 : 0);
        }

        out.close();
    }

    private static boolean isRouteValid(Graph graph, int currentPosition, int T, int O) {
        long totalOxygenNeeded = 0;

        while (T-- > 0) {
            int D = in.nextInt();

            totalOxygenNeeded += graph.getDijkstra(currentPosition, D);
            if (totalOxygenNeeded > O - 1) {
                return false;
            }

            currentPosition = D;
        }

        totalOxygenNeeded += graph.getDijkstra(currentPosition, 1); // Kembali ke posisi awal
        return totalOxygenNeeded <= O - 1;
    }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the
    // usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit
    // Exceeded caused by slow input-output (IO)
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }
    }
}

class Graph {
    public int V;
    public Map<Integer, List<Edge>> adj;
    public Map<Integer, Map<Integer, Long>> shortest;

    public Graph(int V) {
        this.V = V;
        adj = new HashMap<>();
        shortest = new HashMap<>();
        for (int i = 1; i <= this.V; i++) {
            addNode(i);
        }
    }

    public void addNode(int node) {
        adj.put(node, new ArrayList<>());
    }

    public void addEdge(int from, int to, long weight) {
        adj.get(from).add(new Edge(to, weight));
        adj.get(to).add(new Edge(from, weight));
    }

    public void computeAllPairsShortestPaths() {
        for (int i = 1; i <= V; i++) {
            dijkstra(i);
        }
    }

    public void dijkstra(int source) {
        if (shortest.containsKey(source)) {
            return;
        }

        Map<Integer, Long> distance = new HashMap<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>((o1, o2) -> Long.compare(o1.weight, o2.weight));

        pq.add(new Edge(source, 0));
        distance.put(source, 0L);

        while (!pq.isEmpty()) {
            Edge edge = pq.poll();
            int node = edge.to;
            long weight = edge.weight;

            if (distance.getOrDefault(node, Long.MAX_VALUE) < weight) {
                continue;
            }

            if (adj.containsKey(node)) {
                for (Edge next : adj.get(node)) {
                    long nextWeight = next.weight;
                    int nextNode = next.to;

                    if (!distance.containsKey(nextNode) || distance.get(nextNode) > weight + nextWeight) {
                        distance.put(nextNode, weight + nextWeight);
                        pq.add(new Edge(nextNode, weight + nextWeight));
                    }
                }
            }
        }

        shortest.put(source, distance);
    }

    public long getDijkstra(int source, int target) {
        return shortest.getOrDefault(source, Collections.emptyMap()).getOrDefault(target, Long.MAX_VALUE);
    }
}

class Edge {
    public int to;
    public long weight;

    public Edge(int to, long weight) {
        this.to = to;
        this.weight = weight;
    }
}