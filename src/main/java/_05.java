import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

void main() throws Exception {
    Map<Integer, Set<Integer>> rules = new HashMap<>();

    try (InputStream inputStream = getClass().getResourceAsStream("05.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

        // read page ordering rules
        while (reader.ready()) {
            String line = reader.readLine();
            if (line.isEmpty()){
                break;
            }
            String[] tokens = line.split("\\|");
            int before = Integer.parseInt(tokens[0]);
            int after = Integer.parseInt(tokens[1]);

            Set<Integer> set = rules.getOrDefault(before, new HashSet<>());
            set.add(after);
            rules.put(before, set);
        }

        // read updates
        int correctlyOrderedUpdates = 0;
        int incorrectlyOrderedUpdates = 0;
        while (reader.ready()) {
            String line = reader.readLine();
            String[] tokens = line.split(",");
            List<Integer> pageNumbers = Arrays.stream(tokens).mapToInt(Integer::parseInt).boxed().toList();

            if (isInOrder(rules, pageNumbers)) {
                correctlyOrderedUpdates  += pageNumbers.get(pageNumbers.size() / 2);
            } else {
                pageNumbers = sort(rules, pageNumbers);
                incorrectlyOrderedUpdates += pageNumbers.get(pageNumbers.size() / 2);
            }
        }
        System.out.println(correctlyOrderedUpdates );
        System.out.println(incorrectlyOrderedUpdates);
    }
}

boolean isInOrder(Map<Integer, Set<Integer>> rules, List<Integer> pageNumbers) {
    for (int i = 1; i < pageNumbers.size(); i++) {
        int x = pageNumbers.get(i);
        Set<Integer> afterX = rules.getOrDefault(x, Set.of());

        // check page numbers before x against the rules
        for (int j = 0; j < i; j++) {
            int b = pageNumbers.get(j);

            if (afterX.contains(b)) {
                return false; // page b must be after page x
            }
        }
    }

    return true;
}

List<Integer> sort(Map<Integer, Set<Integer>> rules, List<Integer> pageNumbers) {
    List<Integer> newPageNumbers = new ArrayList<>(pageNumbers);
    for (int i = 1; i < newPageNumbers.size(); i++) {
        int x = newPageNumbers.get(i);
        Set<Integer> afterX = rules.getOrDefault(x, Set.of());

        // check page numbers before x against the rules
        for (int j = 0; j < i; j++) {
            int b = newPageNumbers.get(j);

            if (afterX.contains(b)) {
                newPageNumbers.remove(i); // remove page x from its current position
                newPageNumbers.add(j, x); // move page x before page b
                break;
            }
        }
    }

    return newPageNumbers;
}