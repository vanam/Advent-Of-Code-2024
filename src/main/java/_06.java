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

record Position(int x, int y) { }

record Guard(int x, int y, Direction d) {
    Position position(){
        return new Position(x, y);
    }

    Guard move() {
        return new Guard(x + d.dx, y + d.dy, d);
    }

    Guard turn() {
        return new Guard(
                x, y,
                switch (this.d) {
                    case LEFT -> Direction.UP;
                    case RIGHT -> Direction.DOWN;
                    case UP -> Direction.RIGHT;
                    case DOWN -> Direction.LEFT;
                }
        );
    }
}

void main() throws Exception {
    List<String> grid = new ArrayList<>();

    try (InputStream inputStream = getClass().getResourceAsStream("06.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

        Guard start = null;
        int y = 0;
        while (reader.ready()) {
            String line = reader.readLine();

            int x = line.indexOf('^');
            if (x != -1) {
                start = new Guard(x, y, Direction.UP);
            };
            grid.add(line);
            y++;
        }

        // 1. part
        Set<Position> visited = new HashSet<>();
        visited.add(start.position());
        Guard guard = start;
        while (true) {
            Guard ng = guard.move();
            if (ng.y < 0 || ng.y >= grid.size() || ng.x < 0 || ng.x >= grid.get(0).length()) {
                break;
            }
            if (grid.get(ng.y).charAt(ng.x) == '#') {
                guard = guard.turn();
            } else {
                guard = ng;
                visited.add(guard.position());
            }
        }
        System.out.println(visited.size());

//        // 2. part - Brute force - place obstruction at every position in the grid
//        int obstructionCount = 0;
//        for (int oy = 0; oy < grid.size(); oy++) {
//            for (int ox = 0; ox < grid.get(0).length(); ox++) {
//                if (grid.get(oy).charAt(ox) == '#' || grid.get(oy).charAt(ox) == '^') {
//                    continue;
//                }
//
//                if (isLooping(grid, new Position(ox, oy), start)) {
//                    obstructionCount++;
//                }
//            }
//        }
//        System.out.println(obstructionCount);

        // 2. part - Brute force - place obstruction at every position from the visited path
        int obstructionCount = 0;
        for (Position position : visited) {
            if (start.position().equals(position)) {
                continue;
            }

            if (isLooping(grid, position, start)) {
                obstructionCount++;
            }
        }

        System.out.println(obstructionCount);
    }
}

boolean isLooping(List<String> grid, Position obstruction, Guard guard) {
    Set<Guard> visited = new HashSet<>();
    visited.add(guard);
    while (true) {
        Guard ng = guard.move();
        if (ng.y < 0 || ng.y >= grid.size() || ng.x < 0 || ng.x >= grid.get(0).length()) {
            return false;
        } else if (visited.contains(ng)) {
            return true;
        }

        if (grid.get(ng.y).charAt(ng.x) == '#' || ng.position().equals(obstruction)) {
            guard = guard.turn();
        } else {
            guard = ng;
            visited.add(guard);
        }
    }
}
