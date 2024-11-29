import java.io.*;

void main() throws Exception {
    // Load the resource file
    try (InputStream inputStream = getClass().getResourceAsStream("01.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        // Read and print the file line by line
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}