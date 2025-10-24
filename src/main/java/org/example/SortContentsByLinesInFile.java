package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortContentsByLinesInFile {

    static int wl = 5;
    static String fn = "5letterwoorde.txt";

    public static void main(String[] args) {
        sortFile(fn, wl);
    }

    public static void sortFile(String filename, int wordlength) {

        try {
            //1) Read file in String, and store each String in ArrayList
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            List<String> wordlist=new ArrayList<String>();
            String str;

            while((str = br.readLine()) != null){
                if (str.length()==wordlength && !str.contains("."))
                    wordlist.add(str);
            }
            br.close();

            //By now, String fileDataInString contains all the data of file


            //2) Sort ArrayList
            Collections.sort(wordlist);

            System.out.print("Display sorted list > ");
            //Display sorted list
            System.out.println(wordlist);

            //3) Now, write sorted content in file
            FileWriter fw = new FileWriter(filename);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String s: wordlist){
                bw.write(s);
                bw.write("\n");
            }

            bw.close();
            System.out.println("\nSorted content has been written in file");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
