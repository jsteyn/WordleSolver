package org.example;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FindLengthFive extends JFrame implements ActionListener {
    private static JPanel panel = new JPanel();

    private static JLabel lbl_includes = new JLabel("Unknown positions");
    private static JLabel lbl_excludes = new JLabel("Not included");
    private static JLabel lbl_positions = new JLabel("Known positions");
    private static JLabel lbl_knownexcludes = new JLabel("Know exclude positions");
    private static JTextField tf_includes = new JTextField();
    private static JTextField tf_excludes = new JTextField();
    private static JTextField[] tf_positions = new JTextField[5];
    private static JTextField[] tf_knownexcludes = new JTextField[5];
    private static JButton search = new JButton("Search");
    private static JButton clear = new JButton("Clear");
    private static JTextArea ta_words = new JTextArea();

    public FindLengthFive() {
        System.out.println("Find Length Five");
        setTitle("Find Length Five");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        getContentPane().add(panel);
        panel.setLayout(new MigLayout("", "[20%][80%]", "[fill][fill]"));

        panel.add(lbl_includes, "cell 0 2,alignx trailing");
        panel.add(tf_includes, "cell 1 2,growx");
        panel.add(lbl_excludes, "cell 0 3,alignx trailing");
        panel.add(tf_excludes, "cell 1 3,growx");
        panel.add(lbl_positions, "cell 0 4,alignx trailing");
        for (int i = 0; i < tf_positions.length; i++) {
            tf_positions[i] = new JTextField(' ');
            panel.add(tf_positions[i], "cell 1 4,growx");
        }
        panel.add(lbl_knownexcludes, "cell 0 5,alignx trailing");
        for (int i = 0; i < tf_knownexcludes.length; i++) {
            tf_knownexcludes[i] = new JTextField(' ');
            panel.add(tf_knownexcludes[i], "cell 1 5,growx");
        }
        panel.add(search);
        panel.add(clear, "wrap");
        search.addActionListener(this);
        clear.addActionListener(this);

// ✅ Put the JTextArea inside a JScrollPane
        JScrollPane scroll = new JScrollPane(ta_words);

// ✅ Span across 2 columns, grow horizontally AND vertically
        panel.add(scroll, "span, grow, push");


        setVisible(true);
    }

    public static void main(String[] args) {
        FindLengthFive findLengthFive = new FindLengthFive();
    }

    private static boolean excludesAny(String word, String excludes) {
        for (char ch : excludes.toCharArray()) {
            if (word.indexOf(ch) >= 0) return false;
        }
        return true;
    }

    private static boolean includesAll(String word, String includes) {
        for (char ch : includes.toCharArray()) {
            if (word.indexOf(ch) < 0) return false;
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Clear")) {
            ta_words.setText("");
            tf_includes.setText("");
            tf_excludes.setText("");
            for (int i = 0; i < tf_positions.length; i++) {
                    tf_positions[i].setText("");
                    tf_knownexcludes[i].setText("");
            }
        }
        if (e.getActionCommand().equals("Search")) {
            String includes = "";
            String excludes = "";
            String[] posexludes = {" ", " ", " ", " ", " "};
            char[] letters = {' ', ' ', ' ', ' ', ' '};
            ta_words.setText("");
            includes = tf_includes.getText();
            excludes = tf_excludes.getText();
            for (int i = 0; i < tf_positions.length; i++) {
                if (!tf_positions[i].getText().isEmpty()) {
                    letters[i] = tf_positions[i].getText().trim().charAt(0);
                }
                if (!tf_knownexcludes[i].getText().isEmpty()) {
                    posexludes[i] = tf_knownexcludes[i].getText();
                }
            }
            System.out.println(includes);
            System.out.println(excludes);
            System.out.println(letters);
            System.out.println(posexludes);
            try {
                System.out.println("Open file");
                Scanner sc = new Scanner(new File("5letterwords.txt"));
                int lines = 0;
                while (sc.hasNextLine()) {
                    String word = sc.nextLine();
                    boolean cont = true;
                    // Check that word is five characters long
                    if (word.length() == 5) {
                        for (int i = 0; i < word.length(); i++) {
                            if (letters[i] != ' ' && letters[i] != word.charAt(i)) {
                                cont = false;
                            }
                        }
                        // check for letters that should be excluded in certain position
                        for (int pos = 0; pos < word.length(); pos++) {
                            for (char ch : posexludes[pos].toCharArray()) {
                                if (word.charAt(pos) == ch){
                                    cont = false;
                                }
                            }
                        }
                        if (cont) {
                            // Check that word excludes excluded letter sand includes included letters
                            if (excludesAny(word, excludes) && (includesAll(word, includes))) {
                                lines++;
                                ta_words.append(word + "\t");
                                if (lines % 10 == 0)
                                    ta_words.append("\n");
                            }
                        }
                    }
                }
                System.out.println("Close file");
                sc.close();
            } catch (FileNotFoundException f) {
                throw new RuntimeException(f);
            }
        }
    }
}
