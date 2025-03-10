import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HurwitzClassNumberGenerator {
    private final String path;
    private final ExecutorService executor;
    private final int threadCount;

    public HurwitzClassNumberGenerator(int threadCount, String path) {
        this.threadCount = threadCount;
        this.path = path;
        executor = Executors.newFixedThreadPool(threadCount);
    }

    public List<Integer> generate(List<Integer> primes, long classNumbersLowerBound, long classNumbersUpperBound) {
        CountDownLatch latch = new CountDownLatch(primes.size());
        List<Integer> resolvedPrimes = new ArrayList<>();
        String baseDirectory = path + "/temp";
        for (Integer prime : primes) {
            executor.execute(() -> {
                try {
                    Process process = new ProcessBuilder("./hurwitz-sigma", String.valueOf(classNumbersLowerBound),
                            String.valueOf(classNumbersUpperBound), String.valueOf(prime), "-o",
                            baseDirectory + "/sigma").start();
                    process.waitFor();

                    process = new ProcessBuilder("./hurwitz-lambda", String.valueOf(classNumbersLowerBound),
                            String.valueOf(classNumbersUpperBound), String.valueOf(prime), "-o",
                            baseDirectory + "/lambda").start();
                    process.waitFor();

                    process = new ProcessBuilder("./hurwitz-rhs", String.valueOf(classNumbersLowerBound),
                            String.valueOf(classNumbersUpperBound), String.valueOf(prime), "--sigma_dir",
                            baseDirectory + "/sigma", "--lambda_dir", baseDirectory + "/lambda", "-o",
                            baseDirectory + "/rhs").start();
                    process.waitFor();

                    process = new ProcessBuilder("rm", baseDirectory + "/lambda/lambda_mod" +
                            String.valueOf(prime) + "_0_" + String.valueOf(classNumbersUpperBound)).start();
                    process.waitFor();
                    process = new ProcessBuilder("rm", baseDirectory + "/sigma/sigma_mod" +
                            String.valueOf(prime) + "_0_" + String.valueOf(classNumbersUpperBound)).start();
                    process.waitFor();

                    process = new ProcessBuilder("./hurwitz", String.valueOf(classNumbersLowerBound),
                            String.valueOf(classNumbersUpperBound), String.valueOf(prime), "--rhs_dir",
                            baseDirectory + "/rhs", "-o", path).start();
                    process.waitFor();
                    process = new ProcessBuilder("rm", baseDirectory + "/rhs/hurwitz_rhs_mod" +
                            String.valueOf(prime) + "_0_" + String.valueOf(classNumbersUpperBound)).start();
                    process.waitFor();

                    long count = classNumbersUpperBound / 1_200_000L;
                    List<String> files = new ArrayList<>();
                    for (int j = 0; j <= count; j++) {
                        String name = "./" + path + "/hurwitz_mod" + prime + "_" + String.valueOf(1_200_000L * j) + "_"
                                + String.valueOf(Math.min(classNumbersUpperBound, 1_200_000L * (j + 1)));
                        files.add(name);
                        if (prime <= 128) {
                            compressToByte(name);
                        } else {
                            compressToShort(name);
                        }
                    }
                    combine(files, "./" + path + "/hurwitz_mod" + prime + "_" + String.valueOf(classNumbersLowerBound)
                            + "_" + String.valueOf(classNumbersUpperBound));
                    latch.countDown();
                    resolvedPrimes.add(prime);

                } catch (IOException | InterruptedException e) {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            // does not really matter, right?
        }
        return resolvedPrimes;
    }

    public List<Integer> generate(Integer primesUpperBound, long classNumbersLowerBound, long classNumbersUpperBound)
            throws IOException, InterruptedException {
        List<Integer> primes = Util.getPrimeRange(5, primesUpperBound);
        List<Integer> resolvedPrimes = new ArrayList<>();
        for (int i = 0; i < primes.size(); i += threadCount) {
            resolvedPrimes.addAll(generate(primes.subList(i, Math.min(i + threadCount, primes.size())),
                    classNumbersLowerBound, classNumbersUpperBound));
        }
        Process process = new ProcessBuilder("rm", "-rf", path + "/temp").start();
        process.waitFor();
        return resolvedPrimes;
    }

    public void compressToShort(String name) {
        File inputFile = new File(name);
        File outputFile = new File(name + "_short");
        try (FileInputStream fis = new FileInputStream(inputFile);
                FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[8]; // 8 bytes for uint64
            while (fis.read(buffer) == 8) {
                short num = (short) ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getLong();
                fos.write((byte) (num & 0xff));
                fos.write((byte) ((num >> 8) & 0xff));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputFile.renameTo(inputFile);
    }

    public void compressToByte(String name) {
        File inputFile = new File(name);
        File outputFile = new File(name + "_byte");
        try (FileInputStream fis = new FileInputStream(inputFile);
                FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[8]; // 8 bytes for uint64
            while (fis.read(buffer) == 8) {
                byte num = (byte) ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getLong();
                fos.write(num);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputFile.renameTo(inputFile);
    }

    public void shutdown() {
        executor.shutdown();
    }

    public boolean clean() {
        try {
            Process process = new ProcessBuilder("rm", "-rf", path).start();
            process.waitFor();
            String basePath = path + "/temp/";
            process = new ProcessBuilder("mkdir", path, basePath, basePath + "lambda", basePath + "sigma",
                    basePath + "rhs").start();
            process.waitFor();
            return true;
        } catch (InterruptedException | IOException e) {
            return false;
        }
    }

    public void combine(List<String> files, String newFile) throws IOException, InterruptedException {
        try (FileOutputStream fos = new FileOutputStream(newFile)) {
            byte[] buffer = new byte[1];
            for (String file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    while (fis.read(buffer) == 1) {
                        fos.write(buffer);
                    }
                }
                Process process = new ProcessBuilder("rm", "-rf", file).start();
                process.waitFor();
            }
        }
    }

    public HurwitzClassNumberList open(int prime, long classNumbersLowerBound, long classNumbersUpperBound)
            throws IOException {
        if (prime <= 128) {
            return new HurwitzClassNumberListByte(path, prime, classNumbersLowerBound, classNumbersUpperBound);
        }
        return new HurwitzClassNumberListShort(path, prime, classNumbersLowerBound, classNumbersUpperBound);
    }

    private class HurwitzClassNumberListByte implements HurwitzClassNumberList {
        private final List<Byte> values;

        public HurwitzClassNumberListByte(String path, int prime, long classNumbersLowerBound,
                long classNumbersUpperBound) throws IOException {
            values = new ArrayList<>();
            try (FileInputStream fis = new FileInputStream(String.format("./%s/hurwitz_mod%d_%d_%d", path, prime,
                    classNumbersLowerBound, classNumbersUpperBound))) {
                byte[] buffer = new byte[1];
                while (fis.read(buffer) == 1) {
                    values.add(buffer[0]);
                }
            }

        }

        @Override
        public boolean isCongruentAt(long index) throws IndexOutOfBoundsException {
            long r = index % 4;
            long m = index / 4;
            return r == 1 || r == 2 || values.get((int) (2 * m + r / 3)) == 0;

        }

    }

    private class HurwitzClassNumberListShort implements HurwitzClassNumberList {
        private final List<Short> values;

        public HurwitzClassNumberListShort(String path, int prime, long classNumbersLowerBound,
                long classNumbersUpperBound) throws IOException {
            values = new ArrayList<>();
            try (FileInputStream fis = new FileInputStream(String.format("./%s/hurwitz_mod%d_%d_%d", path, prime,
                    classNumbersLowerBound, classNumbersUpperBound))) {
                byte[] buffer = new byte[2];
                while (fis.read(buffer) == 2) {
                    short num = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort();
                    values.add(num);
                }
            }
        }

        @Override
        public boolean isCongruentAt(long index) throws IndexOutOfBoundsException {
            long r = index % 4;
            long m = index / 4;
            return r == 1 || r == 2 || values.get((int) (2 * m + r / 3)) == 0;

        }
    }
}