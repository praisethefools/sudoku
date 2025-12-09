import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * Sudoku.java - single-file final
 * Features:
 * - Generator (random full-solution + remove by difficulty)
 * - Count-up timer (javax.swing.Timer)
 * - Full Helper hint system (Level 4) with Auto Apply
 * - Win detection (auto-stop timer, lock board)
 * - Save / Load progress to "savegame.dat"
 *
 * Usage: new Sudoku("easy"|"medium"|"hard")
 */
public class Sudoku {

    // Inner Tile class (button that represents a cell)
    class Tile extends JButton {
        int r, c;
        Tile(int r, int c) {
            super("");
            this.r = r;
            this.c = c;
            setMargin(new Insets(0,0,0,0));
            setFocusPainted(false);
        }
    }

    // UI & board
    private final int boardWidth = 600, boardHeight = 650;
    private String[] puzzle;      // current puzzle with '-' for empty
    private String[] solution;    // string rows of solution digits
    private String difficulty;    // current difficulty

    private final JFrame frame = new JFrame("Sudoku");
    private final JLabel statusLabel = new JLabel();
    private final JPanel boardPanel = new JPanel();
    private final JPanel controlPanel = new JPanel();

    private Tile[][] tiles = new Tile[9][9];
    private JButton[] numButtons = new JButton[9];

    // Timer (count-up)
    private javax.swing.Timer gameTimer;  // use javax.swing.Timer explicitly
    private int elapsedSeconds = 0;
    private final JLabel timerLabel = new JLabel("Time: 00:00");

    // Hint system
    private int lastHintRow = -1;
    private int lastHintCol = -1;
    private int lastHintValue = -1;
    private int hintUsage = 0;
    private final int MAX_HINTS = 5;

    // Error tracking
    private int errors = 0;

    // Save/load filename
    private final String SAVE_FILE = "savegame.dat";

    // Constructor
    public Sudoku(String difficulty) {
        this.difficulty = (difficulty == null) ? "medium" : difficulty.toLowerCase();

        // generate puzzle + solution
        GeneratedSudoku gs = SudokuGenerator.generate(this.difficulty);
        this.puzzle = gs.puzzle;
        this.solution = gs.solution;

        buildUI();
        startTimer();
    }

    // ---------- UI ----------
    private void buildUI() {
        frame.setSize(boardWidth, boardHeight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setText("Difficulty: " + difficulty.toUpperCase() + "    Errors: 0");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        timerLabel.setHorizontalAlignment(JLabel.RIGHT);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(statusLabel, BorderLayout.WEST);
        topPanel.add(timerLabel, BorderLayout.EAST);
        frame.add(topPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(9,9));
        setupBoard();
        frame.add(boardPanel, BorderLayout.CENTER);

        controlPanel.setLayout(new GridLayout(2,5,6,6));
        for (int i = 0; i < 9; i++) {
            final int val = i+1;
            JButton b = new JButton(String.valueOf(val));
            b.setFont(new Font("Arial", Font.BOLD, 18));
            b.addActionListener(e -> selectNumber(val));
            numButtons[i] = b;
            controlPanel.add(b);
        }

        JButton hintBtn = new JButton("Full Hint");
        hintBtn.setFont(new Font("Arial", Font.BOLD, 14));
        hintBtn.addActionListener(e -> giveFullHelperHint());
        controlPanel.add(hintBtn);

        JButton applyHintBtn = new JButton("Auto Apply Hint");
        applyHintBtn.setFont(new Font("Arial", Font.BOLD, 14));
        applyHintBtn.addActionListener(e -> applyHint());
        controlPanel.add(applyHintBtn);

        JButton saveBtn = new JButton("Save Game");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.addActionListener(e -> saveGame());
        controlPanel.add(saveBtn);

        JButton loadBtn = new JButton("Load Game");
        loadBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loadBtn.addActionListener(e -> {
            loadGame();
        });
        controlPanel.add(loadBtn);

        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Setup board tiles
    private void setupBoard() {
        boardPanel.removeAll();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Tile t = new Tile(r,c);
                char ch = puzzle[r].charAt(c);
                if (ch != '-') {
                    t.setText(String.valueOf(ch));
                    t.setEnabled(false);
                    t.setBackground(Color.LIGHT_GRAY);
                    t.setFont(new Font("Arial", Font.BOLD, 20));
                } else {
                    t.setText("");
                    t.setEnabled(true);
                    t.setBackground(Color.WHITE);
                    t.setFont(new Font("Arial", Font.PLAIN, 20));
                }

                int top = (r % 3 == 0) ? 3 : 1;
                int left = (c % 3 == 0) ? 3 : 1;
                int bottom = (r == 8) ? 3 : 1;
                int right = (c == 8) ? 3 : 1;
                t.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));

                t.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Tile src = (Tile) e.getSource();
                        JButton selectedNum = getSelectedNumberButton();
                        if (selectedNum != null) {
                            String chosen = selectedNum.getText();
                            if (!src.getText().isEmpty()) return; // already filled

                            String correct = String.valueOf(solution[src.r].charAt(src.c));
                            if (chosen.equals(correct)) {
                                src.setText(chosen);
                                src.setForeground(new Color(10,90,160));
                                src.setEnabled(false);
                                src.setBackground(new Color(220,255,240));
                                checkWinAndStopIfDone();
                            } else {
                                errors++;
                                statusLabel.setText("Difficulty: " + difficulty.toUpperCase() + "    Errors: " + errors);
                                Color prev = src.getBackground();
                                src.setBackground(new Color(255,200,200));
                                javax.swing.Timer flash = new javax.swing.Timer(220, ev -> src.setBackground(prev));
                                flash.setRepeats(false);
                                flash.start();
                            }
                        } else {
                            // no number selected -> show possible numbers
                            showPossibleNumbersForTile(src.r, src.c);
                        }
                    }
                });

                tiles[r][c] = t;
                boardPanel.add(t);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    // Return selected number button or null
    private JButton getSelectedNumberButton() {
        for (JButton b : numButtons) {
            if (b != null && Color.LIGHT_GRAY.equals(b.getBackground())) return b;
        }
        return null;
    }

    // Selecting a number toggles highlight
    private void selectNumber(int val) {
        for (JButton b : numButtons) {
            if (b.getText().equals(String.valueOf(val))) {
                if (Color.LIGHT_GRAY.equals(b.getBackground())) {
                    b.setBackground(null);
                } else {
                    b.setBackground(Color.LIGHT_GRAY);
                }
            } else {
                b.setBackground(null);
            }
        }
    }

    // Show possible numbers for a tile based on current board state
    private void showPossibleNumbersForTile(int r, int c) {
        if (!tiles[r][c].isEnabled()) return;
        ArrayList<Integer> possibles = new ArrayList<>();
        for (int n = 1; n <= 9; n++) {
            if (isValidPlacementByCurrentBoard(r, c, n)) possibles.add(n);
        }
        if (possibles.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No valid numbers (board may be inconsistent).", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Possible numbers for (" + (r+1) + "," + (c+1) + "): " + possibles, "Hints", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Check validity against current board entries
    private boolean isValidPlacementByCurrentBoard(int r, int c, int val) {
        for (int i = 0; i < 9; i++) {
            String tr = tiles[r][i].getText();
            if (!tr.isEmpty() && Integer.parseInt(tr) == val) return false;
            String tc = tiles[i][c].getText();
            if (!tc.isEmpty() && Integer.parseInt(tc) == val) return false;
        }
        int br = (r/3)*3, bc = (c/3)*3;
        for (int i = br; i < br+3; i++)
            for (int j = bc; j < bc+3; j++) {
                String t = tiles[i][j].getText();
                if (!t.isEmpty() && Integer.parseInt(t) == val) return false;
            }
        return true;
    }

    // ---------- Timer ----------
    private void startTimer() {
        elapsedSeconds = 0;
        timerLabel.setText("Time: 00:00");
        gameTimer = new javax.swing.Timer(1000, new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                elapsedSeconds++;
                int mm = elapsedSeconds / 60;
                int ss = elapsedSeconds % 60;
                timerLabel.setText(String.format("Time: %02d:%02d", mm, ss));
            }
        });
        gameTimer.start();
    }
    private void stopTimer() {
        if (gameTimer != null) gameTimer.stop();
    }

    // ---------- Full Helper (Level 4) ----------
    // Finds solved board from current state and returns first empty cell as hint
    private void giveFullHelperHint() {
        if (hintUsage >= MAX_HINTS) {
            JOptionPane.showMessageDialog(frame, "Max hints used (" + MAX_HINTS + ").", "Hint", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int[][] current = new int[9][9];
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++) {
                String t = tiles[r][c].getText();
                current[r][c] = (t == null || t.isEmpty()) ? 0 : Integer.parseInt(t);
            }

        int[][] solved = SudokuSolver.solve(current);
        if (solved == null) {
            JOptionPane.showMessageDialog(frame, "Board has no valid solution (inconsistent).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // find first empty tile and provide rich explanation
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (tiles[r][c].getText().isEmpty()) {
                    int correct = solved[r][c];

                    String message =
                        "FULL HELPER HINT\n\n" +
                        "Position: Row " + (r+1) + ", Column " + (c+1) + "\n" +
                        "Suggested value: " + correct + "\n\n" +
                        "Reasoning (brief):\n" +
                        "- The solver computed a consistent full solution from the current board state.\n" +
                        "- Given current row/column/box entries, only number " + correct + " fits this cell without conflict.\n\n" +
                        "Options:\n" +
                        "1) Press 'Auto Apply Hint' to fill this cell automatically.\n" +
                        "2) Manually place the number to practice solving.\n\n" +
                        "Hints used: " + (hintUsage+1) + " / " + MAX_HINTS;

                    JOptionPane.showMessageDialog(frame, message, "Full Helper", JOptionPane.INFORMATION_MESSAGE);

                    lastHintRow = r;
                    lastHintCol = c;
                    lastHintValue = correct;
                    hintUsage++;
                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(frame, "No empty cells found. Puzzle may already be solved.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // Apply last hint (auto fill)
    private void applyHint() {
        if (lastHintRow == -1) {
            JOptionPane.showMessageDialog(frame, "No hint available. Use 'Full Hint' first.", "Hint", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        tiles[lastHintRow][lastHintCol].setText(String.valueOf(lastHintValue));
        tiles[lastHintRow][lastHintCol].setEnabled(false);
        tiles[lastHintRow][lastHintCol].setBackground(new Color(220,255,240));
        checkWinAndStopIfDone();

        lastHintRow = lastHintCol = lastHintValue = -1;
    }

    // ---------- Win detection ----------
    private void checkWinAndStopIfDone() {
        if (isSolved()) {
            stopTimer();
            JOptionPane.showMessageDialog(frame,
                "Congratulations — you solved the puzzle!\nTime: " +
                String.format("%02d:%02d", elapsedSeconds/60, elapsedSeconds%60) +
                "\nErrors: " + errors, "Solved", JOptionPane.INFORMATION_MESSAGE);

            // lock board
            for (int r = 0; r < 9; r++)
                for (int c = 0; c < 9; c++)
                    tiles[r][c].setEnabled(false);
        }
    }

    private boolean isSolved() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                String t = tiles[r][c].getText();
                if (t == null || t.isEmpty()) return false;
                if (!t.equals(String.valueOf(solution[r].charAt(c)))) return false;
            }
        }
        return true;
    }

    // ---------- Save / Load ----------
    private void saveGame() {
        try {
            PrintWriter pw = new PrintWriter(new File(SAVE_FILE));

            // difficulty
            pw.println(difficulty);
            // elapsed seconds
            pw.println(elapsedSeconds);

            // puzzle original rows
            for (int r = 0; r < 9; r++) {
                pw.println(puzzle[r]);
            }

            // current board state 9 lines of 9 chars (use '-' for empty)
            for (int r = 0; r < 9; r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < 9; c++) {
                    String t = tiles[r][c].getText();
                    sb.append((t == null || t.isEmpty()) ? '-' : t.charAt(0));
                }
                pw.println(sb.toString());
            }

            // solution rows
            for (int r = 0; r < 9; r++) pw.println(solution[r]);

            pw.close();
            JOptionPane.showMessageDialog(frame, "Game saved to " + SAVE_FILE, "Saved", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error saving game: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadGame() {
        try {
            File f = new File(SAVE_FILE);
            if (!f.exists()) {
                JOptionPane.showMessageDialog(frame, "No save file found.", "Load", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Scanner sc = new Scanner(f);

            String loadedDifficulty = sc.nextLine().trim();
            int loadedSeconds = Integer.parseInt(sc.nextLine().trim());

            String[] loadedPuzzle = new String[9];
            for (int r = 0; r < 9; r++) loadedPuzzle[r] = sc.nextLine().trim();

            String[] loadedCurrent = new String[9];
            for (int r = 0; r < 9; r++) loadedCurrent[r] = sc.nextLine().trim();

            String[] loadedSolution = new String[9];
            for (int r = 0; r < 9; r++) loadedSolution[r] = sc.nextLine().trim();

            sc.close();

            // apply loaded data
            this.difficulty = loadedDifficulty;
            this.elapsedSeconds = loadedSeconds;
            this.puzzle = loadedPuzzle;
            this.solution = loadedSolution;

            // rebuild UI board texts and enabled state
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    char orig = puzzle[r].charAt(c);
                    char cur = loadedCurrent[r].charAt(c);
                    if (orig != '-') {
                        tiles[r][c].setText(String.valueOf(orig));
                        tiles[r][c].setEnabled(false);
                        tiles[r][c].setBackground(Color.LIGHT_GRAY);
                    } else {
                        if (cur == '-') {
                            tiles[r][c].setText("");
                            tiles[r][c].setEnabled(true);
                            tiles[r][c].setBackground(Color.WHITE);
                        } else {
                            tiles[r][c].setText(String.valueOf(cur));
                            tiles[r][c].setEnabled(false); // filled by user or hint
                            tiles[r][c].setBackground(new Color(220,255,240));
                        }
                    }
                }
            }

            // update labels and restart timer at loadedSeconds
            statusLabel.setText("Difficulty: " + difficulty.toUpperCase() + "    Errors: " + errors);
            timerLabel.setText(String.format("Time: %02d:%02d", elapsedSeconds/60, elapsedSeconds%60));
            // restart timer from loadedSeconds
            if (gameTimer != null) gameTimer.stop();
            gameTimer = new javax.swing.Timer(1000, new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    elapsedSeconds++;
                    timerLabel.setText(String.format("Time: %02d:%02d", elapsedSeconds/60, elapsedSeconds%60));
                }
            });
            gameTimer.start();

            JOptionPane.showMessageDialog(frame, "Game loaded from " + SAVE_FILE, "Loaded", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error loading save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Generator + support ----------
    static class GeneratedSudoku {
        String[] puzzle; String[] solution;
        GeneratedSudoku(String[] p, String[] s) { this.puzzle = p; this.solution = s; }
    }

    static class SudokuGenerator {
        private static final Random rand = new Random();

        static GeneratedSudoku generate(String difficulty) {
            String[] solution = generateFullSolution();
            String[] puzzle = generatePuzzleFromSolution(solution, difficulty);
            return new GeneratedSudoku(puzzle, solution);
        }

        static String[] generateFullSolution() {
            int[][] grid = new int[9][9];
            fillGrid(grid);
            String[] out = new String[9];
            for (int r = 0; r < 9; r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < 9; c++) sb.append(grid[r][c]);
                out[r] = sb.toString();
            }
            return out;
        }

        private static boolean fillGrid(int[][] g) {
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (g[r][c] == 0) {
                        ArrayList<Integer> nums = new ArrayList<>();
                        for (int i = 1; i <= 9; i++) nums.add(i);
                        Collections.shuffle(nums);
                        for (int n : nums) {
                            if (isValid(g, r, c, n)) {
                                g[r][c] = n;
                                if (fillGrid(g)) return true;
                                g[r][c] = 0;
                            }
                        }
                        return false;
                    }
                }
            }
            return true;
        }

        private static boolean isValid(int[][] g, int r, int c, int val) {
            for (int i = 0; i < 9; i++) {
                if (g[r][i] == val) return false;
                if (g[i][c] == val) return false;
            }
            int br = (r/3)*3, bc = (c/3)*3;
            for (int i = br; i < br+3; i++)
                for (int j = bc; j < bc+3; j++)
                    if (g[i][j] == val) return false;
            return true;
        }

        static String[] generatePuzzleFromSolution(String[] sol, String difficulty) {
            int removeCount;
            switch ((difficulty == null) ? "medium" : difficulty.toLowerCase()) {
                case "easy": removeCount = 35; break;
                case "medium": removeCount = 45; break;
                case "hard": removeCount = 55; break;
                default: removeCount = 45; break;
            }
            char[][] grid = new char[9][9];
            for (int r = 0; r < 9; r++)
                for (int c = 0; c < 9; c++)
                    grid[r][c] = sol[r].charAt(c);

            int removed = 0;
            while (removed < removeCount) {
                int r = rand.nextInt(9);
                int c = rand.nextInt(9);
                if (grid[r][c] != '-') {
                    grid[r][c] = '-';
                    removed++;
                }
            }

            String[] puzzle = new String[9];
            for (int r = 0; r < 9; r++) puzzle[r] = new String(grid[r]);
            return puzzle;
        }
    }

    // ---------- Internal solver used by Full Helper ----------
    static class SudokuSolver {
        static int[][] solve(int[][] board) {
            int[][] copy = new int[9][9];
            for (int i = 0; i < 9; i++) System.arraycopy(board[i], 0, copy[i], 0, 9);
            if (solveBack(copy)) return copy;
            return null;
        }

        private static boolean solveBack(int[][] g) {
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (g[r][c] == 0) {
                        for (int n = 1; n <= 9; n++) {
                            if (isValid(g, r, c, n)) {
                                g[r][c] = n;
                                if (solveBack(g)) return true;
                                g[r][c] = 0;
                            }
                        }
                        return false;
                    }
                }
            }
            return true;
        }

        private static boolean isValid(int[][] g, int r, int c, int v) {
            for (int i = 0; i < 9; i++) {
                if (g[r][i] == v) return false;
                if (g[i][c] == v) return false;
            }
            int br = (r/3)*3, bc = (c/3)*3;
            for (int i = br; i < br+3; i++)
                for (int j = bc; j < bc+3; j++)
                    if (g[i][j] == v) return false;
            return true;
        }
    }
}

class GeneratedSudoku {
    String[] puzzle;
    String[] solution;

    GeneratedSudoku(String[] puzzle, String[] solution) {
        this.puzzle = puzzle;
        this.solution = solution;
    }
}