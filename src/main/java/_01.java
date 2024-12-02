import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

void main() throws Exception {
    // 1. part
    List<Integer> listA = new ArrayList<>();
    List<Integer> listB = new ArrayList<>();

    // Load the resource file
    try (InputStream inputStream = getClass().getResourceAsStream("01.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        reader.lines().forEach((line) -> {
            List<Integer> numbers = parseNumbersFromLine(line);
            listA.add(numbers.get(0));
            listB.add(numbers.get(1));
        });
    }

    listA.sort(Integer::compareTo);
    listB.sort(Integer::compareTo);

    int totalDistance = 0;
    for (int i = 0; i < listA.size(); i++) {
        totalDistance += Math.abs(listA.get(i) - listB.get(i));
    }
    System.out.println(totalDistance);

    // 2. part
    Map<Integer, Integer> counterB = new HashMap<>();
    for (Integer integer : listB) {
        counterB.put(integer, counterB.getOrDefault(integer, 0) + 1);
    }
    int similarityScore = 0;
    for (Integer integer : listA) {
        similarityScore += integer * counterB.getOrDefault(integer, 0);
    }
    System.out.println(similarityScore);
}

List<Integer> parseNumbersFromLine(String line) {
    return Arrays.stream(line.split("\s+"))
            .mapToInt(Integer::parseInt)
            .boxed()
            .toList();
}