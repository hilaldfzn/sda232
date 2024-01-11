/* Referensi:
 * https://www.geeksforgeeks.org/introduction-to-doubly-linked-lists-in-java/
 * https://www.geeksforgeeks.org/sorted-merge-of-two-sorted-doubly-circular-linked-lists/
 * https://www.geeksforgeeks.org/merge-sort-for-linked-list/
 * https://www.geeksforgeeks.org/insertion-in-an-avl-tree/
 * https://www.geeksforgeeks.org/deletion-in-an-avl-tree/
 * https://www.geeksforgeeks.org/avl-with-duplicate-keys/
 * Slide Kuliah SDA
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.ArrayList;

public class TP2 {
    private static InputReader in;
    private static PrintWriter out;
    private static int currentIdx = 0;
    private static int totalStudents = 0;
    private static int[] studentCount;
    private static Student[] arrStudents;
    private static CircularDoublyLL classList;
    private static final int INIT_CLASS_ID = Integer.MIN_VALUE;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int M = in.nextInt();
        studentCount = new int[M];

        for (int i = 0; i < M; i++) {
            int Mi = in.nextInt();
            studentCount[i] = Mi;
            totalStudents += Mi;
        }

        arrStudents = new Student[totalStudents];
        classList = new CircularDoublyLL();

        for (int i = 0; i < M; i++) {
            StudentTree siswaTree = new StudentTree(INIT_CLASS_ID);

            for (int j = 0; j < studentCount[i]; j++) {
                int Pj = in.nextInt();
                Student student = new Student(1 + currentIdx, Pj);

                arrStudents[currentIdx++] = student;
                siswaTree.insert(siswaTree.root, student);
            }

            classList.add(siswaTree);
        }

        int Q = in.nextInt();
        while (Q-- > 0) {
            char query = in.nextChar();

            switch (query) {
                case 'T':
                    T();
                    break;
                case 'C':
                    C();
                    break;
                case 'G':
                    G();
                    break;
                case 'S':
                    S();
                    break;
                case 'K':
                    K();
                    break;
                case 'A':
                    A();
                    break;
            }
        }

        out.close();
    }

    private static void T() {
        int taskPoints = in.nextInt();
        int studentId = in.nextInt();

        if (currentIdx < studentId) {
            out.println(-1);
            return;
        }

        Student student = arrStudents[studentId - 1];
        StudentTree siswaTree = classList.pakcilNow.treeSiswa;

        if (siswaTree.search(siswaTree.root, student) == null) {
            out.println(-1);
        } else {
            if (taskPoints == 0) {
                out.println(student.points);
                return;
            }

            int bonusTutor = siswaTree.countTutee(siswaTree.root, student.points) - 1;
            int finalBonus = Math.min(taskPoints, bonusTutor);

            siswaTree.delete(siswaTree.root, student);
            student.points += taskPoints + finalBonus;
            siswaTree.insert(siswaTree.root, student);
            
            out.println(student.points);
        }
    }

    private static void C() {
        int studentId = in.nextInt();

        if (currentIdx < studentId) {
            out.println(-1);
            return;
        }

        Student student = arrStudents[studentId - 1];
        ClassNode pakcilPos = classList.pakcilNow;
        StudentTree siswaTree = pakcilPos.treeSiswa;

        if (siswaTree.search(siswaTree.root, student) == null) {
            out.println(-1);
        } else {
            student.cheat++;
            helperC(siswaTree, student, student.cheat, studentId);

            if (siswaTree.root.size == 5) {
                ClassNode moveNode = pakcilPos.next;

                if (pakcilPos == classList.tail) {
                    classList.tail = moveNode = pakcilPos.prev;
                } else {
                    classList.head = pakcilPos.next;
                }

                for (int i = 0; i < 5; i++) {
                    Student moveStudent = siswaTree.root.student;
                    siswaTree.delete(siswaTree.root, moveStudent);
                    moveNode.treeSiswa.insert(moveNode.treeSiswa.root, moveStudent);
                }

                classList.pakcilNow.next.prev = classList.pakcilNow.prev;
                classList.pakcilNow.prev.next = classList.pakcilNow.next;
                classList.pakcilNow = moveNode;
                classList.size--;
            }
        }
    }

    private static void G() {
        char arah = in.nextChar();
        StudentTree moveClass = classList.move(arah).treeSiswa;

        out.println(moveClass.classId);
    }

    private static void S() {
        if (classList.size == 1) {
            out.println("-1 -1");
            return;
        }
    
        ClassNode pakcilNode = classList.pakcilNow;
        StudentTree M = pakcilNode.treeSiswa;
        StudentTree MA = pakcilNode.prev.treeSiswa;
        StudentTree MB = pakcilNode.next.treeSiswa;

        ArrayList<Student> bestM = new ArrayList<>();
        ArrayList<Student> worstM = new ArrayList<>();
        ArrayList<Student> bestMB = new ArrayList<>();
        ArrayList<Student> worstMA = new ArrayList<>();
    
        if (M == classList.tail.treeSiswa) {
            for (int i = 0; i < 3; i++) {
                bestM.add(i, M.getHighest(M.root).student);
                M.delete(M.root, bestM.get(i));

                worstMA.add(i, MA.getLowest(MA.root).student);
                MA.delete(MA.root, worstMA.get(i));
            }
    
            for (int i = 0; i < 3; i++) {
                MA.insert(MA.root, bestM.get(i));
                M.insert(M.root, worstMA.get(i));
            }
        } else if (M == classList.head.treeSiswa) {
            for (int i = 0; i < 3; i++) {
                bestMB.add(i, MB.getHighest(MB.root).student);
                MB.delete(MB.root, bestMB.get(i));

                worstM.add(i, M.getLowest(M.root).student);
                M.delete(M.root, worstM.get(i));
            }
    
            for (int i = 0; i < 3; i++) {
                M.insert(M.root, bestMB.get(i));
                MB.insert(MB.root, worstM.get(i));
            }
        } else {
            for (int i = 0; i < 3; i++) {
                bestM.add(i, M.getHighest(M.root).student);
                M.delete(M.root, bestM.get(i));
                
                bestMB.add(i,  MB.getHighest(MB.root).student);
                MB.delete(MB.root, bestMB.get(i));
                
                worstMA.add(i, MA.getLowest(MA.root).student);
                MA.delete(MA.root, worstMA.get(i));

                worstM.add(i, M.getLowest(M.root).student);
                M.delete(M.root, worstM.get(i));
            }
    
            for (int i = 0; i < 3; i++) {
                M.insert(M.root, bestMB.get(i));
                MA.insert(MA.root, bestM.get(i));
                MB.insert(MB.root, worstM.get(i));
                M.insert(M.root, worstMA.get(i));
            }
        }
    
        int bestStudentM = M.getHighest(M.root).student.studentId;
        int worstStudentM = M.getLowest(M.root).student.studentId;
    
        out.println(bestStudentM + " " + worstStudentM);
    }    

    private static void K() {
        classList.head.prev = null;
        classList.tail.next = null;
    
        classList.head = classList.sort(classList.head);
    
        ClassNode temp = classList.head;
        ClassNode prevNode = null;
        int tempPos = 1;
        int pakcilPos = 1;
    
        while (temp != null) {
            if (temp == classList.pakcilNow) {
                pakcilPos = tempPos;
            }
    
            prevNode = temp;
            temp = temp.next;
            tempPos++;
        }
    
        if (prevNode != null) {
            classList.tail = prevNode;
            classList.tail.next = classList.head;
            classList.head.prev = classList.tail;
        }
    
        temp = classList.head;
        for (int i = 1; i < pakcilPos; i++) {
            temp = temp.next;
        }

        classList.pakcilNow = temp;
        out.println(pakcilPos);
    }

    private static void A() {
        int N = in.nextInt();

        if (currentIdx + N > arrStudents.length) {
            Student[] newArrStudents = new Student[currentIdx + N];
            System.arraycopy(arrStudents, 0, newArrStudents, 0, arrStudents.length);
            arrStudents = newArrStudents;
        }
    
        StudentTree siswaTree = new StudentTree(INIT_CLASS_ID);
        for (int i = 0; i < N; i++) {
            Student student = new Student(1 + currentIdx, 0);
            arrStudents[currentIdx++] = student;
            siswaTree.insert(siswaTree.root, student);
        }
    
        classList.add(siswaTree);
        out.println(classList.tail.treeSiswa.classId);
    }    

    private static void helperC(StudentTree siswaTree, Student student, int cheat, int studentId) {
        if (student.cheat == 1) {
            siswaTree.delete(siswaTree.root, student);
            student.points = 0;
            siswaTree.insert(siswaTree.root, student);
            out.println(student.points);
        } else if (student.cheat == 2) {
            siswaTree.delete(siswaTree.root, student);
            student.points = 0;

            ClassNode lastNode = classList.tail;
            StudentTree worstClass = lastNode.treeSiswa;
            worstClass.insert(worstClass.root, student);

            out.println(lastNode.treeSiswa.classId);
        } else if (student.cheat == 3) {
            siswaTree.delete(siswaTree.root, student);
            out.println(studentId);
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

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public char nextChar() { 
            return next().charAt(0); 
        }
    }
}

class ClassNode {
    ClassNode next, prev;
    StudentTree treeSiswa;
    
    public ClassNode(StudentTree treeSiswa) {
        this.treeSiswa = treeSiswa;
    }
}

class CircularDoublyLL {
    ClassNode head, tail, pakcilNow;
    int counter = 1;
    int size = 0;

    public ClassNode add(StudentTree treeSiswa) {
        if (treeSiswa.classId == Integer.MIN_VALUE) {
            treeSiswa.classId = counter++;
        }
        
        ClassNode newNode = new ClassNode(treeSiswa);
        if (size == 0) {
            newNode.next = newNode;
            newNode.prev = newNode;
            head = newNode;
            pakcilNow = newNode;
            tail = newNode;
        } else {
            newNode.prev = tail;
            newNode.next = head;
            tail.next = newNode;
            head.prev = newNode;
            tail = newNode;
        }
        size++;

        return newNode;
    }

    public ClassNode move(char direction) {
        pakcilNow = (direction == 'R') ? pakcilNow.next : pakcilNow.prev;
        return pakcilNow;
    }

    public ClassNode sort(ClassNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ClassNode mid = getMid(head);
        ClassNode nextMid = mid.next;

        mid.next = null;
        nextMid.prev = null;

        ClassNode left = sort(head);
        ClassNode right = sort(nextMid);

        ClassNode sorted = mergeSort(left, right);
        return sorted;
    }

    private ClassNode mergeSort(ClassNode c1, ClassNode c2) {
        ClassNode result;

        if (c1 == null) { 
            return c2;
        }

        if (c2 == null) {
            return c1;
        }

        if (c1.treeSiswa.compareTo(c2.treeSiswa) <= 0) {
            result = c1;
            result.next = mergeSort(c1.next, c2);
        } else {
            result = c2;
            result.next = mergeSort(c1, c2.next);
        }

        if (result.next != null) {
            result.next.prev = result;
        }

        return result;
    }

    private ClassNode getMid(ClassNode head) {
        if (head == null) return head;

        ClassNode slow = head;
        ClassNode fast = head;

        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }
}

class Student implements Comparable<Student> {
    int studentId, points, cheat;

    public Student(int studentId, int points) {
        this.studentId = studentId;
        this.points = points;
        this.cheat = 0;
    }

    @Override
    public int compareTo(Student other) {
        if (this.points < other.points) {
            return -1;
        } else if (this.points > other.points) {
            return 1;
        } else {
            if (this.studentId > other.studentId) {
                return -1;
            } else if (this.studentId < other.studentId) {
                return 1;
            } else {
                return 0;
            }            
        }
    }
}

class StudentNode {
    StudentNode left, right;
    Student student;
    int height, size;

    public StudentNode(Student student) {
        this.student = student;
        this.height = 1;
        this.size = 1;
    }
}

class StudentTree implements Comparable<StudentTree> {
    StudentNode root;
    int classId;
    int totalPoints = 0;

    public StudentTree(int classId) {
        this.classId = classId;
    }

    int countTutee(StudentNode node, int points) {
        if (node == null) {
            return 0;
        }

        if (node.student.points > points) {
            return countTutee(node.left, points);
        }

        return 1 + size(node.left) + countTutee(node.right, points);
    }

    int height(StudentNode node) {
        if (node == null) {
            return 0;
        }

        return node.height;
    }

    int size(StudentNode node) {
        if (node == null) {
            return 0;
        }
        
        return node.size;
    }

    int getBalance(StudentNode node) {
        if (node == null) {
            return 0;
        }

        return height(node.left) - height(node.right);
    }

    StudentNode singleLeftRotate(StudentNode node) {
        StudentNode node2 = node.right;
        node.right = node2.left;
        node2.left = node;

        node.height = Math.max(height(node.left), height(node.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;

        node.size = 1 + size(node.left) + size(node.right);
        node2.size = 1 + size(node2.left) + size(node2.right);

        return node2;
    }

    StudentNode singleRightRotate(StudentNode node) {
        StudentNode node2 = node.left;
        node.left = node2.right;
        node2.right = node;

        node.height = Math.max(height(node.left), height(node.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;

        node.size = 1 + size(node.left) + size(node.right);
        node2.size = 1 + size(node2.left) + size(node2.right);

        return node2;
    }

    void insert(StudentNode node, Student student) {
        totalPoints += student.points;
        root = insertStudent(node, student);
    }

    StudentNode insertStudent(StudentNode node, Student student) {
        if (node == null) {
            node =  new StudentNode(student);
        } else if ((student.compareTo(node.student) > 0)) {
            node.right = insertStudent(node.right, student);
        } else if (student.compareTo(node.student) < 0) {
            node.left = insertStudent(node.left, student);
        }
        
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        node.size = 1 + size(node.left) + size(node.right);
        int balance = getBalance(node);
        
        // Left Left
        if (balance == 2 && getBalance(node.left) == 1) {
            return singleRightRotate(node);
        }
        
        // Right Right 
        if (balance == -2 && getBalance(node.right) == -1) {
            return singleLeftRotate(node);
        }

        // Left Right 
        if (balance == 2 && getBalance(node.left) == -1) {
            node.left = singleLeftRotate(node.left);
            return singleRightRotate(node);
        }

        // Right Left 
        if (balance == -2 && getBalance(node.right) == 1) {
            node.right = singleRightRotate(node.right);
            return singleLeftRotate(node);
        }

        return node;
    }

    void delete(StudentNode node, Student student) {
        if (search(root, student) != null) { 
            totalPoints -= student.points; 
        }

        root = deleteStudent(node, student);
    }

    StudentNode deleteStudent(StudentNode node, Student student) {
        if (node == null) { 
            return node;
        }

        if (student.compareTo(node.student) < 0) {
            node.left = deleteStudent(node.left, student); 
        } else if (student.compareTo(node.student) > 0) { 
            node.right = deleteStudent(node.right, student); 
        } else {
            if ((node.left == null) || (node.right == null)) {
                StudentNode temp = null;

                if (temp == node.left) {
                    temp = node.right; 
                } else { 
                    temp = node.left; 
                }

                node = temp;
            } else {
                // Successor Inorder (Delete minimum di subtree kanan)
                StudentNode temp = getLowest(node.right);
                node.student = temp.student;
                node.right = deleteStudent(node.right, temp.student);
            }
        }

        if (node == null) { 
            return node; 
        }

        node.height = Math.max(height(node.left), height(node.right)) + 1;
        node.size = 1 + size(node.left) + size(node.right);
        int balance = getBalance(node);
        
        // Left Left
        if (balance == 2 && getBalance(node.left) == 1) {
            return singleRightRotate(node);
        }
        
        // Right Right 
        if (balance == -2 && getBalance(node.right) == -1) {
            return singleLeftRotate(node);
        }

        // Left Right 
        if (balance == 2 && getBalance(node.left) == -1) {
            node.left = singleLeftRotate(node.left);
            return singleRightRotate(node);
        }

        // Right Left 
        if (balance == -2 && getBalance(node.right) == 1) {
            node.right = singleRightRotate(node.right);
            return singleLeftRotate(node);
        }

        return node;
    }

    StudentNode search(StudentNode node, Student student) {
        if (node == null) {
            return node;
        }
        
        if (student.compareTo(node.student) < 0) {
            return search(node.left, student);
        }
        
        if (student.compareTo(node.student) > 0) {
            return search(node.right, student);
        }

        return node;
    }

    StudentNode getHighest(StudentNode node) {
        StudentNode current = node;

        while (current.right != null) {
            current = current.right;
        }

        return current;
    }

    StudentNode getLowest(StudentNode node) {
        StudentNode current = node;

        while (current.left != null) {
            current = current.left;
        }

        return current;
    }

    @Override
    public int compareTo(StudentTree other) {
        double averageA = (double) this.totalPoints / this.root.size;
        double averageB = (double) other.totalPoints / other.root.size;

        if (averageA > averageB) {
            return -1;
        } else if (averageA < averageB) {
            return 1;
        } else {
            if (this.classId < other.classId) {
                return -1;
            } else if (this.classId > other.classId) {
                return 1;
            } else {
                return 0;
            }            
        }
    }
}