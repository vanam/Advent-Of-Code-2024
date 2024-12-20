import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("20.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        // Read input
        char [][] grid = reader.lines()
                .map(String::toCharArray)
                .toArray(char[][]::new);

        int maxX = grid[0].length;
        int maxY = grid.length;

        // Find start and end position
        Position start = new Position(0, 0);
        Position end = new Position(maxX - 1, maxY - 1);
        for (int i = 0; i < maxY; i++) {
            for (int j = 0; j < maxX; j++) {
                if (grid[i][j] == 'S') {
                    start = new Position(j, i);
                } else if (grid[i][j] == 'E') {
                    end = new Position(j, i);
                }
            }
        }

        // Search for path (there is only one so any algo would do)
        Map<Position, Integer> dist = new HashMap<>();
        dist.put(start, 0);
        Stack<Position> q = new Stack<>();
        q.add(start);
        while (!q.isEmpty()) {
            Position u = q.pop();

            if (u == end) {
                break;
            }

            int distance = dist.get(u);
            for (Direction d : Direction.values()) {
                Position v = u.move(d);

                if (!v.outOfBounds(maxX, maxY) && grid[v.y][v.x] != '#') {
                    if (distance + 1 < dist.getOrDefault(v, Integer.MAX_VALUE)) {
                        dist.put(v, distance + 1);
                        q.add(v);
                    }
                }
            }
        }

        System.out.println(countCheats(grid, maxX, maxY, dist, 2));
        System.out.println(countCheats(grid, maxX, maxY, dist, 20));
    }
}

int countCheats(char [][] grid, int maxX, int maxY, Map<Position, Integer> dist, int cheatLength) {
    int count = 0;
    for (int i = 0; i < maxY; i++) {
        for (int j = 0; j < maxX; j++) {
            if (grid[i][j] != '#') {
                // For every path position try to find cheat in a sliding 2D window of size 2*cheatLength x 2*cheatLength
                Position u = new Position(j, i);
                for (int k = Math.max(0, i - cheatLength - 1); k < Math.min(maxY, i + cheatLength + 1); k++) {
                    for (int l = Math.max(0, j - cheatLength - 1); l < Math.min(maxX, j + cheatLength + 1); l++) {
                        Position v = new Position(l, k);
                        int d = manhattan(u, v);

                        if (d > 0 && d <= cheatLength && grid[v.y][v.x] != '#' && dist.get(v) - dist.get(u) - d >= 100) {
                            count++;
                        }
                    }
                }
            }
        }
    }
    return count;
}

int manhattan(Position u, Position v) {
    return Math.abs(u.x - v.x) + Math.abs(u.y - v.y);
}

record Position(int x, int y) {
    Position move(Direction d) {
        return new Position(x + d.dx, y + d.dy);
    }

    boolean outOfBounds(int maxX, int maxY) {
        return x < 0 || x >= maxX || y < 0 || y >= maxY;
    }
}

enum Direction {
    LEFT(-1, 0),
    RIGHT(1, 0),
    UP(0, -1),
    DOWN(0, 1);

    int dx;
    int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
