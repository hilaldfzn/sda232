import java.io.*;
import java.util.*;

public class TP1 {
    private static InputReader in;
    private static PrintWriter out;
    private static HashMap<Integer, Ride> rideIdMap = new HashMap<>();          // HashMap untuk menyimpan wahana berdasarkan ID
    private static HashMap<Integer, Visitor> visitorIdMap = new HashMap<>();    // HashMap untuk menyimpan pengunjung berdasarkan ID
    private static ArrayDeque<Visitor> exitList = new ArrayDeque<>();           // Daftar keluar pengunjung ketika uang habis atau tidak cukup untuk antre wahana
    private static int[][][] dp, pick;  // Tabel DP dan pilihan untuk mengoptimalkan rencana pengunjung

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int M = in.nextInt(); // Jumlah Wahana
        List<Ride> rides = new ArrayList<>();

        // Membaca input untuk atribut setiap wahana
        // Menambahkan wahana ke list dan map berdasarkan ID
        // Format: [Harga] [Poin] [Kapasitas] [Persentase FT]
        for (int i = 0; i < M; i++) {
            int price = in.nextInt();
            int points = in.nextInt();
            int capacity = in.nextInt();
            int fastTrackPercent = in.nextInt();
            int maxFT = (int) Math.ceil(capacity * fastTrackPercent / 100.0);       // Kapasitas maksimum untuk FT dalam antrean wahana (pembulatan ke atas)

            rides.add(new Ride(i + 1, price, points, capacity, maxFT));
            rideIdMap.put(i + 1, new Ride(i + 1, price, points, capacity, maxFT));
        }

        int N = in.nextInt();  // Jumlah pengunjung
        List<Visitor> visitors = new ArrayList<>();
        int maxMoney = -1;

        // Membaca input untuk atribut setiap pengunjung
        // Menambahkan pengunjung ke dalam list dan map berdasarkan ID
        // Format: [Tipe] [Uang]
        for (int i = 0; i < N; i++) {
            String type = in.next();
            int money = in.nextInt();

            if (money > maxMoney) maxMoney = money;     // Menentukan uang terbanyak yang dimiliki pengunjung
            visitors.add(new Visitor(i + 1, type, money, 0));
            visitorIdMap.put(i + 1, new Visitor(i + 1, type, money, 0));
        }

        int T = in.nextInt();  // Jumlah aktivitas

        while (T-- > 0) {
            String query = in.next();

            // Query A: Menambahkan pengunjung ke antrean wahana
            // Format: A [ID_PENGUNJUNG] [ID_WAHANA]
            if (query.equals("A")) {
                int visitorId = in.nextInt();
                int rideId = in.nextInt();

                Visitor visitor = visitorIdMap.get(visitorId);
                Ride ride = rideIdMap.get(rideId);

                // Jika pengunjung tidak memiliki uang yang cukup untuk bermain di wahana, cetak -1
                if (visitor.money < ride.price) {
                    out.println(-1);
                    continue;
                }

                // Tambahkan pengunjung ke antrean yang sesuai berdasarkan tipenya
                if (visitor.type.equals("FT")) {
                    ride.fastTrackQueue.add(new Visitor(visitor.id, visitor.type, visitor.money, visitor.totalRides));
                } else if (visitor.type.equals("R")) {
                    ride.regularQueue.add(new Visitor(visitor.id, visitor.type, visitor.money, visitor.totalRides));
                }

                // Mencetak banyak pengunjung yang berada di dalam antrean wahana
                out.println(ride.fastTrackQueue.size() + ride.regularQueue.size());
            } 

            // Query E: Memproses antrean wahana dan menampilkan ID pengunjung yang dapat memainkan wahana
            // Format: E [ID_WAHANA]
            else if (query.equals("E")) {
                int rideId = in.nextInt();
                Ride ride = rideIdMap.get(rideId);
                
                int counterFT = 0;                      // Counter untuk jumlah pengunjung FT yang masuk
                int counterR = 0;                       // Counter untuk jumlah pengunjung regular yang masuk

                StringBuilder sb = new StringBuilder();

                /* Urutan bermain dilihat dari jenis pengunjung, dimulai dari pengunjung FT
                 * dengan kapasitas prioritas maksimal sesuai wahana, pengunjung regular,
                 * kemudian pengunjung FT lagi apabila masih ada.
                 * 
                 * Penentuan urutan bermain pengunjung berdasarkan dari jenis pengunjung FT, 
                 * total bermain yang lebih sedikit, lalu id yang lebih kecil.
                 */
                while (ride.capacity > counterFT + counterR && (!ride.fastTrackQueue.isEmpty() || !ride.regularQueue.isEmpty())) {
                    if (ride.maxFastTrack > counterFT && !ride.fastTrackQueue.isEmpty()) {
                        Visitor v = visitorIdMap.get(ride.fastTrackQueue.poll().id);
                        
                        if (updateVisitor(v, ride)) {
                            counterFT++;
                            sb.append(v.id + " ");
                        }
                    } else if (!ride.regularQueue.isEmpty()) {
                        Visitor v = visitorIdMap.get(ride.regularQueue.poll().id);

                        if (updateVisitor(v, ride)) {
                            counterR++;
                            sb.append(v.id + " ");
                        }
                    } else {
                        Visitor v = visitorIdMap.get(ride.fastTrackQueue.poll().id);

                        if (updateVisitor(v, ride)) {
                            counterFT++;
                            sb.append(v.id + " ");
                        }
                    }
                }
         
                if (sb.length() == 0) {
                    out.println(-1);               // Jika tidak ada pengunjung pada wahana tersebut
                } else {
                    out.println(sb.toString());    // Mencetak semua ID pengunjung yang bermain di wahana tersebut sesuai urutan bermain
                }
            } 

            // Query S: Mengecek posisi pengunjung dalam antrean suatu wahana
            // Format: S [ID_PENGUNJUNG] [ID_WAHANA]
            else if (query.equals("S")) {
                int visitorId = in.nextInt();
                int rideId = in.nextInt();
                Ride ride = rideIdMap.get(rideId);

                // Clone antrean FT dan Regular untuk mengecek posisi tanpa mengubah antrean aslinya
                Queue<Visitor> ftQueueClone = new PriorityQueue<>(ride.fastTrackQueue);
                Queue<Visitor> regularQueueClone = new PriorityQueue<>(ride.regularQueue);

                int pos = -1;       // Menyimpan posisi pengunjung dalam antrean (-1 jika tidak ditemukan)
                int index = 0;      // Menghitung total pengunjung yang sudah diperiksa IDnya

                // Cek apakah pengunjung memiliki cukup uang untuk masuk wahana
                if (visitorIdMap.get(visitorId).money < ride.price) {
                    out.println(-1);
                    continue;
                }
                
                while (!ftQueueClone.isEmpty() || !regularQueueClone.isEmpty()) {
                    if (!ftQueueClone.isEmpty() && (index % ride.capacity) < ride.maxFastTrack) {
                        Visitor v = visitorIdMap.get(ftQueueClone.poll().id);

                        if (v.money >= ride.price) {
                            index++;
                        }

                        if (v.id == visitorId) {
                            pos = index;
                            break;
                        }
                    } else if (!regularQueueClone.isEmpty()) {
                        Visitor v = visitorIdMap.get(regularQueueClone.poll().id);

                        if (v.money >= ride.price) {
                            index++;
                        }

                        if (v.id == visitorId) {
                            pos = index;
                            break;
                        }
                    } else {
                        Visitor v = visitorIdMap.get(ftQueueClone.poll().id);

                        if (v.money >= ride.price) {
                            index++;
                        }
                        
                        if (v.id == visitorId) {
                            pos = index;
                            break;
                        }
                    }
                }

                out.println(pos);   // Cetak posisi pengunjung dalam antrean
            } 

            // Query F: Mencetak banyak poin dari pengunjung pertama atau terakhir yang terpilih dari daftar keluar
            // Format: F [P]
            else if (query.equals("F")) {
                int P = in.nextInt();
                int point = -1;

                // Jika tidak ada pengunjung dalam daftar keluar atau belum ada pengunjung yang telah menghabiskan uangnya
                if (exitList.isEmpty()) {
                    out.println(-1);
                    continue;
                }

                // Jika P = 0, pilih pengunjung pertama pada daftar keluar
                // Jika P = 1, pilih pengunjung terakhir pada daftar keluar
                if (P == 0) { point = visitorIdMap.get(exitList.pollFirst().id).points; }
                else { point = visitorIdMap.get(exitList.pollLast().id).points; }

                out.println(point);     // Mencetak poin pengunjung yang terpilih dari daftar keluar
            } 

            // Query O: Mengoptimalkan pengeluaran uang pengunjung untuk mendapatkan poin maksimal
            // Format: O [ID_PENGUNJUNG]
            //
            // Kondisi Tiebreaker:
            // Jika terdapat lebih dari satu rencana yang mungkin untuk mencapai poin maksimal, buatlah rencana 
            // yang membutuhkan uang paling sedikit (total harga wahana yang dikunjungi paling murah). 
            // Jika masih terdapat lebih dari satu rencana, prioritaskan wahana dengan indeks terkecil sehingga 
            // indeks-indeks dari wahana yang dipilih memiliki urutan leksikografis paling rendah.
            else if (query.equals("O")) {
                int visitorId = in.nextInt();
                Visitor visitor = visitorIdMap.get(visitorId);
 
                // Jika belum ada, maka inisialisasi DP dan pick
                if (dp == null) {
                    dp = new int[maxMoney + 2][3][rides.size() + 2];
                    pick = new int[maxMoney + 2][3][rides.size() + 2];

                    // Iterasi untuk mengisi tabel DP
                    for (int k = rides.size(); k >= 0 ; k--) {
                        for (int i = maxMoney; i >= 0; i--) {
                            for (int j = 0; j <= 2; ++j) {
                                // Base case: jika di wahana terakhir, atur nilai dp menjadi 0
                                if (k == rides.size()) {
                                    dp[i][j][k] = 0;
                                    continue;
                                }

                                // Menyalin nilai dari sub-problem yang lebih besar
                                dp[i][j][k] = dp[i][j][k + 1];

                                // Menentukan apakah akan memilih wahana saat ini berdasarkan harga dan poinnya
                                // Cek kondisi spesifik untuk memutuskan apakah wahana k bisa diambil atau tidak
                                if (k % 2 != j && i >= rides.get(k).price) {
                                    // Perbarui dp jika memilih wahana saat ini menghasilkan rencana yang lebih baik
                                    if (dp[i][j][k + 1] <= dp[i - rides.get(k).price][k % 2][k + 1] + rides.get(k).points) {
                                        // Update tabel DP jika mengambil wahana memberikan poin lebih banyak
                                        dp[i][j][k] = dp[i - rides.get(k).price][k % 2][k + 1] + rides.get(k).points;
                                        pick[i][j][k] = 1;       // Tandai wahana ini sebagai yang dipilih
                                    } else {
                                        pick[i][j][k] = 0;       // Tandai wahana ini tidak dipilih
                                    }
                                } else {
                                    pick[i][j][k] = 0;           // Tandai wahana ini tidak dipilih
                                }          
                            }
                        }
                    }
                }

                handleO(visitor, rides);
            }
        }

        // don't forget to close/flush the output
        out.close();
    }

    /**
     * Menghandle query O yang bertujuan untuk mengoptimalkan pengeluaran uang
     * pengunjung untuk mendapatkan jumlah poin maksimal yang mungkin.
     *
     * @param visitor Pengunjung yang akan dioptimalkan pengeluaran uangnya.
     * @param rides   Daftar wahana yang tersedia.
     */
    public static void handleO(Visitor visitor, List<Ride> rides) {
        int maxRideId = rides.size();           // Jumlah maksimal wahana yang tersedia
        int money = visitor.money;              // Jumlah uang yang dimiliki pengunjung
        int maxPoints =  dp[money][2][0];       // Mengambil poin maksimal yang dapat diperoleh dengan uang yang ada
        int parity = 2;                         // Paritas ID (ganjil atau genap) digunakan untuk memilih wahana secara bergantian

        // Jika pengunjung tidak dapat membeli tiket untuk wahana apa pun, poin maksimal yang dapat diperoleh 0.
        if (maxPoints == 0) {
            out.println(maxPoints);
            return;
        }

        // Cetak jumlah poin maksimal yang dapat diperoleh
        out.print(maxPoints + " ");

        // Mengurangi uang hingga titik di mana poin maksimal berubah
        while (money >= 1 && dp[money][2][0] == dp[money - 1][2][0]) money--;

        // Iterasi melalui semua wahana
        for (int i = 0; i < maxRideId; i++) {
            if (money <= 0) break;                  // Keluar dari loop jika uang pengunjung habis

            // Jika paritas sesuai dan wahana dipilih
            if (parity != i % 2 && pick[money][parity][i] == 1) {
                out.print((i + 1) + " ");           // Mencetak indeks wahana yang dipilih (ditambah 1 karena indeks mulai dari 0)
                
                parity = i % 2;                     // Mengganti paritas untuk memastikan perubahan ID wahana
                money -= rides.get(i).price;        // Kurangi uang pengunjung sesuai dengan harga wahana yang dimainkan
            }
        }
        
        out.println();
    }   
    
    public static boolean updateVisitor(Visitor v, Ride r) {
        if (v.money >= r.price) {
            v.money -= r.price;
            v.points += r.points;
            v.totalRides++;

            if (v.money <= 0) {
                exitList.add(new Visitor(v.id, v.type, v.money, v.totalRides));
            }
            return true;
        }
        return false;
    }

    static class Visitor implements Comparable<Visitor> {
        int id;
        String type;
        int money;
        int points;
        int totalRides;
    
        public Visitor(int id, String type, int money, int totalRides) {
            this.id = id;
            this.type = type;
            this.money = money;
            this.points = 0;
            this.totalRides = totalRides;
        }

        @Override
        public int compareTo(Visitor other) {
            if (this.totalRides < other.totalRides) {
                return -1;
            } else if (this.totalRides > other.totalRides) {
                return 1;
            } else {
                if (this.id < other.id) {
                    return -1;
                } else {
                    return 1;
                }
            }          
        }
    }

    static class Ride {
        int id;
        int price;
        int points;
        int capacity;
        int maxFastTrack;
        PriorityQueue<Visitor> fastTrackQueue = new PriorityQueue<>();
        PriorityQueue<Visitor> regularQueue = new PriorityQueue<>();

        public Ride(int id, int price, int points, int capacity, int maxFastTrack) {
            this.id = id;
            this.price = price;
            this.points = points;
            this.capacity = capacity;
            this.maxFastTrack = maxFastTrack;
        }
    }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit Exceeded caused by slow input-output (IO)
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
    }
}    