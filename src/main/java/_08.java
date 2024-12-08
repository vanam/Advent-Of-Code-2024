import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

record Position(int x, int y) { }

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("08.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

        Map<Character, List<Position>> frequency2antennas = new HashMap<>();
        int i = 0;
        int maxX = 0;
        while (reader.ready()) {
            String line = reader.readLine();
            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                if (c != '.') {
                    List<Position> antennas = frequency2antennas.getOrDefault(c, new ArrayList<>());
                    antennas.add(new Position(i, j));
                    frequency2antennas.put(c, antennas);
                }
            }
            maxX = line.length() - 1;
            i++;
        }
        int maxY = i-1;

        Set<Position> antinodes = new HashSet<>();
        Set<Position> antinodes2 = new HashSet<>();
        for (List<Position> antennas : frequency2antennas.values()) {
            if (antennas.size() < 2) {
                continue;
            }

            antinodes2.addAll(antennas); // In part 2 antennas itself are antinodes as well

            for (i = 0; i < antennas.size(); i++) {
                Position a1 = antennas.get(i);
                for (int j = i+1; j < antennas.size(); j++) {
                    Position a2 = antennas.get(j);
                    int dx = a1.x - a2.x;
                    int dy = a1.y - a2.y;

                    for (int k = 1; ; k++) { // calculate antinodes before first antenna
                        Position an1 = new Position(a1.x + k*dx, a1.y + k*dy);
                        if (an1.x >= 0 && an1.x <= maxX && an1.y >= 0 && an1.y <= maxY) {
                            antinodes2.add(an1);

                            if (k == 1) {
                                antinodes.add(an1); // In part 1 only the first antinode counts
                            }
                        } else {
                            break; // out of bounds
                        }
                    }

                    for (int k = 1; ; k++) { // calculate antinodes after second antenna
                        Position an2 = new Position(a2.x - k*dx, a2.y - k*dy);
                        if (an2.x >= 0 && an2.x <= maxX && an2.y >= 0 && an2.y <= maxY) {
                            antinodes2.add(an2);

                            if (k == 1) {
                                antinodes.add(an2); // In part 1 only the first antinode counts
                            }
                        } else {
                            break; // out of bounds
                        }
                    }
                }
            }
        }

        System.out.println(antinodes.size());
        System.out.println(antinodes2.size());
    }
}