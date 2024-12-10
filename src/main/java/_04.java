import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

enum Direction {
    LEFT(-1, 0),
    RIGHT(1, 0),
    UP(0, -1),
    DOWN(0, 1),

    LEFT_UP(-1, -1),
    RIGHT_UP(1, -1),
    LEFT_DOWN(-1, 1),
    RIGHT_DOWN(1, 1);

    static final Direction[] DIAGONALS = {LEFT_DOWN, RIGHT_DOWN, LEFT_UP, RIGHT_UP};

    int dx;
    int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}

void main() throws Exception {
    List<String> grid = new ArrayList<>();
    try (InputStream inputStream = getClass().getResourceAsStream("04.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        reader.lines().forEach(grid::add);
    }

    int xmasCount = 0;
    int xmasCount2 = 0;
    for (int i = 0; i < grid.size(); i++) {
        for (int j = 0; j < grid.get(0).length(); j++) {
            if (grid.get(i).charAt(j) == 'X') {
                xmasCount += searchXmas(grid, i, j);
            }
            if (grid.get(i).charAt(j) == 'A') {
                xmasCount2 += searchXmas2(grid, i, j);
            }
        }
    }

    System.out.println(xmasCount);
    System.out.println(xmasCount2);
}

private int searchXmas(List<String> grid, int si, int sj) {
    int count = 0;
    String xmas = "XMAS";

    for (Direction dir : Direction.values()) {
        if (containsWord(grid, xmas, dir, si, sj)) {
            count++;
        }
    }
    return count;
}

boolean containsWord(List<String> grid, String word, Direction dir, int si, int sj) {
    for (int x = 0; x < word.length(); x++) {
        int i = si + x * dir.dy;
        int j = sj + x * dir.dx;
        if (i < 0 || i >= grid.size() || j < 0 || j >= grid.get(0).length()) {
            return false; // out of bounds
        }
        if (grid.get(i).charAt(j) != word.charAt(x)) {
            return false; // no match
        }
    }
    return true;
}

private int searchXmas2(List<String> grid, int ssi, int ssj) {
    String mas = "MAS";
    int ds = mas.length() / 2;
    int masCount = 0;

    for (Direction dir : Direction.DIAGONALS) {
        int si = ssi - dir.dy * ds;
        int sj = ssj - dir.dx * ds;

        if (containsWord(grid, mas, dir, si, sj)) {
            masCount++;
        }
    }

    return masCount == 2 ? 1 : 0;
}