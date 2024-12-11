import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

record Key(int t, long n) {}
Map<Key, Long> MEMO = new HashMap<>();

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("11.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        List<Long> originalStones = parseNumbersFromLine(reader.readLine());

        // 1. part - Simulation with list of stones
        List<Long> stones = new ArrayList<>(originalStones);
        for (int t = 0; t < 25; t++) {
            List<Long> newStones = new ArrayList<>();

            for (Long n : stones) {
                if (n == 0) {
                    newStones.add(1L);
                } else if (digitCount(n) % 2 == 0) {
                    int digitCount = digitCount(n);
                    long a = 0;
                    for (int i = 0; i < digitCount / 2; i++) {
                        a += (n % 10) * powerN(10, i);
                        n /= 10;
                    }
                    newStones.add(n);
                    newStones.add(a);
                } else {
                    newStones.add(n * 2024);
                }
            }
            stones = newStones;
        }
        System.out.println(stones.size());

        // 2. part - Dynamic programming
        long count = 0;
        for (Long n : originalStones) {
            count += simulate(0, n,75);
        }
        System.out.println(count);
    }
}

long simulate(int t, long n, long blinks) {
    if (t >= blinks) {
        return 1L;
    }

    Key key = new Key(t, n);
    if (MEMO.containsKey(key)) {
        return MEMO.get(key);
    }

    long value;
    if (n == 0) {
        value = simulate(t+1, 1L, blinks);
    } else if (digitCount(n) % 2 == 0) {
        int digitCount = digitCount(n);
        long a = 0;
        for (int i = 0; i < digitCount / 2; i++) {
            a += (n % 10) * powerN(10, i);
            n /= 10;
        }
        value = simulate(t+1, n, blinks) + simulate(t+1, a, blinks);
    } else {
        value = simulate(t+1, n * 2024, blinks);
    }

    MEMO.put(key, value);
    return value;
}

static int digitCount(long number) {
    return (int)(Math.log10(number)+1);
}

static long powerN(long number, int power){
    long res = 1;
    long sq = number;
    while(power > 0){
        if(power % 2 == 1){
            res *= sq;
        }
        sq = sq * sq;
        power /= 2;
    }
    return res;
}

List<Long> parseNumbersFromLine(String line) {
    return Arrays.stream(line.split("\s+"))
            .mapToLong(Long::parseLong)
            .boxed()
            .toList();
}