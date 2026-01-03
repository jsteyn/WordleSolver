package org.example;

import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.nio.file.*;

public class FindLengthFive extends JFrame implements ActionListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final JPanel leftPanel = new JPanel();
    private static final JPanel rightPanel = new JPanel();
    private static final JPanel topPanel = new JPanel();
    private static final JPanel bottomPanel = new JPanel();
    private static int wordlength = 5;

    private static final JLabel lbl_wordLength = new JLabel("Word length");
    private static final JLabel lbl_excludes = new JLabel("Not included");
    private static final JLabel lbl_positions = new JLabel("Known positions");
    private static final JLabel lbl_knownExcludes = new JLabel("Known exclude positions");
    private static final JTextField tf_excludes = new JTextField();
    private static JTextField[] tf_positions;
    private static JTextField[] tf_knownExcludes;
    private static final JButton btn_search = new JButton("Search");
    private static final JButton btn_clear = new JButton("Clear");
    private static final JTextArea ta_words = new JTextArea();
    private static final JTextField tf_newWords = new JTextField();
    private static final JButton btn_add = new JButton("Add");
    private static final JButton btn_find = new JButton("Find");
    private static File wordFile = new File("en_5.wrdl");
//    private static final JComboBox<Integer> cb_wordLength = new JComboBox<Integer>(new Integer[]{5, 6});
    private static JComboBox<String> cb_wordLength = new JComboBox<>(findWrdlFiles().toArray(new String[0]));;

    public static void makeLeftPanel(int wordLength) {

        leftPanel.setLayout(new MigLayout("", "", ""));
        leftPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        leftPanel.add(lbl_wordLength, "");
        leftPanel.add(cb_wordLength);
        leftPanel.add(lbl_excludes, "");
        leftPanel.add(tf_excludes, "grow, wrap");
        leftPanel.add(lbl_positions, "cell 0 3,alignx trailing");
        tf_positions = new JTextField[wordLength];
        tf_knownExcludes = new JTextField[wordLength];
        for (int i = 0; i < wordLength; i++) {
            tf_positions[i] = new JTextField(' ');
            leftPanel.add(tf_positions[i], "cell " + (i + 1) + " 3,growx");
        }
        leftPanel.add(lbl_knownExcludes, "cell 0 4,alignx trailing");
        for (int i = 0; i < wordLength; i++) {
            tf_knownExcludes[i] = new JTextField(' ');
            leftPanel.add(tf_knownExcludes[i], "cell " + (i + 1) + " 4,growx");
        }
        leftPanel.add(new JLabel(), "wrap");
        leftPanel.add(btn_search);
        leftPanel.add(btn_clear, "wrap");
    }


    public static ArrayList<String> findWrdlFiles() {
        ArrayList<String> filenames = new ArrayList<>();

        Path dir = Paths.get("."); // change this to another directory if needed

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.wrdl")) {
            for (Path entry : stream) {
                filenames.add(entry.toString().substring(2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filenames;
    }


    public FindLengthFive() {
        setTitle("Wordle Solver: " + "File: " + wordFile + "\t Length: " + wordlength);
        KeyStroke ctrlD = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlB = KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 800);

        setLayout(new MigLayout("", "[fill]", ""));
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open");
        open.addActionListener(this);
        mb.add(file);
        file.add(open);
        setJMenuBar(mb);

        makeLeftPanel(wordlength);
        getContentPane().add(leftPanel);
        rightPanel.setLayout(new MigLayout("", "[fill]"));
        topPanel.setLayout(new MigLayout("", "[fill]"));
        bottomPanel.setLayout(new MigLayout("", "[fill]"));


        btn_search.addActionListener(this);
        btn_clear.addActionListener(this);
        btn_add.addActionListener(this);
        btn_find.addActionListener(this);
        cb_wordLength.addActionListener(this);

// ✅ Put the JTextArea inside a JScrollPane
        JScrollPane scroll = new JScrollPane(ta_words);

// ✅ Span across 2 columns, grow horizontally AND vertically



        rightPanel.add(tf_newWords, "wrap");
        rightPanel.add(btn_add, "wrap");
        rightPanel.add(btn_find, "wrap");

        topPanel.add(leftPanel, "cell 0 0,grow");
        topPanel.add(rightPanel, "cell 1 0,grow");
        bottomPanel.add(scroll, "span, grow, push");
        add(topPanel, "wrap");
        add(bottomPanel, "span, grow, push");

        // Get the root pane input map (when the window is focused)
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(ctrlD, "triggerClear");
        actionMap.put("triggerClear", clearAction);

        inputMap.put(ctrlS, "triggerSearch");
        actionMap.put("triggerSearch", searchAction);

        inputMap.put(ctrlB, "cursorBegin");
        actionMap.put("cursorBegin", focusExcludes);

        setVisible(true);
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
            System.out.println("Search: " + e.getActionCommand());
            doSearch();
        }
    };

    Action clearAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Clear: " + e.getActionCommand());
            doClear();
        }
    };

    Action focusExcludes = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            tf_excludes.requestFocusInWindow();
            tf_excludes.setCaretPosition(tf_excludes.getText().length()); // place cursor at end
            System.out.println("Focus moved to tf_excludes");
        }
    };
    private void doAdd() {
        if (!tf_newWords.getText().isEmpty()) {
            try {

                PrintWriter pw = new PrintWriter(new FileOutputStream(
                        wordFile,
                        true /* append = true */));
                pw.println(tf_newWords.getText());
                logger.info("Read file {}", wordFile);
                pw.close();
                SortContentsByLinesInFile.sortFile(wordFile.getName(), wordlength);

            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        } else System.out.println("Add field empty");
    }

    private void doClear() {
        ta_words.setText("");
        tf_excludes.setText("");
        for (int i = 0; i < wordlength; i++) {
            tf_positions[i].setText("");
            tf_knownExcludes[i].setText("");
        }
    }

    /**
     * Checks if 'word' contains all the letters in 'letters'.
     * Comparison is case-insensitive, and letter frequency matters.
     *
     * Examples:
     *   containsAllLetters("apple", "pal") → true
     *   containsAllLetters("apple", "ppp") → false
     *   containsAllLetters("banana", "nab") → true
     */
    public static boolean containsAllLetters(String word, String letters) {
        if (word == null || letters == null) return false;

        // Normalize to lowercase
        word = word.toLowerCase();
        letters = letters.toLowerCase();

        // Count the frequency of each letter in the word
        int[] wordCount = new int[26];
        for (char c : word.toCharArray()) {
            if (Character.isLetter(c)) {
                System.out.println(word);
                wordCount[c - 'a']++;
            }
        }

        // Check if the word has enough of each required letter
        for (char c : letters.toCharArray()) {
            if (Character.isLetter(c)) {
                int index = c - 'a';
                wordCount[index]--;
                if (wordCount[index] < 0) {
                    return false; // not enough of this letter
                }
            }
        }

        return true;
    }

    private void doFind() {
        String letters = tf_newWords.getText();
        try {
            Scanner sc = new Scanner(wordFile);
            logger.info("Open {}", wordFile);
            ta_words.setText("");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (containsAllLetters(line, letters))
                    ta_words.append(line + "\n");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void doSearch() {
        StringBuilder includes = new StringBuilder();
        String excludes = "";
        // initialise variables for letters in word
        String[] posexludes = new String[wordlength];
        char[] letters = new char[wordlength];
        for (int i = 0; i < wordlength; i++) {
            posexludes[i] = " ";
            letters[i] = ' ';
        }
        ta_words.setText("");
        //wordFile = new File(tf_wordfile.getText());
        String filename = (String)cb_wordLength.getSelectedItem();
        wordlength = Integer.parseInt(filename.substring(3, filename.indexOf(".")));
        excludes = tf_excludes.getText();
        for (int i = 0; i < tf_positions.length; i++) {
            if (!tf_positions[i].getText().isEmpty()) {
                letters[i] = tf_positions[i].getText().trim().charAt(0);
            }
            if (!tf_knownExcludes[i].getText().isEmpty()) {
                posexludes[i] = tf_knownExcludes[i].getText();
                if (!includes.toString().contains(posexludes[i])) {
                    includes.append(posexludes[i]);
                }
            }
        }

        try {
            Scanner sc = new Scanner(wordFile);
            int lines = 0;
            while (sc.hasNextLine()) {
                String word = sc.nextLine();
                boolean cont = true;
                // Check that word is 'wordlength' characters long
                if (word.length() == wordlength) {
                    for (int i = 0; i < word.length(); i++) {
                        if (letters[i] != ' ' && letters[i] != word.charAt(i)) {
                            cont = false;
                            break;
                        }
                    }
                    // check for letters that should be excluded in certain position
                    for (int pos = 0; pos < word.length(); pos++) {
                        for (char ch : posexludes[pos].toCharArray()) {
                            if (word.charAt(pos) == ch) {
                                cont = false;
                                break;
                            }
                        }
                    }
                    if (cont) {
                        // Check that word excludes the excluded letters and includes the included letters
                        if (excludesAny(word, excludes) && (includesAll(word, includes.toString()))) {
                            lines++;
                            ta_words.append(word + "\t");
                            if (lines % 5 == 0)
                                ta_words.append("\n");
                        }
                    }
                }
            }
            sc.close();
        } catch (FileNotFoundException f) {
            throw new RuntimeException(f);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Action: " + e.getActionCommand());
        switch (e.getActionCommand()) {
            case "Search": {
                doSearch();
                break;
            }
            case "Clear": {
                doClear();
                break;
            }
            case "Add": {
                doAdd();
                break;
            }
            case "comboBoxChanged": {
                int index = cb_wordLength.getSelectedIndex();
                String filename = (String) cb_wordLength.getSelectedItem();
                cb_wordLength.setSelectedIndex(index);
                wordlength = Integer.parseInt((filename).substring(3, filename.indexOf(".")));
                logger.info("Word length: " + wordlength);
                leftPanel.removeAll();
                makeLeftPanel(wordlength);
                SwingUtilities.updateComponentTreeUI(leftPanel);
                wordFile = new File(filename);
                setTitle("Wordle Solver: " + "File: " + wordFile + "\t Length: " + wordlength);
                doSearch();
                break;
            }
//            case "Open": {
//                JFileChooser fc = new JFileChooser();
//                fc.setCurrentDirectory(new File("."));
//                FileNameExtensionFilter extFilter = new FileNameExtensionFilter("Text file", "txt", "TXT");
//                fc.addChoosableFileFilter(extFilter);
//                int result = fc.showOpenDialog(null);
//                if (result == JFileChooser.APPROVE_OPTION) {
//                    wordFile = fc.getSelectedFile();
//                    setTitle("Wordle Solver: " + wordFile);
//                }
//            }
            case "Find": {
                doFind();
            }
        }

    }
}
