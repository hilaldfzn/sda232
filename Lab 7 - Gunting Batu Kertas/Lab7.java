import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Lab7 {
    static class Box {
        int id;
        long value;
        String state;

        Box(int id, long value, String state) {
            this.id = id;
            this.value = value;
            this.state = state;
        }
    }

    static class BoxContainer {
        public ArrayList<Box> heap;
        public int size;
        public HashMap<Integer, Integer> idToIndexMap;

        public BoxContainer() {
            this.heap = new ArrayList<>();
            this.idToIndexMap = new HashMap<>();
        }

        public static int getParentIndex(int i) {
            return (i - 1) / 2;
        }

        public void percolateUp(int i) {
            while (i > 0 && compareBoxes(heap.get(i), heap.get(getParentIndex(i)))) {
                swap(i, getParentIndex(i));
                i = getParentIndex(i);
            }
        }    
        
        public void percolateDown(int i) {
            int leftIdx = 2 * i + 1;
            int rightIdx = 2 * i + 2;
            int largest = i;
        
            if (leftIdx < size && compareBoxes(heap.get(leftIdx), heap.get(largest))) {
                largest = leftIdx;
            }
        
            if (rightIdx < size && compareBoxes(heap.get(rightIdx), heap.get(largest))) {
                largest = rightIdx;
            }
        
            if (largest != i) {
                swap(i, largest);
                percolateDown(largest);
            }
        }

        private boolean compareBoxes(Box a, Box b) {
            if (a.value == b.value) {
                return a.id < b.id;
            }
            return a.value > b.value;
        }

        public void insert(Box box) {
            heap.add(box);
            int currentIndex = size;
            idToIndexMap.put(box.id, currentIndex);
            size++;
            percolateUp(currentIndex);
        }

        public Box peek() {
            return heap.get(0);
        }

        public void swap(int firstIndex, int secondIndex) {
            Box temp = heap.get(firstIndex);
            heap.set(firstIndex, heap.get(secondIndex));
            heap.set(secondIndex, temp);
            
            idToIndexMap.put(heap.get(firstIndex).id, firstIndex);
            idToIndexMap.put(heap.get(secondIndex).id, secondIndex);
        }

        public void updateBox(Box box, long newValue) {
            int index = idToIndexMap.get(box.id);
            box.value = newValue;
        
            percolateUp(index);
            percolateDown(index);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);
    
        int N = Integer.parseInt(br.readLine());
        ArrayList<Box> boxes = new ArrayList<>();
        BoxContainer boxContainer = new BoxContainer();
    
        for (int i = 0; i < N; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            long value = Long.parseLong(st.nextToken());
            String state = st.nextToken();
    
            Box box = new Box(boxes.size(), value, state);
            boxes.add(box);
            boxContainer.insert(box);
        }
    
        int T = Integer.parseInt(br.readLine());
    
        for (int i = 0; i < T; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            String command = st.nextToken();
    
            if ("A".equals(command)) {
                long value = Long.parseLong(st.nextToken());
                String state = st.nextToken();

                Box boxBaru = new Box(boxes.size(), value, state);
                boxes.add(boxBaru);
                boxContainer.insert(boxBaru);
            } else if ("D".equals(command)) {
                int idI = Integer.parseInt(st.nextToken());
                int idJ = Integer.parseInt(st.nextToken());
        
                Box boxI = boxes.get(idI);
                Box boxJ = boxes.get(idJ);
            
                if (!boxI.state.equals(boxJ.state)) {
                    if ((boxI.state.equals("R") && boxJ.state.equals("S")) ||
                        (boxI.state.equals("P") && boxJ.state.equals("R")) ||
                        (boxI.state.equals("S") && boxJ.state.equals("P"))) {
                        boxI.value += boxJ.value;
                        boxJ.value /= 2;
                    } else {
                        boxJ.value += boxI.value;
                        boxI.value /= 2;
                    }
                    
                    boxContainer.updateBox(boxI, boxI.value);
                    boxContainer.updateBox(boxJ, boxJ.value);
                }
            } else if ("N".equals(command)) {
                int idI = Integer.parseInt(st.nextToken());
                Box boxI = boxes.get(idI);
            
                for (int neighborId : new int[] {idI - 1, idI + 1}) {
                    if (neighborId >= 0 && neighborId < boxes.size()) {
                        Box neighborBox = boxes.get(neighborId);
            
                        if (!boxI.state.equals(neighborBox.state)) {
                            if ((boxI.state.equals("R") && neighborBox.state.equals("S")) ||
                                (boxI.state.equals("P") && neighborBox.state.equals("R")) ||
                                (boxI.state.equals("S") && neighborBox.state.equals("P"))) {
                                boxI.value += neighborBox.value;
                                boxContainer.updateBox(boxI, boxI.value);
                            }
                        }
                    }
                }
            }

            Box topBox = boxContainer.peek();
            pw.println(topBox.value + " " + topBox.state);
        }
    
        pw.flush();
        pw.close();
    }
}