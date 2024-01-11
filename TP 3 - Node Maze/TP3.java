/* Referensi:
 * https://github.com/edutjie/sda222/blob/main/TP03/TP03.java
 * https://medium.com/@ankur.singh4012/implementing-min-heap-in-java-413d1c20f90d (MinHeap)
 * https://medium.com/@ankur.singh4012/implementing-max-heap-in-java-ea368dadd273 (MaxHeap)
 * https://www.geeksforgeeks.org/dijkstras-algorithm-for-adjacency-list-representation-greedy-algo-8/ (Dijkstra - modification)
 * https://stackoverflow.com/questions/41965431/dijkstra-algorithm-min-heap-as-a-min-priority-queue (Dijkstra - modification)
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Arrays;

public class TP3 {
    private static InputReader in;
    private static PrintWriter out;
    static char[] rooms;                        // Array untuk menyimpan tipe ruangan 
    static long[][] memo;                       // Memo untuk menyimpan hasil perhitungan Dijkstra
    static ArrayList<ArrayList<Edge>> graph;    // Representasi graf dalam bentuk adjacency list

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Format input: [V E]
        int V = in.nextInt();       // Banyak ruangan
        int E = in.nextInt();       // Banyak koridor

        rooms = new char[V + 1];
        graph = new ArrayList<>();

        // Input tipe ruangan sebagai V (ruangan)
        // Tipe ruangan (N = ruangan biasa, S = treasure room)
        for (int i = 0; i < V; i++) {
            graph.add(new ArrayList<>());
            rooms[i] = in.next().charAt(0);
        }
        
        // Format input: [A B N]
        // Dari A ke B dengan N sebagai jumlah minimal orang untuk melewati koridor
        for (int i = 0; i < E; i++) {
            int A = in.nextInt();
            int B = in.nextInt();
            long N = in.nextLong();
            graph.get(A - 1).add(new Edge(B - 1, N));
            graph.get(B - 1).add(new Edge(A - 1, N));
        }

        // Preprocess graf untuk menyimpan hasil Dijkstra untuk anggota minimum ke Treasure Room
        preprocessGraph(V);

        int Q = in.nextInt();     // Banyak query dijalankan
        while (Q-- > 0) {
            String query = in.next();

            // Query M
            // Format input: M [Group size]
            if (query.equals("M")) {
                long groupSize = in.nextLong();
                out.println(handleM(0, groupSize));
            }
            
            // Query S
            // Format input: S [Start ID]
            else if (query.equals("S")) {
                int startId = in.nextInt();
                out.println(handleS(startId - 1));
            }
            
            // Query T
            // Format input: T [Start ID] [Middle ID] [End ID] [Group size]
            else if (query.equals("T")) {
                int start = in.nextInt();
                int middle = in.nextInt();
                int end = in.nextInt();
                long size = in.nextLong();
                out.println(handleT(start - 1, middle - 1, end - 1, size));
            }
        }

        out.close();
    }

    /**
     * Query M: Menghitung jumlah 'Treasure Rooms' yang bisa diakses.
     * @param start Posisi awal (Selalu dari ruangan ID 1 dengan index 0).
     * @param size Jumlah anggota grup.
     * @return Jumlah 'Treasure Rooms' yang dapat diakses.
     */
    private static int handleM(int start, long size) {
        int maxTreasure = 0;

        for (int i = 0; i < graph.size(); i++) {
            if (rooms[i] == 'S') {
                if (memo[i][0] <= size) {
                    maxTreasure += 1;
                }
            }
        }

        return maxTreasure;
    }

    /**
     * Query S: Mencari anggota grup terkecil untuk mencapai 'Treasure Room' apapun.
     * @param startId ID start ruangan.
     * @return Anggota grup terkecil yang diperlukan untuk mencapai 'Treasure Room' terdekat.
     */
    private static long handleS(int startId) {
        long reqMinGroup = Long.MAX_VALUE;
        
        if (rooms[startId] != 'S') {
            for (int idx = 0; idx < graph.size(); idx++) {
                if (rooms[idx] == 'S') {
                    reqMinGroup = Math.min(reqMinGroup, memo[idx][startId]);
                }

            }
        } else {
            reqMinGroup = 0;
        }

        return reqMinGroup;
    }    
    
    /**
     * Query T: Memeriksa kemungkinan perjalanan dari start ke end melalui middle.
     * Terdapat 3 kemungkinan:
     * 'N' jika tidak bisa sampai ke middle.
     * 'H' jika bisa sampai ke middle tapi tidak bisa menempuh ke end.
     * 'Y' jika bisa mencapai end melalu middle.
     * @param start ID start ruangan.
     * @param middle ID middle ruangan.
     * @param end ID end ruangan.
     * @param size Jumlah anggota grup.
     * @return Karakter yang menggambarkan hasil perjalanan ('Y', 'H', 'N').
     */
    private static char handleT(int start, int middle, int end, long size) {
        if (memo[start] == null) {
            dijkstra(start);
        }

        if (memo[middle] == null) {
            dijkstra(middle);
        }
    
        boolean startToMid = memo[start][middle] <= size;
        boolean midToEnd = memo[middle][end] <= size;
    
        if (startToMid && midToEnd) {
            return 'Y';
        } else if (startToMid) {
            return 'H';
        } else {
            return 'N';
        }
    }

    /**
     * Preprocessing graf menggunakan Dijkstra untuk setiap 'Treasure Room'.
     * @param V Jumlah ruangan.
     */
    private static void preprocessGraph(int V) {
        memo = new long[V][];

        for (int i = 0; i < V; i++) {
            if (rooms[i] == 'S') {
                memo[i] = dijkstra(i);
            }
        }
    }

    /**
     * Implementasi Algoritma Dijkstra untuk menghitung jarak terpendek.
     * @param start ID start ruangan.
     * @return Array yang berisi jarak terpendek dari node start ke node lain.
     */
    private static long[] dijkstra(int start) {
        long[] dist = new long[graph.size()];
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[start] = 0;

        Heap heap = new Heap(graph.size(), true);
        heap.add(new RoomNode(start, 0));

        while (!heap.isEmpty()) {
            RoomNode node = heap.poll();
            int u = node.id;
            if (dist[u] < node.dist) continue;

            for (Edge edge : graph.get(u)) {
                int v = edge.dest;
                long weight = edge.minGroupSize;
                long newDist = Math.max(dist[u], weight);
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    heap.add(new RoomNode(v, dist[v]));
                }
            }
        }

        memo[start] = dist;
        return dist;
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

// Class Heap
class Heap {
    public int capacity;
    public int size;
    public RoomNode[] data;
    public boolean isMinHeap = false;

    public Heap(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.data = new RoomNode[capacity];
    }

    public Heap(int capacity, boolean isMinHeap) {
        this.data = new RoomNode[capacity];
        this.size = 0;
        this.isMinHeap = isMinHeap;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int getLeftChildIndex(int parentIndex) {
        return 2 * parentIndex + 1;
    }

    public int getRightChildIndex(int parentIndex) {
        return 2 * parentIndex + 2;
    }

    public int getParentIndex(int childIndex) {
        return (childIndex - 1) / 2;
    }

    public boolean hasLeftChild(int index) {
        return getLeftChildIndex(index) < size;
    }

    public boolean hasRightChild(int index) {
        return getRightChildIndex(index) < size;
    }

    public boolean hasParent(int index) {
        return getParentIndex(index) >= 0;
    }

    public RoomNode leftChild(int index) {
        return data[getLeftChildIndex(index)];
    }

    public RoomNode rightChild(int index) {
        return data[getRightChildIndex(index)];
    }

    public RoomNode parent(int index) {
        return data[getParentIndex(index)];
    }

    public void swap(int a, int b) {
        RoomNode temp = data[a];
        data[a] = data[b];
        data[b] = temp;
    }

    private void ensureExtraCapacity() {
        if (size == data.length) {
            data = Arrays.copyOf(data, data.length * 2);
        }
    }

    // Time Complexity : O(1)
    public RoomNode peek() {
        if (size == 0) {
            return null;
        }
        return data[0];
    }

    // Time Complexity : O(log n)
    public RoomNode poll() {
        if (size == 0) {
            return null;
        }
        RoomNode item = data[0];
        data[0] = data[size - 1];
        size--;

        heapifyDown();
        return item;
    }

    // Time Complexity : O(log n)
    public void add(RoomNode item) {
        ensureExtraCapacity();
        data[size] = item;
        size++;
        heapifyUp();
    }
    
    // Optimasi heapifyUp dan heapifyDown
    private void heapifyUp() {
        int index = size - 1;
        while (hasParent(index) && (isMinHeap ? parent(index).dist > data[index].dist : parent(index).dist < data[index].dist)) {
            swap(getParentIndex(index), index);
            index = getParentIndex(index);
        }
    }

    private void heapifyDown() {
        int index = 0;
        while (hasLeftChild(index)) {
            int smallerChildIndex = getLeftChildIndex(index);
            if (hasRightChild(index) && (isMinHeap ? rightChild(index).dist < leftChild(index).dist : rightChild(index).dist > leftChild(index).dist)) {
                smallerChildIndex = getRightChildIndex(index);
            }

            if (isMinHeap ? data[index].dist < data[smallerChildIndex].dist : data[index].dist > data[smallerChildIndex].dist) {
                break;
            } else {
                swap(index, smallerChildIndex);
            }
            index = smallerChildIndex;
        }
    }
}

// Class RoomNode (Ruangan)
class RoomNode implements Comparable<RoomNode> {
    int id;                 // ID ruangan
    long dist;              // Jarak ke ruangan lain

    RoomNode(int id, long dist) {
        this.id = id;
        this.dist = dist;
    }

    @Override
    public int compareTo(RoomNode other) {
        return Long.compare(this.dist, other.dist);
    }
}

// Class Edge (Koridor)
class Edge {
    int dest;               // Sebagai ID ruangan tujuan (destination)
    long minGroupSize;      // Jumlah anggota grup

    Edge(int dest, long minGroupSize) {
        this.dest = dest;
        this.minGroupSize = minGroupSize;
    }
}