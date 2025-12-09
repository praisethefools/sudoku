import java.util.*;

public class SudokuGenerator {

    // Generate puzzle berdasarkan difficulty
    public static GeneratedSudoku generate(String difficulty) {
        int[][] solution = generateFullGrid();
        int[][] puzzle = deepCopy(solution);

        int removeCount = 0;

        switch (difficulty) {
            case "easy": removeCount = 35; break;
            case "medium": removeCount = 45; break;
            case "hard": removeCount = 55; break;
        }

        removeCells(puzzle, removeCount);

        return new GeneratedSudoku(convert(puzzle), convert(solution));
    }

    // ------------------- Generator Tools -------------------

    private static int[][] generateFullGrid() {
        int[][] grid = new int[9][9];
        fill(grid);
        return grid;
    }

    private static boolean fill(int[][] grid) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (grid[r][c] == 0) {
                    List<Integer> nums = randomNumbers();
                    for (int num : nums) {
                        if (isSafe(grid, r, c, num)) {
                            grid[r][c] = num;
                            if (fill(grid))
                                return true;
                            grid[r][c] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isSafe(int[][] grid, int r, int c, int num) {
        for (int i = 0; i < 9; i++) {
            if (grid[r][i] == num) return false;
            if (grid[i][c] == num) return false;
        }

        int sr = r - r % 3;
        int sc = c - c % 3;
        for (int i = sr; i < sr + 3; i++)
            for (int j = sc; j < sc + 3; j++)
                if (grid[i][j] == num) return false;

        return true;
    }

    private static List<Integer> randomNumbers() {
        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= 9; i++) nums.add(i);
        Collections.shuffle(nums);
        return nums;
    }

    private static void removeCells(int[][] grid, int count) {
        Random rand = new Random();
        while (count > 0) {
            int r = rand.nextInt(9);
            int c = rand.nextInt(9);
            if (grid[r][c] != 0) {
                grid[r][c] = 0;
                count--;
            }
        }
    }

    private static int[][] deepCopy(int[][] arr) {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++)
            copy[i] = arr[i].clone();
        return copy;
    }

    private static String[] convert(int[][] grid) {
        String[] arr = new String[9];
        for (int i = 0; i < 9; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 9; j++) {
                sb.append(grid[i][j] == 0 ? '-' : grid[i][j]);
            }
            arr[i] = sb.toString();
        }
        return arr;
    }
}
