import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Util {
    public static List<Integer> getPrimeRange(int startValue, int endValue) {
        List<Integer> primes = new ArrayList<>();
        boolean[] isPrime = new boolean[endValue+1];
        Arrays.fill(isPrime, true);
        isPrime[0] = isPrime[1] = false;
        for (int i = 2; i * i <= endValue; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j <= endValue; j += i) {
                    isPrime[j] = false;
                }
            }
        }

        for (int i = (int) startValue; i <= endValue; i++) {
            if (isPrime[i]) {
                primes.add(i);
            }
        }
        return primes;
    }
    public static boolean isPerfectPower(int n, int y) {
    if(n < 0) return false;
    if (n == 1) return true;
    if (n == 0) return y == 0;
    if(y < 0 && n % 2 == 0) return false;
    y = Math.abs(y);
    double upperBound = Math.pow(y, 1f/n);
    for(int j = 0; j <= upperBound; j++) {
        if(y == Math.pow(j, n)) return true;
    }
    return false;
    }


//return a mod b
public static int mod(int a, int b) {
        if(b == 0) {
            return a;
        }
        return (a % b + b) % b;
    }
}
