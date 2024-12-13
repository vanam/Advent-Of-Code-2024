import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("13.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

        Pattern pattern = Pattern.compile("^[A-Za-z\\s]*: X[\\+=](?<dx>\\d+), Y[\\+=](?<dy>\\d+)$");

        int totalTokens = 0;
        long totalTokens2 = 0;
        while (reader.ready()) {
            String buttonA = reader.readLine();
            String buttonB = reader.readLine();
            String prize = reader.readLine();
            if (reader.ready()) {
                reader.readLine(); // empty line
            }

            Matcher matcher = pattern.matcher(buttonA);
            matcher.matches();
            long bax = Long.parseLong(matcher.group(1));
            long bay = Long.parseLong(matcher.group(2));

            matcher = pattern.matcher(buttonB);
            matcher.matches();
            long bbx = Long.parseLong(matcher.group(1));
            long bby = Long.parseLong(matcher.group(2));

            matcher = pattern.matcher(prize);
            matcher.matches();
            long px = Long.parseLong(matcher.group(1));
            long py = Long.parseLong(matcher.group(2));

            // 1. part - Naive solution
            int tokens = Integer.MAX_VALUE;
            for (int i = 0; i <= 100; i++) {
                for (int j = 0; j <= 100; j++) {
                    long x = bax * i + bbx * j;
                    long y = bay * i + bby * j;

                    if (x == px && y == py) {
                        tokens = 3 * i + j;
                        break;
                    }
                }
            }
            if (tokens != Integer.MAX_VALUE) {
                totalTokens += tokens;
            }

            // 2. part - Closed formula
            px += 10000000000000L;
            py += 10000000000000L;

            long B = (bay * px - bax * py) / (bbx * bay - bby * bax);
            long A = (px - bbx * B) / bax;

            if ((bay * px - bax * py) % (bbx * bay - bby * bax) == 0 && (px - bbx * B) % bax == 0) {
                totalTokens2 += 3 * A + B;
            }
        }

        System.out.println(totalTokens);
        System.out.println(totalTokens2);
    }
}
