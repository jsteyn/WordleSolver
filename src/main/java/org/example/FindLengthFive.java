package org.example;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class FindLengthFive extends JFrame {
    private static JPanel leftPanel = new JPanel();
    private static JPanel rightPanel = new JPanel();
    private static JPanel topPanel = new JPanel();
    private static JPanel bottomPanel = new JPanel();

    private static final JLabel lbl_excludes = new JLabel("Not included");
    private static final JLabel lbl_positions = new JLabel("Known positions");
    private static final JLabel lbl_knownexcludes = new JLabel("Know exclude positions");
    private static final JTextField tf_excludes = new JTextField();
    private static final JTextField[] tf_positions = new JTextField[5];
    private static final JTextField[] tf_knownexcludes = new JTextField[5];
    private static final JButton search = new JButton("Search");
    private static final JButton clear = new JButton("Clear");
    private static final JTextArea ta_words = new JTextArea();
    private static final JTextField tf_newwords = new JTextField();
    private static final JButton add = new JButton("Add");
    private static final File wordFile = new File("5letterwords.txt");

    public FindLengthFive() {
        System.out.println("Find Length Five");
        setTitle("Find Length Five");
        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 768);
        getContentPane().add(leftPanel);

        setLayout(new MigLayout("", "", ""));
        leftPanel.setLayout(new MigLayout("", "", "[fill][fill]"));
        rightPanel.setLayout(new MigLayout("", "[fill]"));
        topPanel.setLayout(new MigLayout("", "[fill]"));
        bottomPanel.setLayout(new MigLayout("", "[fill]"));

        leftPanel.add(lbl_excludes, "cell 0 3,alignx trailing");
        leftPanel.add(tf_excludes, "cell 1 3,growx");
        leftPanel.add(lbl_positions, "cell 0 4,alignx trailing");
        for (int i = 0; i < tf_positions.length; i++) {
            tf_positions[i] = new JTextField(' ');
            leftPanel.add(tf_positions[i], "cell 1 4,growx");
        }
        leftPanel.add(lbl_knownexcludes, "cell 0 5,alignx trailing");
        for (int i = 0; i < tf_knownexcludes.length; i++) {
            tf_knownexcludes[i] = new JTextField(' ');
            leftPanel.add(tf_knownexcludes[i], "cell 1 5,growx");
        }


        search.addActionListener(searchAction);
        clear.addActionListener(clearAction);
        add.addActionListener(addWord);

// ✅ Put the JTextArea inside a JScrollPane
        JScrollPane scroll = new JScrollPane(ta_words);

// ✅ Span across 2 columns, grow horizontally AND vertically

        leftPanel.add(new JLabel(), "wrap");
        leftPanel.add(search);
        leftPanel.add(clear, "wrap");

        rightPanel.add(tf_newwords, "wrap");
        rightPanel.add(add, "wrap");

        topPanel.add(leftPanel, "cell 0 0,grow");
        topPanel.add(rightPanel, "cell 1 0,grow");
        bottomPanel.add(scroll, "span, grow, push");
        add(topPanel, "wrap");
        add(bottomPanel, "span, grow, push");
        // Bind it at the window level so it's always active
        JRootPane rootPane = this.getRootPane();

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlX, "triggerClear");
        rootPane.getActionMap().put("triggerClear", clearAction);

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlS, "triggerSearch");
        rootPane.getActionMap().put("triggerSearch", searchAction);

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

    Action searchAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Search button triggered!");
            doSearch();
        }
    };

    Action clearAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Clear button triggered!");
            doClear();
        }
    };

    Action addWord = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                PrintWriter pw = new PrintWriter(new FileOutputStream(
                        wordFile,
                        true /* append = true */));
                pw.println(tf_newwords.getText());
                SortContentsByLinesInFile.sortFile(wordFile.getName());
                pw.close();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    };

    private void doClear() {
        ta_words.setText("");
        tf_excludes.setText("");
        for (int i = 0; i < tf_positions.length; i++) {
            tf_positions[i].setText("");
            tf_knownexcludes[i].setText("");
        }
    }

    private void doSearch() {
        String includes = "";
        String excludes = "";
        String[] posexludes = {" ", " ", " ", " ", " "};
        char[] letters = {' ', ' ', ' ', ' ', ' '};
        ta_words.setText("");
        excludes = tf_excludes.getText();
        for (int i = 0; i < tf_positions.length; i++) {
            if (!tf_positions[i].getText().isEmpty()) {
                letters[i] = tf_positions[i].getText().trim().charAt(0);
            }
            if (!tf_knownexcludes[i].getText().isEmpty()) {
                posexludes[i] = tf_knownexcludes[i].getText();
                if (!includes.contains(posexludes[i])) {
                    includes += posexludes[i];
                }
            }
        }
        System.out.println(includes);
        System.out.println(excludes);
        System.out.println(letters);
        System.out.println(Arrays.toString(posexludes));
        try {
            System.out.println("Open file");
            Scanner sc = new Scanner(wordFile);
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
                            if (word.charAt(pos) == ch) {
                                cont = false;
                            }
                        }
                    }
                    if (cont) {
                        // Check that word excludes the excluded letters and includes the included letters
                        if (excludesAny(word, excludes) && (includesAll(word, includes))) {
                            lines++;
                            ta_words.append(word + "\t");
                            if (lines % 5 == 0)
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
