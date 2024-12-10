import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

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

record Position(int x, int y) {
    Position move(Direction d) {
        return new Position(x + d.dx, y + d.dy);
    }

    boolean outOfBounds(int maxX, int maxY) {
        if (x < 0 || x >= maxX || y < 0 || y >= maxY) {
            return true;
        }
        return false;
    }
}

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("10.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        List<String> lines = new ArrayList<>();
        reader.lines().forEach(lines::add);

        int maxX = lines.get(0).length();
        int maxY = lines.size();
        int [][] topoMap = new int[maxY][maxX];
        List<Position> startingPositions = new ArrayList<>();

        for (int i = 0; i < maxY; i++) {
            for (int j = 0; j < maxX; j++) {
                char c = lines.get(i).charAt(j);
                int height = c - '0';
                topoMap[i][j] = height;
                if (height == 0) {
                    startingPositions.add(new Position(j, i));
                }
            }
        }

        int sumOfTrailheadScore = 0;
        for (Position start : startingPositions) {
            Set<Position> visited = new HashSet<>();

            Stack<Position> stack = new Stack<>();
            stack.push(start);

            while (!stack.isEmpty()) {
                Position u = stack.pop();
                visited.add(u);
                int uh = topoMap[u.y][u.x];

                if (uh == 9) {
                    sumOfTrailheadScore += 1;
                    continue;
                }

                for (Direction d : Direction.values()) {
                    Position v  = u.move(d);
                    if (v.outOfBounds(maxX, maxY) || visited.contains(v)) {
                        continue;
                    }

                    int vh = topoMap[v.y][v.x];
                    if (vh - uh == 1) {
                        stack.push(v);
                    }
                }
            }
        }
        System.out.println(sumOfTrailheadScore);

        int sumOfDistinctTrailheadScore = 0;
        for (Position start : startingPositions) {
            Stack<Position> stack = new Stack<>();
            stack.push(start);

            while (!stack.isEmpty()) {
                Position u = stack.pop();
                int uh = topoMap[u.y][u.x];

                if (uh == 9) {
                    sumOfDistinctTrailheadScore += 1;
                    continue;
                }

                for (Direction d : Direction.values()) {
                    Position v  = u.move(d);
                    if (v.outOfBounds(maxX, maxY)) {
                        continue;
                    }

                    int vh = topoMap[v.y][v.x];
                    if (vh - uh == 1) {
                        stack.push(v);
                    }
                }
            }
        }
        System.out.println(sumOfDistinctTrailheadScore);
    }
}
