import java.io.*;
import java.util.StringTokenizer;

/**
 * Note:
 * 1. Mahasiswa tidak diperkenankan menggunakan data struktur dari library seperti ArrayList, LinkedList, dll.
 * 2. Mahasiswa diperkenankan membuat/mengubah/menambahkan class, class attribute, instance attribute, tipe data, dan method 
 *    yang sekiranya perlu untuk menyelesaikan permasalahan.
 * 3. Mahasiswa dapat menggunakan method {@code traverse()} dari class {@code DoublyLinkedList}
 *    untuk membantu melakukan print statement debugging.
 */
public class Lab5 {
  private static InputReader in;
  private static PrintWriter out;
  private static DoublyLinkedList rooms = new DoublyLinkedList();

  public static void main(String[] args) {
    InputStream inputStream = System.in;
    in = new InputReader(inputStream);
    OutputStream outputStream = System.out;
    out = new PrintWriter(outputStream);

    Long N = in.nextLong();

    for (int i = 0; i < N; i++) {
      char command = in.nextChar();
      char direction;

      switch (command) {
        case 'A':
          direction = in.nextChar();
          char type = in.nextChar();
          add(type, direction);
          break;
        case 'D':
          direction = in.nextChar();
          out.println(delete(direction));
          break;
        case 'M':
          direction = in.nextChar();
          out.println(move(direction));
          break;
        case 'J':
          direction = in.nextChar();
          out.println(jump(direction));
          break;
      }
    }

    out.close();
  }

  public static void add(char type, char direction) {
    rooms.add(type, direction);
  }

  public static int delete(char direction) {
    ListNode deletedNode = rooms.delete(direction);
    return deletedNode.id;
  }

  public static int move(char direction) {
    ListNode movedNode = rooms.move(direction);
    return movedNode.id;
  }

  public static int jump(char direction) {
    ListNode jumpedNode = rooms.jump(direction);
    return jumpedNode != null ? jumpedNode.id : -1;
  }

  // taken from https://codeforces.com/submissions/Petr
  // together with PrintWriter, these input-output (IO) is much faster than the
  // usual Scanner(System.in) and System.out
  // please use these classes to avoid your fast algorithm gets Time Limit
  // Exceeded caused by slow input-output (IO)
  private static class InputReader {
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

    public long nextLong() {
      return Long.parseLong(next());
    }
  }
}

class DoublyLinkedList {
  private int nodeIdCounter = 1;
  ListNode first;
  ListNode current;
  ListNode last;
  int size = 0;
  
  public ListNode add(Object element, char direction) {
    ListNode newNode = new ListNode(element, nodeIdCounter++);
    if (size == 0) {
      first = last = current = newNode;
      newNode.next = newNode.prev = newNode;
    } else {
      if (direction == 'R') {
        newNode.next = current.next;
        newNode.prev = current;
        current.next.prev = newNode;
        current.next = newNode;
      } else {
        newNode.prev = current.prev;
        newNode.next = current;
        current.prev.next = newNode;
        current.prev = newNode;
      }
    }
    size++;

    return newNode;
  }

  public ListNode delete(char direction) {
    ListNode deletedNode;
    if (direction == 'R') {
      deletedNode = current.next;
      current.next = deletedNode.next;
      deletedNode.next.prev = current;
    } else {
      deletedNode = current.prev;
      current.prev = deletedNode.prev;
      deletedNode.prev.next = current;
    }
    size--;
    
    return deletedNode;
  }

  public ListNode move(char direction) {
    current = direction == 'R' ? current.next : current.prev;
    return current;
  }

  public ListNode jump(char direction) {
    if (current.element.equals('C')) {
      return null;
    }

    ListNode temp = current;
    do {
      temp = direction == 'R' ? temp.next : temp.prev;
      
      if (temp.element.equals('S')) {
        current = temp;
        return current;
      }
    } while (temp != current);

    return null;
  }
}

class ListNode {
  Object element;
  ListNode next;
  ListNode prev;
  int id;

  ListNode(Object element, int id) {
    this.element = element;
    this.id = id;
  }

  public String toString() {
    return String.format("(ID:%d Elem:%s)", id, element);
  }
}