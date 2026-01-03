package tests;

import org.example.FindLengthFive;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FindLengthFiveTest {

    @Test
    void findWrdlFiles() {
        ArrayList<String> filenames = new ArrayList<>();
        filenames = FindLengthFive.findWrdlFiles();
        assertEquals(3, filenames.size());
        for (String filename : filenames) {
            System.out.println(filename);
        }
    }
}