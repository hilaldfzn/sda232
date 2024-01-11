import java.io.*;
import java.util.StringTokenizer;

public class Lab2 {
    private static InputReader in;
    private static PrintWriter out;

    static long maxOddEvenSubSum(long[] a) {
        long N = a.length;
        long maxSum = 0; 
        long currentSum = Long.MIN_VALUE;
        boolean isEven = (N % 2 == 0);
    
        for (int i = 0; i < N; i++) {
            if ((a[i] % 2 == 0) == isEven) {
                maxSum += a[i];
                currentSum = Math.max(currentSum, maxSum);
                
                if (maxSum < 0) {
                    maxSum = 0;
                }
            } else {
                maxSum = 0;
            }
        }
    
        if (currentSum == Long.MIN_VALUE) {
            return 0;
        }
    
        return currentSum;
    }
    
    public static void main(String[] args) throws IOException {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int N = in.nextInt();
        
        long[] x = new long[N];
        for (int i = 0; i < N; ++i) {
            x[i] = in.nextLong();
        }

        long ans = maxOddEvenSubSum(x);
        out.println(ans);

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

        public long nextLong() {
            return Long.parseLong(next());
        }

    }
}