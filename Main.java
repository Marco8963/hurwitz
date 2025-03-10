import java.io.*;
import java.util.List;

public class Main {
    final static int PURE_DATA_SET_SIZE = 2_000;
    final static int TRIVIAL_DATA_SET_SIZE = 10_000;
    public static void main(String[] args) throws IOException {
        HurwitzClassNumberGenerator g = new HurwitzClassNumberGenerator(3,"result");
        g.clean();
        List<Integer> availablePrimes = g.generate(10, 0L,1_000_000L);
        DataSetManager dataSetManager = new DataSetManager(availablePrimes.size(), "test_a");
        dataSetManager.setSizes(PURE_DATA_SET_SIZE);
        /*
        for(int p: availablePrimes) {
            System.out.printf("Prime: %d\n",p);
                HurwitzClassNumberList hurwitzNumbers = g.open(p,0L,1_000_000L);
                coefficients:
                for(int a = 0; a < 4000  ;  a++) {
                    for(int b = 0; b < 4000 ; b++) {
                        for(int c = 0; c < 4000 ; c++) {
                            if(a == 0 && b == 0) continue;
                            if(
                             (a % 4 == 0 && (b % 4 == 0) && (c % 4 == 1 || c % 4 == 2)) 
                            || (a % 4 == 1 && ((b % 4 == 2 && c % 4 == 2) || (b % 4 == 0 && c % 4 == 1))) 
                            || (a % 4 == 2 && (b % 4 == 2 && (c % 4 == 1 || c % 4 == 2))) 
                            || (a % 4 == 3 && (b % 4 == 0 && c % 4 == 2) || (b % 4 == 2 && c % 4 == 1))) {
                                //dataSetManager.addIfPossible(3, a % p == 0, a,b,c);
                                //dataSetManager.addIfPossible(4, b % p == 0, a,b,c);
                                //dataSetManager.addIfPossible(5, c % p == 0, a,b,c);
                                continue;
                            }
                            int n = 0;
                            int v = c;
                            boolean congruent = true;
                            while(v < 1_000_000) {
                                if(!hurwitzNumbers.isCongruentAt(v)) {
                                    congruent = false;
                                    break;
                                }   
                                n++;
                                v = a*n*n+b*n+c;
                            }
                            if(congruent) {
                                dataSetManager.addIfPossible(0, a % p == 0, a,b,c);
                                /*dataSetManager.addIfPossible(1, b % p == 0, a,b,c);
                                dataSetManager.addIfPossible(2, c % p == 0, a,b,c);
                                dataSetManager.addIfPossible(3, a % p == 0, a,b,c);
                                dataSetManager.addIfPossible(4, b % p == 0, a,b,c);
                                dataSetManager.addIfPossible(5, c % p == 0, a,b,c);
                            }
                            if(dataSetManager.isFull(0)) break coefficients;
                        }
                    }
                }
            dataSetManager.resetCount();
            }
        */
        for(int p: availablePrimes) {
            System.out.printf("Prime: %d\n",p);
                HurwitzClassNumberList hurwitzNumbers = g.open(p,0L,1_000_000L);
                coefficients:
                for(int a = 1; a < 1000 ; a++) {
                for(int b = 0; b < 1000  ;  b++) {
                    
                        int n = 0;
                        int v = b;
                        boolean congruent = true;
                        while(v < 1_000_000) {
                            if(!hurwitzNumbers.isCongruentAt(v)) {
                                congruent = false;
                                break;
                            }   
                            n++;
                            v = a*n+b;
                        }
                        if(congruent && a % p == 0 && b % p == 0) {
                            dataSetManager.addIfPossible(0, a % p == 0, a,b,0);

                            System.out.printf("%dn+%d -> %d,%b,%b\n",a,b,Util.mod(-b,a), a % p == 0,Util.isPerfectPower(2, Util.mod(-b,a)));
                        }
                        //if(dataSetManager.isFull(0)) break coefficients;
                    }
                }
            dataSetManager.resetCount();
            }
        g.shutdown();
        dataSetManager.finish();
        /*try(FileInputStream fis = new FileInputStream("./datasets/dataset_a_congruent")) {
            byte[] buffer = new byte[7];  // 8 bytes for uint64
            while (fis.read(buffer) == 7) {
                ByteBuffer buf = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
                short a = buf.getShort();
                short b = buf.getShort();
                short c = buf.getShort();
                boolean t = buffer[6] == 1;
                System.out.printf("%d, %d, %d, %b\n",a,b,c,t);
            }
        }*/
    }
}
