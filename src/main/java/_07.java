import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

enum Operator {
    ADD,
    MULTIPLY,
    CONCAT;

    long call(long a, long b) {
        return switch (this) {
            case ADD -> a + b;
            case MULTIPLY -> a * b;
            case CONCAT -> a * powerN(10L, digitCount(b)) + b;
        };
    }
}

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("07.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

        long totalCalibrationSum = 0;
        Operator[] ops1 = {Operator.ADD, Operator.MULTIPLY};

        long totalCalibrationSum2 = 0;
        Operator[] ops2 = {Operator.ADD, Operator.MULTIPLY, Operator.CONCAT};
        
        while (reader.ready()) {
            String line = reader.readLine();
            List<Long> numbers = parseNumbersFromLine(line);

            long testValue = numbers.get(0);
            numbers.removeFirst();

            if (hasSolution(testValue, numbers, ops1)) {
                totalCalibrationSum += testValue;
            }

            if (hasSolution(testValue, numbers, ops2)) {
                totalCalibrationSum2 += testValue;
            }
        }

        System.out.println(totalCalibrationSum);
        System.out.println(totalCalibrationSum2);
    }
}

boolean hasSolution(long testValue, List<Long> numbers, Operator[] ops) {
    List<Long> subResults = new ArrayList<>();
    subResults.add(numbers.get(0));

    for (int i = 1; i < numbers.size(); i++) {
        long b = numbers.get(i);
        List<Long> newSubResults = new ArrayList<>();
        for (long a : subResults) {
            for (Operator op : ops) {
                long c = op.call(a, b);
                if (c <= testValue) { // keep only possible solutions
                    newSubResults.add(op.call(a, b));
                }
            }
        }
        subResults = newSubResults;
    }

    return subResults.contains(testValue); // Note: List is faster than Set
}

static int digitCount(long number) {
    assert number >= 0;
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
    return Arrays.stream(line.split("[\s:]+"))
            .mapToLong(Long::parseLong)
            .boxed()
            .collect(Collectors.toCollection(ArrayList::new));
}