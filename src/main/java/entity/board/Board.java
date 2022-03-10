package entity.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Board representation of the game. it consists of 4 subboards that can be rotated clockwise or counterclockwise.
 * A board can only be shared by 2 players at a time and has 36 fields where players can place marbles.
 *
 * @author Aliaksei Kouzel
 */
public class Board {
    public static final int DIM = 6;
    public static final int SUB_DIM = 3;
    public static final int WINNING_STREAK = 5;
    private Marble[] fields;
    private Marble turn;

    public Board() {
        fields = new Marble[DIM * DIM];
        reset();
    }

    /**
     * Make a deep copy of the board.
     *
     * @return board copy
     */
    public Board deepCopy() {
        Board copiedBoard = new Board();
        copiedBoard.fields = Arrays.copyOf(fields, fields.length);
        return copiedBoard;
    }

    public Marble getTurn() {
        return turn;
    }

    /**
     * Rotate one of the 4 subboards either clockwise or counterclockwise.
     *
     * @param top       true if the subboard is on top.
     * @param left      true if the subboard is on left.
     * @param clockwise true if the subboard should be rotated clockwise.
     */
    public void rotate(boolean top, boolean left, boolean clockwise) {
        int initRow = top ? 0 : SUB_DIM;
        int initCol = left ? 0 : SUB_DIM;
        rotate(initRow, initCol, SUB_DIM, clockwise);
    }

    /**
     * Rotate one of the 4 subboards either clockwise or counterclockwise.
     * After the rotation, the move is ended, thus the turn goes to the next player.
     *
     * @param initRow   row from where the rotation starts
     * @param initCol   column from where the rotation starts
     * @param radius    rotation radius
     * @param clockwise clockwise or counterclockwise
     * @requires initRow >= 0 && initCol >= 0 && radius >= 0
     */
    public void rotate(int initRow, int initCol, int radius, boolean clockwise) {
        Board boardCopy = deepCopy();
        for (int row = initRow; row < initRow + radius; row++) {
            for (int col = initCol; col < initCol + radius; col++) {
                int nextRow = initRow;
                int nextCol = initCol;

                if (clockwise) {
                    nextRow += col - initCol;
                    nextCol += initRow - row + getCenterIndex();
                } else {
                    nextCol += row - initRow;
                    nextRow += initCol - col + getCenterIndex();
                }

                int nextPosition = getIndex(nextRow, nextCol);
                boardCopy.setField(getField(getIndex(row, col)), nextPosition);
            }
        }
        fields = boardCopy.fields;
        changeMove();
    }

    /**
     * Change the turn to the next player.
     */
    private void changeMove() {
        turn = turn.reverse();
    }

    /**
     * Rotate the board by its rotation index.
     *
     * @param index rotation index
     * @requires index >= 0 && index <= 7
     */
    public void rotate(int index) {
        boolean top = Arrays.asList(0, 1, 2, 3).contains(index);
        boolean left = Arrays.asList(0, 1, 4, 5).contains(index);
        boolean clockwise = Arrays.asList(1, 3, 5, 7).contains(index);
        rotate(top, left, clockwise);
    }

    /**
     * Make a move on the board.
     *
     * @param move move that is being played
     * @requires move != null
     */
    public void playMove(Move move) {
        setField(move.getMarble(), move.getPosition());
        rotate(move.getRotation());
    }

    /**
     * Get the center index based on the board dimension.
     *
     * @return the center index
     * @pure
     * @ensures \result == DIM / 2 - 1
     */
    private int getCenterIndex() {
        return DIM / 2 - 1;
    }

    /**
     * Get the field index based on the field row and column.
     *
     * @param row field row
     * @param col field column
     * @return field index
     * @ensures \result == row * DIM + col
     * @requires (row > = 0 & & row < = DIM) && (col >= 0 && col <= DIM)
     * @pure
     */
    public int getIndex(int row, int col) {
        return row * DIM + col;
    }

    /**
     * Set the marble with a matching field index.
     *
     * @param index  field index
     * @param marble marble
     * @requires marble != null
     * @requires (index > = 0) && (index <= 35)
     * @ensures getField(index) == fields[index]
     */
    public void setField(Marble marble, int index) {
        fields[index] = marble;
    }

    /**
     * Set the marble using provided field indexes.
     *
     * @param marble  field marble
     * @param indexes field indexes
     * @requires marble != null
     * @requires indexes.length > 0
     * @ensures (\ forall int i ; 0 < = i & & i < indexes.length ; fields[i] = = marble)
     */
    public void setFields(Marble marble, int... indexes) {
        for (int i : indexes) {
            setField(marble, i);
        }
    }

    /**
     * Get the board field based on its index.
     *
     * @param index field index
     * @return board field
     * @pure
     * @requires (index > = 0) && (index <= 35)
     */
    public Marble getField(int index) {
        return isValidField(index) ? fields[index] : null;
    }

    /**
     * Determine if the next move is valid.
     *
     * @param move next move
     * @return true if the next move is valid
     */
    public boolean isValidMove(Move move) {
        return isValidField(move.getPosition())
                && fields[move.getPosition()] == Marble.EMPTY
                && isValidRotation(move.getRotation());
    }

    /**
     * Determine if the field index is within board bounds.
     *
     * @param index field index
     * @return true if the index is valid
     */
    public boolean isValidField(int index) {
        return (index >= 0) && (index < DIM * DIM);
    }

    /**
     * Determine if the rotation index is valid.
     *
     * @param index rotation index
     * @return true if the rotation index is valid
     */
    public boolean isValidRotation(int index) {
        return index >= 0 && index <= 7;
    }

    /**
     * Determine if the game can no longer continue.
     * e.g. if the board is full, or it has a winner.
     *
     * @return true if the game is over
     * @pure
     * @ensures \result == isFull() || hasWinner()
     */
    public boolean isGameOver() {
        return isFull() || hasWinner();
    }

    /**
     * Determine if there are no longer empty fields on the board.
     *
     * @return true if the board is full
     * @pure
     */
    public boolean isFull() {
        for (int i = 0; i < DIM * DIM; i++) {
            if (fields[i] == Marble.EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determine if there is a row of the winning streak with equal marbles.
     *
     * @param marble field marble
     * @return true if such a row exists
     * @pure
     * @requires marble != null
     */
    public boolean hasRow(Marble marble) {
        for (int shift = 0; shift <= DIM - WINNING_STREAK; shift++) {
            for (int row = 0; row < DIM; row++) {
                if (isStreak(marble, row * DIM + shift, 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determine if there is a column of the winning streak with equal marbles.
     *
     * @param marble field marble
     * @return true if such a column exists
     * @pure
     * @requires marble != null
     */
    public boolean hasColumn(Marble marble) {
        for (int shift = 0; shift <= DIM - WINNING_STREAK; shift++) {
            for (int i = 0; i < DIM; i++) {
                if (isStreak(marble, i + DIM * shift, DIM)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determine if there is a diagonal of the winning streak with equal marbles.
     *
     * @param marble field marble
     * @return true if such a diagonal exists
     * @pure
     * @ensures \result == hasAscendingDiagonal(marble) || hasDescendingDiagonal(marble)
     * @requires marble != null
     */
    public boolean hasDiagonal(Marble marble) {
        return hasAscendingDiagonal(marble) || hasDescendingDiagonal(marble);
    }

    /**
     * Determine if there is a streak among ascending diagonals.
     *
     * @param marble field marble
     * @return true if such a diagonal exists
     * @pure
     * @requires marble != null
     */
    private boolean hasAscendingDiagonal(Marble marble) {
        // does not yet consider different winning streaks & dimensions
        if (isStreak(marble, DIM - 2, DIM - 1)) return true;
        if (isStreak(marble, 2 * DIM - 1, DIM - 1)) return true;

        for (int shift = 0; shift <= DIM - WINNING_STREAK; shift++) {
            if (isStreak(marble, (DIM - 1) * (shift + 1), DIM - 1)) return true;
        }
        return false;
    }

    /**
     * Determine if there is a streak among descending diagonals.
     *
     * @param marble field marble
     * @return true if such a diagonal exists
     * @pure
     * @requires marble != null
     */
    private boolean hasDescendingDiagonal(Marble marble) {
        // does not yet consider different winning streaks & dimensions
        if (isStreak(marble, 1, DIM + 1)) return true;
        if (isStreak(marble, DIM, DIM + 1)) return true;

        for (int shift = 0; shift <= DIM - WINNING_STREAK; shift++) {
            if (isStreak(marble, (DIM + 1) * shift, DIM + 1)) return true;
        }
        return false;
    }

    /**
     * Go through the board from the initial position after each shift
     * and determine whether the field marbles are equal or not.
     *
     * @param position initial position
     * @param shift    shift value
     * @param marble   field marble
     * @return true if the marks are equal
     * @pure
     * @requires marble != null
     * @requires position >= 0 && position <= 35
     * @requires shift >= 0
     */
    private boolean isStreak(Marble marble, int position, int shift) {
        for (int i = 0; i < WINNING_STREAK; i++) {
            if (fields[position + (i * shift)] != marble) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if there is a marble on given indexes.
     *
     * @param indexes field indexes
     * @param marble  field marble
     * @return true if the marks are equal on the given indexes
     * @pure
     * @requires marble != null
     * @requires /result == (\forall int i; 0 <= i && i < indexes.length; fields[i] == marble)
     */
    public boolean hasFields(Marble marble, int... indexes) {
        for (int i : indexes) {
            if (fields[i] != marble) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get all fields of the board.
     *
     * @return all fields
     * @pure
     * @requires this.fields != null
     */
    public Marble[] getFields() {
        return fields;
    }

    /**
     * Determine if the player of the given marble wins the game.
     *
     * @param marble player marble
     * @return true if the owner of the given marble wins
     * @pure
     * @requires marble != null
     * @ensures /result == hasColumn(marble) || hasRow(marble) || hasDiagonal(marble)
     */
    public boolean isWinner(Marble marble) {
        return hasColumn(marble) || hasRow(marble) || hasDiagonal(marble);
    }

    /**
     * Determine if the board has a winner.
     *
     * @return true if there is a winner on the board
     * @pure
     * @ensures /result == isWinner(Marble.BLACK) || isWinner(Marble.WHITE)
     */
    public boolean hasWinner() {
        return isWinner(Marble.BLACK) || isWinner(Marble.WHITE);
    }

    /**
     * Make all fields on the board empty and the 1-st turn by black.
     */
    public void reset() {
        Arrays.fill(fields, Marble.EMPTY);
        turn = Marble.BLACK;
    }

    /**
     * Return a possible random move.
     *
     * @return possible move
     */
    public Move getPossibleMove() {
        List<Integer> indexes = getIndexesOfEmptyFields();
        int position = indexes.get((int) (Math.random() * indexes.size()));
        int rotation = ((int) (Math.random() * 8));
        return new Move(position, rotation, getTurn());
    }

    /**
     * Get indexes of empty fields on the board.
     *
     * @return indexes of empty fields
     * @requires board != null
     * @pure
     * @ensures (/ result).isEmpty() == false
     */
    public List<Integer> getIndexesOfEmptyFields() {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < Board.DIM * Board.DIM; i++) {
            if (fields[i] == Marble.EMPTY) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    /**
     * Get a string representation of the board as well as the numbering of the fields
     * and the rotation hint overview.
     *
     * @return string overview of the board
     */
    public String toString() {
        return new BoardBuilder().build(fields);
    }
}
