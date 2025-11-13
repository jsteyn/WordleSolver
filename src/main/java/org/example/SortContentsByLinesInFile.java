package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SortContentsByLinesInFile {
    private static final Logger logger = LoggerFactory.getLogger(SortContentsByLinesInFile.class);

    static int wl = 5;
    static String fn = "5letterwoorde.txt";

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

    public static void sortFile(String filename, int wordlength) {

        Path inputPath = Paths.get(filename);
        if (!Files.exists(inputPath)) {
            System.err.println("File not found: " + filename);
            return;
        }

        // Use a TreeSet to sort and deduplicate automatically
        // If you want case-insensitive deduplication/sorting, use String.CASE_INSENSITIVE_ORDER
        Set<String> uniqueSorted = new TreeSet<>();

        try (BufferedReader br = Files.newBufferedReader(inputPath)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase();
                line = trimPunctuation(line);
                String cleaned = normaliseLine(line);

                if (cleaned.length() == wordlength && !cleaned.contains(".")) {
                    uniqueSorted.add(cleaned);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Debug: print how many unique entries we have
        System.out.println("Unique entries: " + uniqueSorted.size());

        // Write back to the same file (overwrite) — or change file name to keep original
        try (BufferedWriter bw = Files.newBufferedWriter(inputPath)) {
            for (String s : uniqueSorted) {
                bw.write(s);
                bw.newLine();
            }
            logger.info("File written: {}", inputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Sorted and deduplicated content written to: " + filename);
    }

    /**
     * Normalises a line by removing BOM, CRs, trimming, and stripping control characters.
     */

    private static String normaliseLine(String s) {
        if (s == null) return "";
        // Remove BOM (Byte Order Mark) if present
        if (s.startsWith("\uFEFF")) {
            s = s.substring(1);
        }
        // Remove carriage returns and trim
        s = s.replace("\r", "").trim();

        // Remove remaining control characters (category Cc) except newline (shouldn't be present anyway)
        // Keep printable characters and spaces. Replace control characters with empty string.
        s = s.replaceAll("\\p{Cntrl}", "");

        // Optionally, if you'd like case-insensitive dedupe, uncomment next line:
        // s = s.toLowerCase(Locale.ROOT);

        return s;
    }

    public static void main(String[] args) {

    }
}
