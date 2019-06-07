import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Random;

public class Minesweeper extends JFrame {
    private JButton btn_reset;
    private JPanel game_panel;
    private JLabel lbl_mines;
    private JPanel content_pane;
    private JCheckBox chk_mines;
    private CustomButton[][] buttons;

    private int[][] land; // -1 is mine, 0 is blank, 1+ is number
    private int[][] filled; // 0 is unclicked, 1 is clicked, 2 is flagged
    private int mines;
    private boolean game_over;

    public Minesweeper() {
        setTitle("Minesweeper");
        setSize(new Dimension(500, 500));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(content_pane);
        btn_reset.addActionListener(e -> generate_game());
        setVisible(true);
    }

    public void generate_game() {
        // Reset values
        game_over = false;
        land = new int[10][10];
        filled = new int[10][10];
        mines = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                land[i][j] = 0;
                filled[i][j] = 0;
            }
        }

        // Create mines, don't worry about random location
        Random random = new Random();
        while (mines < 10) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);
            if (land[x][y] != -1) {
                land[x][y] = -1;
                mines++;
            }
        }

        // Calculate other values
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (land[i][j] != -1) {

                    land[i][j] = 0;
                    if (i > 0) {
                        // We can check the left column
                        if (j > 0 && land[i-1][j-1] == -1) land[i][j]++;
                        if (j < 9 && land[i-1][j+1] == -1) land[i][j]++;
                        if (land[i-1][j] == -1) land[i][j]++;
                    }
                    if (i < 9) {
                        // We can check the right column
                        if (j > 0 && land[i+1][j-1] == -1) land[i][j]++;
                        if (j < 9 && land[i+1][j+1] == -1) land[i][j]++;
                        if (land[i+1][j] == -1) land[i][j]++;
                    }
                    if (j > 0 && land[i][j-1] == -1) land[i][j]++; // Up
                    if (j < 9 && land[i][j+1] == -1) land[i][j]++; // Down
                }
            }
        }

        // Print board
        for (int i = 0; i < 10; i++) {
            System.out.println(Arrays.toString(land[i]));
        }

        // Update
        chk_mines.setSelected(false);
        update_board();
    }

    private void perform_click(int row, int column) {
        if (game_over) return;
        if (!chk_mines.isSelected()) {
            // Perform click
            check_click(row, column);
            if (land[row][column] == -1) game_over = true;
        } else {
            // Perform flag
            if (filled[row][column] == 0) {
                // Set flag
                filled[row][column] = 2;
                mines--;
                // Check game state
                int correct_flags = 0;
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (filled[i][j] == 2 && land[i][j] == -1)
                            correct_flags++;
                    }
                }
                if (correct_flags == 10) {
                    // Yay
                    update_board();
                    JOptionPane.showMessageDialog(null,"You win!");
                    game_over = true;
                }
            } else if (filled[row][column] == 2) {
                // Remove flag
                filled[row][column] = 0;
                mines++;
            }
        }

        update_board();
    }

    private void check_click(int row, int column) {
        if (filled[row][column] == 1) return;
        filled[row][column] = 1;
        if (land[row][column] != 0) return;
        if (row > 0) {
            if (column > 0) check_click(row - 1, column - 1);
            check_click(row - 1, column);
            if (column < 9) check_click(row - 1, column + 1);
        }
        if (row < 9) {
            if (column > 0) check_click(row + 1, column - 1);
            check_click(row + 1, column);
            if (column < 9) check_click(row + 1, column + 1);
        }
        if (column > 0) check_click(row, column - 1);
        if (column < 9) check_click(row, column + 1);
    }

    private void update_board() {
        lbl_mines.setText("Remaining: " + mines);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {

                /*
                    private int[][] land; // -1 is mine, 0 is blank, 1+ is number
                    private int[][] filled; // 0 is unclicked, 1 is clicked, 2 is flagged
                 */

                // Set push state
                if (filled[i][j] == 0) {
                    buttons[i][j].setSelected(false);
                    buttons[i][j].setEnabled(true);
                } else if (filled[i][j] == 1) {
                    buttons[i][j].setSelected(true);
                    buttons[i][j].setEnabled(false);
                }

                // Set text
                if (land[i][j] > 0 && filled[i][j] == 1) {
                    buttons[i][j].setText(land[i][j] + "");
                } else if (land[i][j] == -1 && filled[i][j] == 1) {
                    buttons[i][j].setText("*");
                } else if (filled[i][j] == 2) {
                    buttons[i][j].setText("@");
                } else {
                    buttons[i][j].setText("");
                }
            }
        }
        this.invalidate();
    }

    private void createUIComponents() {
        // Instantiate buttons
        buttons = new CustomButton[10][10];
        game_panel = new JPanel();
        game_panel.setLayout(new GridLayout(10, 10));
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                CustomButton cb = new CustomButton(i, j, " ");
                cb.addActionListener(e -> perform_click(cb.getRow(), cb.getColumn()));
                buttons[i][j] = cb;
                game_panel.add(cb);
            }
        }
    }

    public static void main(String args[]) {
        Minesweeper minesweeper = new Minesweeper();
        minesweeper.generate_game();
    }
}

class CustomButton extends JButton {

    private int row;
    public int getRow() { return row; }

    private int column;
    public int getColumn() { return column; }

    CustomButton(int row, int column, String label) {
        super(label);
        this.row = row;
        this.column = column;
    }
}
