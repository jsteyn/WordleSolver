package org.example;

import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class FindLengthFive extends JFrame implements ActionListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static JPanel leftPanel = new JPanel();
    private static JPanel rightPanel = new JPanel();
    private static JPanel topPanel = new JPanel();
    private static JPanel bottomPanel = new JPanel();
    private static int wordlength = 5;

    private static final JLabel lbl_wordLength = new JLabel("Word length");
    private static final JLabel lbl_excludes = new JLabel("Not included");
    private static final JLabel lbl_positions = new JLabel("Known positions");
    private static final JLabel lbl_knownexcludes = new JLabel("Known exclude positions");
    private static final JTextField tf_wordlength = new JTextField("5");
    private static final JTextField tf_excludes = new JTextField();
    private static JTextField[] tf_positions;
    private static JTextField[] tf_knownexcludes;
    private static final JButton btn_search = new JButton("Search");
    private static final JButton btn_clear = new JButton("Clear");
    private static final JTextArea ta_words = new JTextArea();
    private static final JTextField tf_newwords = new JTextField();
    private static final JButton btn_add = new JButton("Add");
    private static File wordFile = new File("5letterwords.txt");
    private static final JButton btn_update = new JButton("Update");

    public static void makeLeftPanel(int wordlength) {
        leftPanel.setLayout(new MigLayout("", "", ""));
        leftPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        leftPanel.add(lbl_wordLength, "");
        leftPanel.add(tf_wordlength, "");
        leftPanel.add(btn_update, "wrap");
        leftPanel.add(lbl_excludes, "");
        leftPanel.add(tf_excludes, "grow, wrap");
        leftPanel.add(lbl_positions, "cell 0 3,alignx trailing");
        tf_positions = new JTextField[wordlength];
        tf_knownexcludes = new JTextField[wordlength];
        for (int i = 0; i < wordlength; i++) {
            tf_positions[i] = new JTextField(' ');
            leftPanel.add(tf_positions[i], "cell " + (i + 1) + " 3,growx");
        }
        leftPanel.add(lbl_knownexcludes, "cell 0 4,alignx trailing");
        for (int i = 0; i < wordlength; i++) {
            tf_knownexcludes[i] = new JTextField(' ');
            leftPanel.add(tf_knownexcludes[i], "cell " + (i + 1) + " 4,growx");
        }
        leftPanel.add(new JLabel(), "wrap");
        leftPanel.add(btn_search);
        leftPanel.add(btn_clear, "wrap");
    }

    public FindLengthFive() {
        setTitle("Wordle Solver");
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
        btn_update.addActionListener(this);

// ✅ Put the JTextArea inside a JScrollPane
        JScrollPane scroll = new JScrollPane(ta_words);

// ✅ Span across 2 columns, grow horizontally AND vertically



        rightPanel.add(tf_newwords, "wrap");
        rightPanel.add(btn_add, "wrap");

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
        for (int i = 0; i < wordlength; i++) {
            tf_positions[i].setText("");
            tf_knownexcludes[i].setText("");
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
        wordlength = Integer.valueOf(tf_wordlength.getText());
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
            System.out.println("Open file: " + wordFile);
            Scanner sc = new Scanner(wordFile);
            int lines = 0;
            while (sc.hasNextLine()) {
                String word = sc.nextLine();
                boolean cont = true;
                // Check that word is 'wordlength' characters long
                logger.info("Word length: {}", wordlength);
                logger.info(word.length() + " " + word);
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
            case "Update": {
                wordlength = Integer.parseInt(tf_wordlength.getText());
                logger.info("Word length: " + wordlength);
                leftPanel.removeAll();
                makeLeftPanel(wordlength);
                SwingUtilities.updateComponentTreeUI(leftPanel);
            }
            case "Open": {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File("."));
                FileNameExtensionFilter extFilter = new FileNameExtensionFilter("Text file", "txt", "TXT");
                fc.addChoosableFileFilter(extFilter);
                int result = fc.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    wordFile = fc.getSelectedFile();
                }
            }
        }

    }
}
