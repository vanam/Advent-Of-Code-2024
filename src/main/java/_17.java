import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("17.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

        long A = Long.parseLong(reader.readLine().split(": ")[1]);
        long B = Long.parseLong(reader.readLine().split(": ")[1]);
        long C = Long.parseLong(reader.readLine().split(": ")[1]);
        reader.readLine();
        String programLine = reader.readLine().split(": ")[1];
        List<Integer> program = parseNumbersFromLine(programLine);

        // 1. part
        int i = 0;
        List<Long> output = new ArrayList<>();
        while (i <= program.size() - 2) {
            int op = program.get(i);
            long lit = program.get(i+1);

            long combo = lit;
            if (lit == 4) {
                combo = A;
            } else if (lit == 5) {
                combo = B;
            } else if (lit == 6) {
                combo = C;
            }

            switch (op) {
                case 0: // adv
                    A /= powerN(2, combo);
                    i += 2;
                    break;
                case 1: // bxl
                    B ^= lit;
                    i += 2;
                    break;
                case 2: // bst
                    B = combo % 8;
                    i += 2;
                    break;
                case 3: // jnz
                    if (A != 0) {
                        i = (int) lit;
                    } else {
                        i += 2;
                    }
                    break;
                case 4: // bxc
                    B ^= C;
                    i += 2;
                    break;

                case 5: // out
                    output.add(combo % 8);
                    i += 2;
                    break;

                case 6: // bdv
                    B = A / powerN(2, combo);
                    i += 2;
                    break;

                case 7: // cdv
                    C = A / powerN(2, combo);
                    i += 2;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid op: " + op);
            }
        }
        System.out.println(output.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",")));

        // 2. part - Hardcoded input algorithm :(
        List<Integer> expectedOutput = List.of(2,4,1,6,7,5,4,6,1,4,5,5,0,3,3,0);
        long initialRegisterA = solvePart2Hardcoded(expectedOutput, 0, expectedOutput.size() - 1);

        A = initialRegisterA;
        output = new ArrayList<>();
        while (A != 0) {
    //        // 2,4,
    //        B = A % 8;
    //        // 1,6,
    //        B ^= 6;
    //        // 7,5,
    //        C = A / powerN(2, B);
    //        // 4,6,
    //        B ^= C;
    //        // 1,4,
    //        B ^= 4;
    //        // 5,5,
    //        output.add(String.valueOf(B % 8));
    //        // 0,3,
    //        A /= 8;
    //        // 3,0

            // 2,4,1,6,7,5,4,6,1,4,
            long o = (((A % 8) ^ 6) ^ (A / powerN(2, (A % 8) ^ 6)) ^ 4) % 8;

            // 5,5,
            output.add(o);
            // 0,3,
            A /= 8;
            // 3,0
        }

        System.out.println(expectedOutput.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",")));
        System.out.println(output.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",")));
        System.out.println(initialRegisterA);

        // 2. part - Generic algorithm :)
        initialRegisterA = solvePart2(program, program, 0, program.size() - 1);
        System.out.println(initialRegisterA);
    }
}

long solvePart2Hardcoded(List<Integer> expectedOutput, long A, int oi) {
    if (oi < 0) {
        return A; // solution found
    }
    int o = expectedOutput.get(oi);

    for (long d = 0; d < 8; d++) {
        long A2 = A << 3 | d; // A * 8 + d;
        if (((((A2 % 8) ^ 6) ^ (A2 / powerN(2, (A2 % 8) ^ 6)) ^ 4) % 8) == o) {
            long result = solvePart2Hardcoded(expectedOutput, A2, oi - 1);
            if (result != -1) {
                return result;
            }
        }
    }

    return -1; // no solution found
}

long solvePart2(List<Integer> program, List<Integer> expectedOutput, long A0, int oi) {
    if (oi < 0) {
        return A0; // solution found
    }
    int o = expectedOutput.get(oi);

    for (long d = 0; d < 8; d++) {
        long A = A0 << 3 | d;
        long B = 0;
        long C = 0;

        int i = 0;
        prog:
        while (i <= program.size() - 2) {
            int op = program.get(i);
            long lit = program.get(i+1);

            long combo = lit;
            if (lit == 4) {
                combo = A;
            } else if (lit == 5) {
                combo = B;
            } else if (lit == 6) {
                combo = C;
            }

            switch (op) {
                case 0: // adv
                    A /= powerN(2, combo);
                    i += 2;
                    break;
                case 1: // bxl
                    B ^= lit;
                    i += 2;
                    break;
                case 2: // bst
                    B = combo % 8;
                    i += 2;
                    break;
                case 3: // jnz
                    if (A != 0) {
                        i = (int) lit;
                    } else {
                        i += 2;
                    }
                    break;
                case 4: // bxc
                    B ^= C;
                    i += 2;
                    break;

                case 5: // out
                    long out = combo % 8;
                    if (out == o) {
                        long result = solvePart2(program, expectedOutput, A0 << 3 | d, oi - 1);
                        if (result != -1) {
                            return result;
                        }
                    }
                    break prog; // stop at first output
                case 6: // bdv
                    B = A / powerN(2, combo);
                    i += 2;
                    break;

                case 7: // cdv
                    C = A / powerN(2, combo);
                    i += 2;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid op: " + op);
            }
        }
    }

    return -1; // no solution found
}

long powerN(long number, long power){
    long res = 1;
    long sq = number;
    while(power > 0){
        if(power % 2 == 1){
            res *= sq;
        }
        sq = sq * sq;
        power /= 2;
    }
    return res;
}

List<Integer> parseNumbersFromLine(String line) {
    return Arrays.stream(line.split(","))
            .mapToInt(Integer::parseInt)
            .boxed()
            .toList();
}