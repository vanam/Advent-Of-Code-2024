import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("03.txt")) {
        String fileContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        System.out.println(part1(fileContent));
        System.out.println(part2(fileContent));
    }
}

int part1(String fileContent) {
    Pattern pattern = Pattern.compile("mul\\((\\d+),(\\d+)\\)"); // mul(A,B)
    Matcher matcher = pattern.matcher(fileContent);

    int result = 0;
    while (matcher.find()) {
        String a = matcher.group(1); // Group 1 is A
        String b = matcher.group(2); // Group 2 is B

        result += Integer.parseInt(a) * Integer.parseInt(b);
    }

    return result;
}

int part2(String fileContent) {
    Pattern pattern = Pattern.compile("(do\\(\\))|(don't\\(\\))|(mul\\((\\d+),(\\d+)\\))"); // do() or don't() or mul(A,B)
    Matcher matcher = pattern.matcher(fileContent);

    int result = 0;
    boolean mulEnabled = true;
    while (matcher.find()) {
        if (matcher.group().startsWith("don")) {
            mulEnabled = false;
            continue;
        } else if (matcher.group().startsWith("do")) {
            mulEnabled = true;
            continue;
        } else if (!mulEnabled) {
            continue;
        }
        String a = matcher.group(4); // Group 4 is A
        String b = matcher.group(5); // Group 5 is B

        result += Integer.parseInt(a) * Integer.parseInt(b);
    }

    return result;
}