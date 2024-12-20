import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("19.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        List<String> patterns = Arrays.stream(reader.readLine().split(", ")).toList();
        reader.readLine();

        int maxPatternLength = 0;
        for (String pattern : patterns) {
            maxPatternLength = Math.max(maxPatternLength, pattern.length());
        }

        int possibleDesigns = 0;
        long differentWays = 0;
        while (reader.ready()) {
            String towel = reader.readLine();
            long dw = ways(patterns, towel, maxPatternLength);
            if (dw > 0) {
                possibleDesigns++;
                differentWays += dw;
            }
        }
        System.out.println(possibleDesigns);
        System.out.println(differentWays);
    }
}

Map<String, Long> MEMO = new HashMap<>();
long ways(List<String> patterns, String s, int maxPatternLength) {
    if (s.isEmpty()) {
        return 1;
    }

    if (MEMO.containsKey(s)) {
        return MEMO.get(s);
    }

    long result = 0;
    for (int i = 1; i <= Math.min(maxPatternLength, s.length()); i++) {
        if (patterns.contains(s.substring(0, i))) {
            result += ways(patterns, s.substring(i), maxPatternLength);
        }
    }

    MEMO.put(s, result);
    return result;
}
