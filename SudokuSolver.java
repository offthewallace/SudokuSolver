package program3;

/**
 * @Date:Nov.10.2016
 * @author Wallace He
 */
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/* This class represents a program that takes an input file specifying a valid Sudoku
 * puzzle and displays the original board and one solution for the Sudoku puzzle if at
 * least one solution exists.
 */

/**
 *
 * @author Wallace
 */

public class SudokuSolver {

    private int[][] myClue;
    private int[][] mySolution;
    /**
     * Symbol used to indicate a blank grid position
     */
    public static final int BLANK = 0;
    /**
     * Overall size of the grid
     */
    public static final int DIMENSION = 9;
    /**
     * Size of a sub region
     */
    public static final int REGION_DIM = 3;

    // For debugging purposes -- see solve() skeleton.
    private Scanner kbd;
    private static final boolean DEBUG = false;

    /**
     * Run the solver. If args.length>= 1, use args[0] as the name of a file
     * containing a puzzle, otherwise, allow the user to browse for a file.
     * @param args
     */
    public static void main(String[] args) {
        String filename = null;
        if (args.length < 1) {
            // file dialog
            //filename = args[0];
            JFileChooser fileChooser = new JFileChooser();
            try {
                File f = new File(new File(".").getCanonicalPath());
                fileChooser.setCurrentDirectory(f);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            int retValue = fileChooser.showOpenDialog(new JFrame());

            if (retValue == JFileChooser.APPROVE_OPTION) {
                File theFile = fileChooser.getSelectedFile();
                filename = theFile.getAbsolutePath();
            } else {
                System.out.println("No file selected: exiting.");
                System.exit(0);
            }
        } else {
            filename = args[0];
        }

        SudokuSolver s = new SudokuSolver(filename);
        if (DEBUG) {
            s.print();
        }

        if (s.solve(0, 0)) {
            // Pop up a window with the clue and the solution.
            s.display();
        } else {
            System.out.println("No solution is possible.");
        }

    }

    /**
     * Create a solver given the name of a file containing a puzzle. We expect
     * the file to contain nine lines each containing nine digits separated by
     * whitespace. A digit from {1...9} represents a given value in the clue,
     * and the digit 0 indicates a position that is blank in the initial puzzle.
     * @param puzzleName
     */
    public SudokuSolver(String puzzleName) {
        myClue = new int[DIMENSION][DIMENSION];
        mySolution = new int[DIMENSION][DIMENSION];
        // Set up keyboard input if we need it for debugging.
        if (DEBUG) {
            kbd = new Scanner(System.in);
        }

        File pf = new File(puzzleName);
        Scanner s = null;
        try {
            s = new Scanner(pf);
        } catch (FileNotFoundException f) {
            System.out.println("Couldn't open file.");
            System.exit(1);
        }

        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                myClue[i][j] = s.nextInt();
            }
        }

        // Copy to solution
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                mySolution[i][j] = myClue[i][j];
            }
        }
    }

    /**
     * Starting at a given grid position, generate values for all 
     * remaining grid positions that do not violate the game constraints.
     *
     * @param row The row of the position to begin with
     * @param col The column of the position to begin with.
     *
     * @return true if a solution was found starting from this position, false
     * if not.
     */
    public boolean solve(int row, int col) {
        // This code will print the solution array and then wait for 
        // you to type "Enter" before proceeding. Helpful for debugging.
        // Set the DEBUG constant to true at the top of the class
        // declaration to turn this on.
        if (DEBUG) {
            System.out.println("solve(" + row + ", " + col + ")");
            print();
            kbd.nextLine();
        }

        //This Code will check the position of Row and Col, if Row is larger
        //than DIMENSION, then change Row to 0 and change to another col.
        //If the col reach the DIMENSION, then it means the code already pass
        //all the cells and we return ture.
        if (row == DIMENSION) {
            row = 0;
            col++;
            if (col == DIMENSION) {
                return true;
            }
        }

        //Pass the cell already with the number in
        if (mySolution[row][col] != BLANK) {

            return solve(row + 1, col);

        }
        // check all the number smaller than DIMENSION to see if it fit in the cell
        // it has a recursive call
        for (int i = 1; i <= DIMENSION; i++) {

            if (!containedInCol(row, col, i) && !containedInBox(row, col, i)
                    && !containedInRow(row, col, i)) {
                mySolution[row][col] = i;

                if (solve(row + 1, col)) {
                    return true;
                }
            }
        }

        //the situation of can not find the result
        mySolution[row][col] = BLANK;
        return false;

    }

    /**
     * Check if a value contains in its REGION_DIM x  REGION_DIM box 
     * for a cell.
     *
     * @param row current row index.
     * @param col current column index.
     * @param value current value need to be checked in the cell
     * @return true if this cell is incorrect or duplicated in its box.
     */
    private boolean containedInBox(int row, int col, int value) {
        // Find the top left of its box to start validating from
        int startRow = row / REGION_DIM * REGION_DIM;
        int startCol = col / REGION_DIM * REGION_DIM;

    // Check within its REGION_DIM x REGION_DIM box except its cell
        for (int i = startRow; i < startRow + REGION_DIM; i++) {

            for (int j = startCol; j < startCol + REGION_DIM; j++) {

                if (!(i == row && j == col)) {

                    if (mySolution[i][j] == value) {

                        return true;
                    }
                }
            }
        }

        return false;
    }

    /*private boolean containedInRowCol(int row, int col, int value) {
        for (int i = 0; i < DIMENSION; i++) {

            if (i != col) {
                if (mySolution[row][i] == value) {
                    return true;
                }
            }
            if (i != row) {
                if (mySolution[i][col] == value) {
                    return true;
                }
            }
        }

       
        return false;
    }
     */
    
    
    /**
     * Check if a value is contained within its column.
     *
     * @param row current row index.
     * @param col current column index.
     * @param value value in this cell.
     * @return true if this value is already in its columns.
     */
    private boolean containedInCol(int row, int col, int value) {
        for (int i = 0; i < DIMENSION; i++) {
            
            //passing the current value in the ccurrent cell
            if (i != col) {
            //if the value is found in cell of col return ture    
                if (mySolution[row][i] == value) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * Check if a value is contained within its row.
     *
     * @param row current row index.
     * @param col current column index.
     * @param value value in this cell.
     * @return true if this value is duplicated in its rows.
     */
    private boolean containedInRow(int row, int col, int value) {

        for (int i = 0; i < DIMENSION; i++) {
            
            //passing the current value in the ccurrent cell
            if (i != row) {
                
             //if the value is found in cell of row return ture    
                if (mySolution[i][col] == value) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * Print a character-based representation of the solution array on standard
     * output.
     */
    public void print() {
        System.out.println("+---------+---------+---------+");
        for (int i = 0; i < DIMENSION; i++) {
            System.out.println("|         |         |         |");
            System.out.print("|");
            for (int j = 0; j < DIMENSION; j++) {
                System.out.print(" " + mySolution[i][j] + " ");
                if (j % REGION_DIM == (REGION_DIM - 1)) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (i % REGION_DIM == (REGION_DIM - 1)) {
                System.out.println("|         |         |         |");
                System.out.println("+---------+---------+---------+");
            }
        }
    }

    /**
     * Pop up a window containing a nice representation of the original puzzle
     * and out solution.
     */
    public void display() {
        JFrame f = new DisplayFrame();
        f.pack();
        f.setVisible(true);
    }

    /**
     * GUI display for the clue and solution arrays.
     */
    private class DisplayFrame extends JFrame implements ActionListener {

        private JPanel mainPanel;

        private DisplayFrame() {
            mainPanel = new JPanel();
            mainPanel.add(buildBoardPanel(myClue, "Clue"));
            mainPanel.add(buildBoardPanel(mySolution, "Solution"));
            add(mainPanel, BorderLayout.CENTER);

            JButton b = new JButton("Quit");
            b.addActionListener(this);
            add(b, BorderLayout.SOUTH);
        }

        private JPanel buildBoardPanel(int[][] contents, String label) {
            JPanel holder = new JPanel();
            JLabel l = new JLabel(label);
            BorderLayout b = new BorderLayout();
            holder.setLayout(b);
            holder.add(l, BorderLayout.NORTH);
            JPanel board = new JPanel();
            GridLayout g = new GridLayout(9, 9);
            g.setHgap(0);
            g.setVgap(0);
            board.setLayout(g);
            Color[] colorChoices = new Color[2];
            colorChoices[0] = Color.WHITE;
            colorChoices[1] = Color.lightGray;
            int colorIdx = 0;
            int rowStartColorIdx = 0;

            for (int i = 0; i < DIMENSION; i++) {
                if (i > 0 && i % REGION_DIM == 0) {
                    rowStartColorIdx = (rowStartColorIdx + 1) % 2;
                }
                colorIdx = rowStartColorIdx;
                for (int j = 0; j < DIMENSION; j++) {
                    if (j > 0 && j % REGION_DIM == 0) {
                        colorIdx = (colorIdx + 1) % 2;
                    }
                    JTextField t = new JTextField("" + contents[i][j]);
                    if (contents[i][j] == 0) {
                        t.setText("");
                    }
                    t.setPreferredSize(new Dimension(35, 35));
                    t.setEditable(false);
                    t.setHorizontalAlignment(JTextField.CENTER);
                    t.setBackground(colorChoices[colorIdx]);
                    board.add(t);
                }
            }
            holder.add(board, BorderLayout.CENTER);
            return holder;
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}
