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

    public static void main(String[] args) {
        sortFile("5letterwords.txt");
    }

    public static void sortFile(String filename) {

        String fileName = filename;


        try {
            //1) Read file in String, and store each String in ArrayList
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);

            List<String> l=new ArrayList<String>();
            String str;

            while((str = br.readLine()) != null){
                l.add(str);
            }
            br.close();

            //By now, String fileDataInString contains all the data of file


            //2) Sort ArrayList
            Collections.sort(l);

            System.out.print("Display sorted list > ");
            //Display sorted list
            System.out.println(l);

            //3) Now, write sorted content in file
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String s: l){
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
/*OUTPUT
 
Display sorted list > [io from , javaMadeSoEasy a, learning file , you are ]
 
Sorted content has been written in file
 
*/