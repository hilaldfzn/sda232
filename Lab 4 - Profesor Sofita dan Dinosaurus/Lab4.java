import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class Lab4 {
    private static InputReader in;
    private static PrintWriter out;
    static int[][] memoDP;

    static int deteksiDNA(int M, int N, String X, String Y) {
        if (memoDP[M][N] != -1) {
            return memoDP[M][N];
        }
        
        if (M == 0 || N == 0) {
            return 0;
        }

        if (X.charAt(M - 1) == Y.charAt(N - 1)) {
            memoDP[M][N] = 1 + deteksiDNA(M - 1, N - 1, X, Y);
            return memoDP[M][N];
        }

        memoDP[M][N] = Math.max(deteksiDNA(M - 1, N, X, Y), deteksiDNA(M, N - 1, X, Y));
        
        return memoDP[M][N];
    }

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int T = in.nextInt();

        while (T-- > 0) {
            int M = in.nextInt();
            int N = in.nextInt();
            String S1 = in.next();
            String S2 = in.next();
            
            memoDP = new int[M + 1][N + 1];
            
            for (int i = 0; i <= M; i++) {
                for (int j = 0; j <= N; j++) {
                    memoDP[i][j] = -1;
                }
            }
            
            int ans = deteksiDNA(M, N, S1, S2);
            out.println(ans);
        }

        out.close();
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
    }
}