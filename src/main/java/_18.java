import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("18.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        // Read input
        List<Position> mem = new ArrayList<>();
        while (reader.ready()) {
            String line = reader.readLine();
            String[] split = line.split(",");
            mem.add(new Position(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
        }

        // 1. part - Dijkstra’s Algorithm
        System.out.println(pathLength(mem.subList(0, 1024)));

        // 2.part - Brute force
//        for (int i = 1025; i < mem.size(); i++) {
//            if (pathLength(mem.subList(0, i)) == null) {
//                System.out.println(mem.get(i-1).x + "," + mem.get(i-1).y);
//                break;
//            }
//        }

        // 2. part - Binary search
        int low = 1025, high = mem.size() - 1;
        int i = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (pathLength(mem.subList(0, mid)) == null) {
                i = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        System.out.println(mem.get(i-1).x + "," + mem.get(i-1).y);
    }
}

Integer pathLength(List<Position> mem) {
    // Hardcoded memory space dimensions
    int maxX = 71;
    int maxY = maxX;

    Position start = new Position(0, 0);
    Position end = new Position(maxX - 1, maxY - 1);

    Map<Position, Integer> dist = new HashMap<>();
    Queue<Node> q = new PriorityQueue<>();
    q.add(new Node(start, 0));
    while (!q.isEmpty()) {
        Node u = q.poll();

        if (u.p == end) {
            break;
        }

        for (Direction d : Direction.values()) {
            Position v = u.p.move(d);
            if (!v.outOfBounds(maxX, maxY) && !mem.contains(v)) {
                if (u.cost + 1 < dist.getOrDefault(v, Integer.MAX_VALUE)) {
                    dist.put(v, u.cost + 1);
                    q.add(new Node(v, u.cost + 1));
                }
            }
        }
    }

    return dist.get(end);
}

record Node(Position p, int cost) implements Comparable<Node> {
    @Override
    public int compareTo(Node n) {
        return cost - n.cost();
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
