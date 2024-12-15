import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

record Position(int x, int y) {
    Position move(Direction d) {
        return new Position(x + d.dx, y + d.dy);
    }
}

enum Direction {
    LEFT(-1, 0),
    RIGHT(1, 0),
    UP(0, -1),
    DOWN(0, 1);

    static final List<Direction> HORIZONTAL = List.of(LEFT, RIGHT);

    int dx;
    int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("15.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

        // Read map
        List<String> mapRows = new ArrayList<>();
        while (reader.ready()) {
            String line = reader.readLine();
            if (line.isEmpty()) {
                break;
            }
            mapRows.add(line);
        }

        // Convert map to char array
        char[][] map = mapRows.stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
        int maxX = map[0].length;
        int maxY = map.length;

        // Expand map for part 2
        int maxX2 = maxX * 2;
        char[][] map2 = new char[maxY][maxX2];

        for (int i = 0; i < maxY; i++) {
            for (int j = 0; j < maxX; j++) {
                if (map[i][j] == 'O') {
                    map2[i][j*2] = '[';
                    map2[i][j*2+1] = ']';
                } else if (map[i][j] == '#') {
                    map2[i][j*2] = '#';
                    map2[i][j*2+1] = '#';
                } else if (map[i][j] == '.') {
                    map2[i][j*2] = '.';
                    map2[i][j*2+1] = '.';
                } else if (map[i][j] == '@') {
                    map2[i][j*2] = '@';
                    map2[i][j*2+1] = '.';
                }
            }
        }

        // Find robot's position for part 1
        Position r = null;
        for (int i = 0; i < maxY; i++) {
            for (int j = 0; j < maxX; j++) {
                if (map[i][j] == '@') {
                    r = new Position(j, i);
                }
            }
        }

        // Find robot's position for part 2
        Position r2 = null;
        for (int i = 0; i < maxY; i++) {
            for (int j = 0; j < maxX2; j++) {
                if (map2[i][j] == '@') {
                    r2 = new Position(j, i);
                }
            }
        }

        // Read robot's movements
        while (reader.ready()) {
            String line = reader.readLine();
            for (int m = 0; m < line.length(); m++) {
                Direction d = switch (line.charAt(m)) {
                    case '^' -> Direction.UP;
                    case 'v' -> Direction.DOWN;
                    case '<' -> Direction.LEFT;
                    case '>' -> Direction.RIGHT;
                    default -> throw new IllegalStateException("Unexpected value: " + line);
                };

                // 1. part
                Position p = r.move(d);
                if (map[p.y][p.x] != '#') { // cannot move walls
                    Position s = null; // find the first space in direction d if any
                    while (true) {
                        if (map[p.y][p.x] == '.') {
                            s = p;
                            break;
                        } else if (map[p.y][p.x] == '#') {
                            break; // cannot move boxes because of the wall
                        }
                        p = p.move(d); // boxes can be moved if there is a space behind them
                    }
                    if (s != null) {
                        map[r.y][r.x] = '.'; // make space where the robot stood
                        r = r.move(d);       // move robot
                        map[r.y][r.x] = '@'; // place robot on the new position (where space or box used to be)
                        map[s.y][s.x] = 'O'; // move box to the available space
                    }
                }

                // 2. part
                Position p2 = r2.move(d);
                if (map2[p2.y][p2.x] == '.') { // simple case of moving robot on empty space
                    map2[r2.y][r2.x] = '.';
                    map2[p2.y][p2.x] = '@';
                    r2 = p2;
                } else if (map2[p2.y][p2.x] == '[' || map2[p2.y][p2.x] == ']') { // moving a box
                    if (Direction.HORIZONTAL.contains(d)) { // moving boxes horizontally is still simple
                        Position s2 = null; // find the first space in direction d if any
                        while (true) {
                            p2 = p2.move(d);
                            if (map2[p2.y][p2.x] == '.') {
                                s2 = p2;
                                break;
                            } else if (map2[p2.y][p2.x] == '#') {
                                break; // cannot move boxes because of the wall
                            }
                        }
                        if (s2 != null) {
                            map2[r2.y][r2.x] = '.'; // make space where the robot stood
                            r2 = r2.move(d);        // move robot
                            map2[r2.y][r2.x] = '@'; // place robot on the new position (where space or box used to be)
                            // redraw moved boxes on the map
                            for (int j = Math.min(s2.x, r2.x+d.dx); j < Math.max(s2.x, r2.x+d.dx); j+=2) {
                                map2[r2.y][j] = '[';
                                map2[r2.y][j+1] = ']';
                            }
                        }
                    } else {
                        // moving boxes vertically can trigger more boxes to move
                        boolean canMove = true;
                        Set<Position> toMove = new HashSet<>(); // boxes which will be affected
                        Stack<Position> stack = new Stack<>(); // keep track of boxes (their left side position) to process
                        if (map2[p2.y][p2.x] == '[') {
                            stack.push(p2);
                            toMove.add(p2);
                        } else {
                            stack.push(new Position(p2.x - 1, p2.y));
                            toMove.add(new Position(p2.x - 1, p2.y));
                        }

                        while (!stack.isEmpty() && canMove) {
                            Position p3 = stack.pop().move(d);
                            if (map2[p3.y][p3.x] == '#' || map2[p3.y][p3.x + 1] == '#') {
                                canMove = false; // cannot move boxes because of the wall
                            } else {
                                if (map2[p3.y][p3.x] == '[') {
                                    // Case: []
                                    //       []
                                    stack.push(p3);
                                    toMove.add(p3);
                                } else {
                                    // Case: []  or   []  or []
                                    //      [][]     [].     .[]
                                    if (map2[p3.y][p3.x] == ']') {
                                        stack.push(new Position(p3.x - 1, p3.y));
                                        toMove.add(new Position(p3.x - 1, p3.y));
                                    }
                                    if (map2[p3.y][p3.x + 1] == '[') {
                                        stack.push(new Position(p3.x + 1, p3.y));
                                        toMove.add(new Position(p3.x + 1, p3.y));
                                    }
                                }
                            }
                        }

                        if (canMove) {
                            // Replace old box positions with spaces
                            for (Position box : toMove) {
                                map2[box.y][box.x] = '.';
                                map2[box.y][box.x+1] = '.';
                            }
                            // Draw boxes on their new position
                            for (Position box : toMove) {
                                map2[box.y + d.dy][box.x] = '[';
                                map2[box.y + d.dy][box.x+1] = ']';
                            }

                            map2[r2.y][r2.x] = '.'; // make space where the robot stood
                            r2 = r2.move(d);        // move robot
                            map2[r2.y][r2.x] = '@'; // place robot on the new position (where space or box used to be)
                        }
                    }
                }
            }
        }

        // 1. part
        int gps = 0;
        for (int i = 0; i < maxY; i++) {
            for (int j = 0; j < maxX; j++) {
                if (map[i][j] == 'O') {
                    gps += 100*i + j;
                }
            }
        }
        System.out.println(gps);

        // 2. part
        int gps2 = 0;
        for (int i = 0; i < maxY; i++) {
            for (int j = 0; j < maxX2; j++) {
                if (map2[i][j] == '[') {
                    gps2 += 100*i + j;
                }
            }
        }
        System.out.println(gps2);
    }
}
