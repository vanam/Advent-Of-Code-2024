import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

enum SequenceType {
    UNKNOWN,
    INCREASING,
    DECREASING,
}

void main() throws Exception {
    AtomicInteger safeReportsCount = new AtomicInteger();
    AtomicInteger safeReportsCount2 = new AtomicInteger();

    // Load the resource file
    try (InputStream inputStream = getClass().getResourceAsStream("02.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        reader.lines().forEach((line) -> {
            List<Integer> report = parseNumbersFromLine(line);
            safeReportsCount.addAndGet(isSafe(report) ? 1 : 0);
            safeReportsCount2.addAndGet(isSafe2(report) ? 1 : 0);
        });
    }

    // 1. part
    System.out.println(safeReportsCount.get());
    // 2. part
    System.out.println(safeReportsCount2.get());
}

SequenceType getDiffType(int a, int b) {
    int diff = a - b;
    int absDiff = Math.abs(diff);
    if (1 <= absDiff && absDiff <= 3) {
        if (diff < 0) {
            return SequenceType.INCREASING;
        } else {
            return SequenceType.DECREASING;
        }
    }
    return null; // Failed: Any two adjacent levels differ by at least one and at most three.
}

boolean isSafe(List<Integer> report) {
    SequenceType type = SequenceType.UNKNOWN;
    for (int i = 0; i < report.size() - 1; i++) {
        SequenceType diffType = getDiffType(report.get(i), report.get(i + 1));
        if (diffType == null) {
            return false; // Failed: Any two adjacent levels differ by at least one and at most three.
        } else if (type == SequenceType.UNKNOWN) {
            type = diffType;
        } else if (type != diffType) {
            return false; // Failed: The levels are either all increasing or all decreasing.
        }
    }
    return true;
}

boolean isSafe2(List<Integer> report) {
    // is it a valid increasing or decreasing report
    return isValidReport(report, -1) || isValidReport(report, 1);
}

boolean isValidReport(List<Integer> report, int sign) {
    // longest increasing/decreasing sequence
    int[] l = new int[report.size()];

    for (int i = 0; i < report.size(); i++) {
        l[i] = 1;
        for (int j = 0; j < i; j++) {
            int diff = report.get(j) - report.get(i);

            if (isValidDiff(diff, sign)) {
                l[i] = Math.max(l[i], l[j] + 1);
            }
        }
    }

    // one error allowed
    return Arrays.stream(l).max().getAsInt() >= report.size() - 1;
}

boolean isValidDiff(int diff, int sign) {
    assert sign == 1 || sign == -1;
    return 1 <= diff * sign && diff * sign <= 3;
}

List<Integer> parseNumbersFromLine(String line) {
    return Arrays.stream(line.split("\s+"))
            .mapToInt(Integer::parseInt)
            .boxed()
            .toList();
}