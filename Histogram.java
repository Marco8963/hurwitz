import java.util.HashMap;
import java.util.Map;

public class Histogram {
    private final Map<Integer, Long> histogram;

    public Histogram() {
        this.histogram = new HashMap<>();
    }

    public void add(int value) {
        if (histogram.containsKey(value)) {
            histogram.put(value, histogram.get(value) + 1);
        } else {
            histogram.put(value, 1L);
        }
    }

    public void add(Histogram histogram) {
        for (int value : histogram.histogram.keySet()) {
            for (int i = 0; i < histogram.histogram.get(value); i++)
                this.add(value);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int value : histogram.keySet()) {
            stringBuilder.append(value);
            stringBuilder.append("|").append(histogram.get(value)).append("\n----\n");
        }
        return stringBuilder.toString();
    }

    public String percentage() {
        double sum = 0;
        for (long count : histogram.values()) {
            sum += count;
        }
        double expectedValue = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (int value : histogram.keySet()) {
            double c = Math.round(100 * (histogram.get(value) / sum));
            expectedValue += (value * c) / 100;
            if (c != 0) {
                stringBuilder.append(value);
                stringBuilder.append("|").append(c).append("%\n----\n");
            }
        }
        stringBuilder.append("mu=").append(expectedValue).append("\n");
        return stringBuilder.toString();
    }
}
