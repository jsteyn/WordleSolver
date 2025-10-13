package org.example;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class FindLengthFive extends JFrame implements ActionListener {
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
        setTitle("Find Length Five");
        KeyStroke ctrlD = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlB = KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK);


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


        search.addActionListener(this);
        clear.addActionListener(this);
        add.addActionListener(this);

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
        if (!tf_newwords.getText().isEmpty()) {
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
        } else System.out.println("Add field empty");
    }

    private void doClear() {
        ta_words.setText("");
        tf_excludes.setText("");
        for (int i = 0; i < tf_positions.length; i++) {
            tf_positions[i].setText("");
            tf_knownexcludes[i].setText("");
        }
    }

    private void doSearch() {
        StringBuilder includes = new StringBuilder();
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
                if (!includes.toString().contains(posexludes[i])) {
                    includes.append(posexludes[i]);
                }
            }
        }

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
            System.out.println("Close file");
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
        }

    }
}
