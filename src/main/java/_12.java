import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("12.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

        char [][] garden = reader.lines()
                .map(String::toCharArray)
                .toArray(char[][]::new);
        int maxX = garden[0].length;
        int maxY = garden.length;

        boolean[][] visited = new boolean[maxY][maxX];

        int totalPrice = 0;
        int totalPrice2 = 0;
        // Flood fill for each unvisited plot
        for (int i = 0; i < maxY; i++) {
            for (int j = 0; j < maxX; j++) {
                if (visited[i][j]) {
                    continue;
                }
                int area = 0;
                int perimeter = 0;

                Stack<Position> s = new Stack<>();
                s.push(new Position(j, i));
                char plant = garden[i][j];
                Set<Position> plotVisited = new HashSet<>(); // keep track of positions belonging to the current plot
                List<Position2> edges = new ArrayList<>();   // keep track of edges and direction we came from

                while (!s.isEmpty()) {
                    Position p = s.pop();
                    if (visited[p.y][p.x]) {
                        continue;
                    }
                    visited[p.y][p.x] = true;
                    plotVisited.add(p);
                    area++;

                    // move in all directions
                    for (Direction d : Direction.values()) {
                        Position p2 = p.move(d);
                        if (p2.outOfBounds(maxX, maxY)) {         // edge of the garden
                            perimeter++;
                            edges.add(new Position2(p2.x, p2.y, d));
                        } else if (garden[p2.y][p2.x] == plant) { // explore current plot
                            s.push(p2);
                        } else if (!plotVisited.contains(p2)) {   // we reached a different plot
                            perimeter++;
                            edges.add(new Position2(p2.x, p2.y, d));
                        }
                    }
                }

                totalPrice += area * perimeter;

                int sides = 0;
                while (!edges.isEmpty()) {
                    Position2 e = edges.removeFirst();
                    sides++;

                    // Remove all edges in the direction perpendicular to the direction we reached the edge
                    for (Direction d : e.d.perpendicular()) {
                        for (int k = 1; ; k++) {
                            Position2 e2 = new Position2(e.x + k*d.dx, e.y + k*d.dy, e.d);
                            if (edges.contains(e2)) {
                                edges.remove(e2);
                            } else {
                                break;
                            }
                        }
                    }
                }
                totalPrice2 += area * sides;
            }
        }

        System.out.println(totalPrice);
        System.out.println(totalPrice2);
    }
}

record Position(int x, int y) {
    Position move(Direction d) {
        return new Position(x + d.dx, y + d.dy);
    }

    boolean outOfBounds(int maxX, int maxY) {
        return x < 0 || x >= maxX || y < 0 || y >= maxY;
    }
}
record Position2(int x, int y, Direction d) {}

enum Direction {
    LEFT(-1, 0),
    RIGHT(1, 0),
    UP(0, -1),
    DOWN(0, 1);

    static final Direction[] HORIZONTAL = {LEFT, RIGHT};
    static final Direction[] VERTICAL = {UP, DOWN};

    int dx;
    int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    Direction[] perpendicular() {
        return switch (this) {
            case LEFT, RIGHT -> VERTICAL;
            case UP, DOWN -> HORIZONTAL;
        };
    }
}
