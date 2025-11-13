package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Harvest {

    static String inputFilename = "I:\\\\BOOKS\\ASCII\\Afrikaans\\Dinsdagaand_trimmed.txt";
    static String outputFilename = "";
    static int wordLength = 5;

    /**
     * Removes all punctuation characters from the start and end of a word.
     * Keeps internal punctuation (like apostrophes or hyphens inside a word).
     * Returns null if the word contains any characters other than A–Z or a–z.
     */
    public static String trimPunctuation(String word) {
        if (word == null) return null;

        word = word.toLowerCase();
        // Trim punctuation from the start and end
        word = word.replaceAll("^[\\p{Punct}]+|[\\p{Punct}]+$", "");

        // If word contains any non a–z letters, return null
        if (!word.matches("^[a-zA-Z]+$")) {
            return null;
        }

        return word;
    }


    Harvest() {

        try {
            Scanner sc = new Scanner(new File(inputFilename));
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split(" ");
                for (String token : tokens) {
                    token = trimPunctuation(token);
                    if (token.length() == wordLength) {
                        System.out.println( token);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        Harvest harvest = new Harvest();
    }


}
