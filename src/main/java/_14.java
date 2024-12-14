import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

static int MAX_X = 101;
static int MAX_Y = 103;
static int MIDDLE_X = MAX_X / 2;
static int MIDDLE_Y = MAX_Y / 2;

record Robot(int x, int y, int vx, int vy){
    Robot move() {
        return new Robot((x + vx + MAX_X) % MAX_X, (y + vy + MAX_Y) % MAX_Y, vx, vy);
    }

    int getQuadrant(int iterations) {
        int x = ((this.x + iterations * vx) % MAX_X + MAX_X) % MAX_X;
        int y = ((this.y + iterations * vy) % MAX_Y + MAX_Y) % MAX_Y;

        if (x == MIDDLE_X || y == MIDDLE_Y) {
            return -1; // does not belong to any quadrant
        }

        if (x < MIDDLE_X && y < MIDDLE_Y) {
            return 1;
        } else if (x > MIDDLE_X && y < MIDDLE_Y) {
            return 2;
        } else if (x < MIDDLE_X && y > MIDDLE_Y) {
            return 3;
        } else {
            return 4;
        }
    }
}

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("14.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        Pattern pattern = Pattern.compile("^p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)$");

        List<Robot> robots = new ArrayList<>();
        while (reader.ready()) {
            String line = reader.readLine();
            Matcher matcher = pattern.matcher(line);
            matcher.matches();
            robots.add(new Robot(
               Integer.parseInt(matcher.group(1)),
               Integer.parseInt(matcher.group(2)),
               Integer.parseInt(matcher.group(3)),
               Integer.parseInt(matcher.group(4))
            ));

        }

        // 1. part - Simulation
        Map<Integer, Integer> quadrantCounts = new HashMap<>();

        for (Robot robot : robots) {
            int q = robot.getQuadrant(100);
            quadrantCounts.put(q, quadrantCounts.getOrDefault(q, 0) + 1);
        }

        long safetyFactor = 1;
        for (Map.Entry<Integer, Integer> qe : quadrantCounts.entrySet()) {
            int q = qe.getKey();
            if (q == -1) {
                continue;
            }
            safetyFactor *= qe.getValue();
        }

        System.out.println(safetyFactor);

        // 2. part - Search for UNKNOWN!!! pattern :( (assuming there will be long horizontal line)
        char[][] grid = new char[MAX_Y][MAX_X];
        for (int t = 0; t < MAX_X * MAX_Y; t++) { // in max MAX_X*MAX_Y iterations we will find solution
            // Clear canvas
            for (int i = 0; i < MAX_Y; i++) {
                for (int j = 0; j < MAX_X; j++) {
                    grid[i][j] = ' ';
                }
            }

            List<Robot> newRobots = new ArrayList<>();
            for (Robot robot : robots) {
                Robot newRobot = robot.move();
                newRobots.add(newRobot);

                grid[newRobot.y][newRobot.x] = '*'; // draw
            }

            robots = newRobots;

            if (Arrays.stream(grid).anyMatch(l -> String.valueOf(l).matches(".*\\*{30}.*"))) { // assume length 30 :(
                System.out.println(t + 1);

                // Print
                for (int i = 0; i < MAX_Y; i++) {
                    System.out.println(grid[i]);
                }
//                System.in.read(); // wait for user to visually find solution
                break;
            }
        }
    }
}
