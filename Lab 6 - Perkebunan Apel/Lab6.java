import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class Lab6 {
    private static InputReader in;
    private static PrintWriter out;
    static AVLTree tree = new AVLTree();

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int N = in.nextInt();
        for (int i = 0; i < N; i++) {
            grow();
        }

        int Q = in.nextInt();
        for (int i = 0; i < Q; i++) {
            char query = in.nextChar();

            if (query == 'G') { grow(); }
            else if (query == 'P') { pick(); }
            else if (query == 'F') { fall(); }
            else { height(); }
        }

        out.close();
    }

    static void grow() {
        int fruit = in.nextInt();
        tree.root = tree.insert(tree.root, fruit);
    }

    static void pick() {
        int fruit = in.nextInt();
        if (tree.find(tree.root, fruit) != null) {
            tree.root = tree.delete(tree.root, fruit);
            out.println(fruit);
        } else {
            out.println(-1);
        }
    }

    static void fall() {
        if (tree.root == null) {
            out.println(-1);
        } else {
            int largestLabel = tree.findLargest(tree.root);
            tree.root = tree.delete(tree.root, largestLabel);
            out.println(largestLabel);
        }
    }

    static void height() {
        out.println(tree.getHeight(tree.root));
    }

    // taken from https://www.programiz.com/dsa/avl-tree
    // a method to print the contents of a Tree data structure in a readable
    // format. it is encouraged to use this method for debugging purposes.
    // to use, simply copy and paste this line of code:
    // printTree(tree.root, "", true);
    static void printTree(Node currPtr, String indent, boolean last) {
        if (currPtr != null) {
            out.print(indent);
            if (last) {
                out.print("R----");
                indent += "   ";
            } else {
                out.print("L----");
                indent += "|  ";
            }
            out.println(currPtr.key);
            printTree(currPtr.left, indent, false);
            printTree(currPtr.right, indent, true);
        }
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

        public char nextChar() {
            return next().charAt(0);
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }
    }
}

class Node {
    int key, height;
    Node left, right;

    Node(int key) {
        this.key = key;
        height = 1;
    }
}

class AVLTree {
    Node root;

    int getHeight(Node node) {
        if (node == null) return 0;
        return node.height;
    }

    Node find(Node root, int key) {
        while (root != null) {
            if (key < root.key) {
                root = root.left;
            } else if (key > root.key) {
                root = root.right;
            } else {
                return root;
            }
        }
        return null;
    }

    int findLargest(Node node) {
        Node current = node;
        while (current.right != null) {
            current = current.right;
        }
        
        return current.key;
    }

    int getBalance(Node node) {
        if (node == null) return 0;
        return getHeight(node.left) - getHeight(node.right);
    }

    Node insert(Node node, int key) {
        if (node == null) return new Node(key);
        if (key < node.key) node.left = insert(node.left, key);
        else if (key > node.key) node.right = insert(node.right, key);
        else return node;

        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        int balance = getBalance(node);

        if (balance > 1 && key < node.left.key) return singleRightRotate(node);
        if (balance < -1 && key > node.right.key) return singleLeftRotate(node);
        if (balance > 1 && key > node.left.key) {
            node.left = singleLeftRotate(node.left);
            return singleRightRotate(node);
        }

        if (balance < -1 && key < node.right.key) {
            node.right = singleRightRotate(node.right);
            return singleLeftRotate(node);
        }

        return node;
    }

    Node delete(Node root, int key) {
        if (root == null) return root;
        if (key < root.key) root.left = delete(root.left, key);
        else if (key > root.key) root.right = delete(root.right, key);
        else {
            if (root.left == null) root = root.right;
            else if (root.right == null) root = root.left;
            else {
                // Pemetikan apel menggunakan kaidah Predecessor In-order
                // Mencari elemen paling maksimum di subtree kiri
                root.key = findMaxNode(root.left).key;
                root.left = delete(root.left, root.key);
            }
        }

        if (root == null) return root;

        root.height = 1 + Math.max(getHeight(root.left), getHeight(root.right));
        int balance = getBalance(root);

        if (balance > 1 && getBalance(root.left) >= 0) return singleRightRotate(root);
        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = singleLeftRotate(root.left);
            return singleRightRotate(root);
        }
        
        if (balance < -1 && getBalance(root.right) <= 0) return singleLeftRotate(root);
        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = singleRightRotate(root.right);
            return singleLeftRotate(root);
        }

        return root;
    }

    Node findMaxNode(Node root) {
        Node current = root;
        while (current.right != null) current = current.right;
        return current;
    }

    Node singleLeftRotate(Node y) {
        Node x = y.right;
        Node T2 = x.left;

        x.left = y;
        y.right = T2;

        y.height = 1 + Math.max(getHeight(y.left), getHeight(y.right));
        x.height = 1 + Math.max(getHeight(x.left), getHeight(x.right));

        return x;
    }

    Node singleRightRotate(Node x) {
        Node y = x.left;
        Node T2 = y.right;

        y.right = x;
        x.left = T2;

        x.height = 1 + Math.max(getHeight(x.left), getHeight(x.right));
        y.height = 1 + Math.max(getHeight(y.left), getHeight(y.right));

        return y;
    }
}