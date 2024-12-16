import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("16.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

        // Read maze
        char [][] maze = reader.lines()
                .map(String::toCharArray)
                .toArray(char[][]::new);

        int maxX = maze[0].length;
        int maxY = maze.length;

        // Modified Dijsktra's algorithm
        Position2 start = new Position2(1, maxY-2, Direction.RIGHT);
        Position end = new Position(maxX-2, 1);

        Map<Position2, Integer> distances = new HashMap<>();
        Map<Position2, List<Position2>> bestPath = new HashMap<>();
        Queue<Node> q = new PriorityQueue<>();
        q.add(new Node(start, 0, List.of(start)));
        Node result = null;

        while (!q.isEmpty()) {
            Node u = q.poll();

            // If the node has already been visited, skip it.
            if (bestPath.containsKey(u.p)) {
                // Update the best paths if we reach the same node with the same cost twice
                if (distances.get(u.p) == u.cost) {
                    List<Position2> posTiles2 = new ArrayList<>(bestPath.get(u.p));
                    posTiles2.addAll(u.tiles2());
                    bestPath.put(u.p, posTiles2);
                }
                continue;
            }

            // Mark the node as visited.
            bestPath.put(u.p, u.tiles2()); // remember best path

            // Solution reached?
            if (u.p.position().equals(end)) {
                result = u;
                break;
            }

            // Move counter-clockwise, straight, clockwise
            for (int i = -1; i <= 1; i++) {
                Position2 p = i == 0 ? u.p.move() : u.p;
                Direction dir = i == 0 ? u.p.d : u.p.d.rotate(i);
                p = new Position2(p.x, p.y, dir);

                if (maze[p.y][p.x] != '#') {
                    int d = i == 0 ? u.cost + 1 : u.cost + 1000;

                    if (d <= distances.getOrDefault(p, Integer.MAX_VALUE)) {
                        distances.put(p, d);

                        List<Position2> tiles2 = new ArrayList<>(u.tiles2());
                        tiles2.add(p);

                        q.add(new Node(p, d, tiles2));
                    }
                }
            }
        }

        // 1. part
        System.out.println(result.cost);

        // Assume there is only one direction we reach the end from
        List<Position2> toProcess = new ArrayList<>(result.tiles2());
        Set<Position2> processed = new HashSet<>();

        Set<Position> bestPathTiles = new HashSet<>();
        while (!toProcess.isEmpty()) {
            Position2 p = toProcess.removeLast();
            if (processed.contains(p)) {
                continue;
            }

            // Collect all best path tiles
            bestPathTiles.add(p.position());

            // Add the best path to reach tile p to processing queue
            toProcess.addAll(bestPath.get(p));
            processed.add(p);
        }

        // 2. part
        System.out.println(bestPathTiles.size());
    }
}

record Node(Position2 p, int cost, List<Position2> tiles2) implements Comparable<Node> {
    @Override
    public int compareTo(Node n) {
        return cost - n.cost();
    }
}

record Position(int x, int y) { }
record Position2(int x, int y, Direction d) {
    Position position() {
        return new Position(x, y);
    }

    Position2 move() {
        return new Position2(x + d.dx, y + d.dy, d);
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

    Direction rotateClockwise() {
        return switch (this) {
            case LEFT -> UP;
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
        };
    }

    Direction rotateCounterClockwise() {
        return switch (this) {
            case LEFT -> DOWN;
            case DOWN -> RIGHT;
            case RIGHT -> UP;
            case UP -> LEFT;
        };
    }

    Direction rotate(int r) {
        if (r == 0) {
            return this;
        } else if (r == -1) {
            return this.rotateCounterClockwise();
        } else if (r == 1) {
            return this.rotateClockwise();
        }
        return null;
    }
}