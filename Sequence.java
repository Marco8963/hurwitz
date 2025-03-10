import java.util.ArrayList;
import java.util.List;

public class Sequence {
    private final List<Integer> sequence;
    private int repetations;
    private int index;
    private int size;
    public Sequence() {
        this.sequence = new ArrayList<>();
        this.repetations = 1;
        this.index = 0;
        this.size = 0;
    }
    public Sequence add(int value) {
        if(sequence.isEmpty()) {
            sequence.add(value);
            size++;
            return this;
        }
        if(sequence.get(index) == value) {
            index++;
            if(index >= size) {
                index = 0;
                repetations++;
            }
        } else {
            for(int j = 1; j < repetations; j++) {
                for(int i = 0; i < size; i++) sequence.add(sequence.get(i));
            }
            size *= repetations;
            sequence.add(value);
            index = 0;
            repetations = 1;
            size++;
        }
        return this;
    }
    @Override
    public String toString() {
        return String.format("reps: %d, sequence: %s", repetations,sequence);
    }
    public int getReps() {
        return this.repetations;
    }
    public boolean isCongruent() {
        return sequence.size() == 1 && sequence.get(0) == 1;
    }
}
