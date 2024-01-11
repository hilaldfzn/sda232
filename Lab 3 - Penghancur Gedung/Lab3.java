import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.ArrayDeque;
import java.util.Deque;

public class Lab3 {
    private static InputReader in;
    private static PrintWriter out;
    static Long T;
    static String arahGerak = "KANAN";
    static Deque<Deque<Integer>> dequeGedung = new ArrayDeque<Deque<Integer>>();

    static String GA() {
        arahGerak = arahGerak.equals("KANAN") ? "KIRI" : "KANAN";
        return arahGerak;
    }

    static long S(long Si) {
        long point = 0;
    
        if (!dequeGedung.isEmpty()) {
            if (!arahGerak.equals("KANAN")) {
                point = prosesLantai(dequeGedung.pollFirst(), Si);
                if (!dequeGedung.isEmpty()) {
                    dequeGedung.addFirst(dequeGedung.pollLast());
                }
            } else {
                point = prosesLantai(dequeGedung.pollFirst(), Si);
            }
        }
    
        if (T <= 0 || dequeGedung.isEmpty()) {
            return -1;
        }
    
        return point;
    }
    
    private static long prosesLantai(Deque<Integer> lantai, long Si) {
        long point = 0;
        
        while (lantai != null && !lantai.isEmpty() && Si > 0) {
            int pointLantai = lantai.pop();
            point += pointLantai;
            T -= pointLantai;
            Si--;
        }
    
        if (lantai != null && !lantai.isEmpty()) {
            if (arahGerak.equals("KANAN")) {
                dequeGedung.addLast(lantai);
            } else {
                dequeGedung.addFirst(lantai);
            }
        }
        
        return point;
    }    

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);
        
        T = in.nextLong();
        int X = in.nextInt();
        int C = in.nextInt();
        int Q = in.nextInt();

        for (int i = 0; i < X; i++) {
            Deque<Integer> dequeLantai = new ArrayDeque<Integer>(C);

            for (int j = 0; j < C; j++) {
                int Ci = in.nextInt();
                dequeLantai.addLast(Ci);
            }
            dequeGedung.add(dequeLantai);
        }

        for (int i = 0; i < Q; i++) {
            String perintah = in.next();
            if (perintah.equals("GA")) {
                out.println(GA());
            } else if (perintah.equals("S")) {
                long Si = in.nextLong();
                long pointsEarned = S(Si);

                if (pointsEarned <= 0) {
                    out.println("MENANG");
                } else {
                    out.println(pointsEarned);
                }
            }
        }

        out.close();
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

        public long nextLong(){
            return Long.parseLong(next());
        }

    }
}