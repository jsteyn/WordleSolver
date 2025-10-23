import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class AlphabetCombinations {

    static PrintWriter writer;
    // Recursive function to generate combinations
    private static void generateCombinations(String prefix, int length) {
        if (length == 0) {
            writer.println(prefix);
            return;
        }

        for (char c = 'a'; c <= 'z'; c++) {
            generateCombinations(prefix + c, length - 1);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the desired length of combinations: ");
        int length = scanner.nextInt();
        String filename = scanner.next();
        File file = new File(filename);
        try {
            writer = new PrintWriter(file);
            System.out.println("\nAll combinations of letters of length " + length + ":");
            generateCombinations("", length);
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        scanner.close();
    }
}
