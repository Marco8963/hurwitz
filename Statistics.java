import java.io.IOException;

public class Statistics {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.exit(1);
        HurwitzClassNumberGenerator g = new HurwitzClassNumberGenerator(2, "result");
        g.generate(Util.getPrimeRange(30, 100), 0, 100_000_000L);
        // g.clean();
        // List<Integer> resolvedPrimes = g.generate(200, 0, 100_000_000L);
        for (int prime : Util.getPrimeRange(5, 100)) {
            Histogram noncongruentHistogram = new Histogram();
            for (int k = 0; k < 4; k++) {
                System.out.printf("bound=%d\n", k * 100);
                HurwitzClassNumberList hurwitzNumbers = g.open(prime, 0, 100_000_000L);
                for (int a = 1 + k * 100; a < 1 + (k + 1) * 100; a++) {
                    for (int b = k * 100; b < (k + 1) * 100; b++) {
                        for (int c = k * 100; c < (k + 1) * 100; c++) {
                            Sequence sequence = new Sequence();
                            // System.out.printf("H(%dn²+%dn+%d for prime: %d)\n",a,b,c,prime);
                            Histogram histogram = new Histogram();
                            int n = 0;
                            int lastCongruency = -1;
                            int v = b;
                            boolean congruent = true;
                            while (v < 100_000_000L) {
                                if (hurwitzNumbers.isCongruentAt(v)) {
                                    if (lastCongruency != -1) {
                                        histogram.add(n - lastCongruency);
                                        sequence.add(n - lastCongruency);
                                    }
                                    lastCongruency = n;
                                } else {
                                    congruent = false;
                                }
                                n++;
                                v = a * n * n + b * n + c;
                            }
                            if (sequence.getReps() != 1 && !sequence.isCongruent()) {
                                System.out.printf("%s, %dn²+%dn+%d\n", sequence, a, b, c);
                            }
                            if (!congruent) {
                                noncongruentHistogram.add(histogram);
                            }
                        }

                    }
                }
                System.err.printf("\n\nPrime: %d\n", prime);
                System.out.println(noncongruentHistogram.percentage());
            }
        }
        g.shutdown();
    }
}
