import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class generate {

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
        System.out.print("Enter the starting letter (a-z): ");
        char startLetter = scanner.next().toLowerCase().charAt(0);

        if (startLetter < 'a' || startLetter > 'z') {
            System.out.println("Invalid starting letter. Please enter a letter between a and z.");
            scanner.close();
            return;
        }
        System.out.print("Enter the desired length of combinations: ");
        int length = scanner.nextInt();
        if (length < 1) {
            System.out.println("Length must be at least 1.");
            scanner.close();
            return;
        }

        System.out.println("\nAll combinations of letters of length " + length +
                " starting with '" + startLetter + "':");


        System.out.print("Filename to write to: ");
        String filename = scanner.next();
        File file = new File(filename);

        try {
            writer = new PrintWriter(file);
            System.out.println("\nAll combinations of letters of length " + length + ":");
            generateCombinations(String.valueOf(startLetter), length - 1);
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        scanner.close();
    }
}
