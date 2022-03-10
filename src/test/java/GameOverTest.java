import entity.board.Board;
import entity.board.Marble;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class that tests game over conditions of the board.
 *
 * @author Aliaksei Kouzel
 */
public class GameOverTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    /**
     * Test the draw result after fully filling the board with marbles so that there are no
     * streaks either in raw, column or diagonal.
     */
    @Test
    void drawIfFullBoard() {
        assertFalse(board.isFull());
        for (int i = 0; i < Board.DIM * Board.DIM; i += 2) {
            boolean halfPassed = i >= Board.DIM * Board.DIM / 2;
            Marble nextMarble = halfPassed ? Marble.BLACK : Marble.WHITE;
            board.setField(nextMarble, i);
            board.setField(nextMarble.reverse(), i + 1);
        }
        assertFalse(board.hasWinner());
        assertTrue(board.isFull());
    }

    /**
     * Test the winning result given a streak of white marbles in the second row of the board.
     */
    @Test
    void winInSecondRow() {
        assertFalse(board.isWinner(Marble.WHITE));
        for (int i : Arrays.asList(0, 1, 2, 3, 4)) {
            board.setField(Marble.WHITE, i);
        }
        assertTrue(board.isWinner(Marble.WHITE));
    }

    /**
     * Test the winning result given a streak of black marbles in the fourth row of the board.
     */
    @Test
    void winInFourthRow() {
        assertFalse(board.isWinner(Marble.BLACK));
        for (int i : Arrays.asList(19, 20, 21, 22, 23)) {
            board.setField(Marble.BLACK, i);
        }
        assertTrue(board.isWinner(Marble.BLACK));
    }

    /**
     * Test the winning result given a streak of white marbles in the first column of the board.
     */
    @Test
    void winInFirstColumn() {
        assertFalse(board.isWinner(Marble.WHITE));
        for (int i : Arrays.asList(0, 6, 12, 18, 24)) {
            board.setField(Marble.WHITE, i);
        }
        assertTrue(board.isWinner(Marble.WHITE));
    }

    /**
     * Test the winning result given a streak of black marbles in the fourth column of the board.
     */
    @Test
    void winInFourthColumn() {
        assertFalse(board.isWinner(Marble.BLACK));
        for (int i : Arrays.asList(9, 15, 21, 27, 33)) {
            board.setField(Marble.BLACK, i);
        }
        assertTrue(board.isWinner(Marble.BLACK));
    }

    /**
     * Test the winning result given a streak of black marbles in an ascending diagonal of the board.
     */
    @Test
    void winInAscendingDiagonal() {
        assertFalse(board.isWinner(Marble.BLACK));
        board.setFields(Marble.BLACK, 4, 9, 14, 19, 24);
        assertTrue(board.isWinner(Marble.BLACK));
    }

    /**
     * Test the winning result given a streak of black marbles in a descending diagonal of the board.
     */
    @Test
    void winInDescendingDiagonal() {
        assertFalse(board.isWinner(Marble.BLACK));
        board.setFields(Marble.BLACK, 7, 14, 21, 28, 35);
        assertTrue(board.isWinner(Marble.BLACK));
    }

    /**
     * Test the winning result given a streak of black marbles in a descending diagonal of the board after
     * turning the top left subboard clockwise.
     */
    @Test
    void winInDescendingDiagonalAfterTurningSubboard() {
        board.setFields(Marble.BLACK, 0, 7, 14, 28, 33);
        assertFalse(board.isWinner(Marble.BLACK));
        board.rotate(false, false, true);
        assertTrue(board.isWinner(Marble.BLACK));
    }

    /**
     * Test the winning result given a streak of white marbles in the first row of the board after
     * turning the top left subboard clockwise.
     */
    @Test
    void winInFirstRowAfterTurningSubboard() {
        for (int i : Arrays.asList(0, 1, 2, 5, 11)) {
            board.setFields(Marble.WHITE, i);
        }
        assertFalse(board.isWinner(Marble.WHITE));
        board.rotate(true, false, false);
        assertTrue(board.isWinner(Marble.WHITE));
    }

    /**
     * Test that player of the black marble does not win given one of the marbles in a streak is white.
     */
    @Test
    void noWinIfOpponentMarbleInterferes() {
        assertFalse(board.isWinner(Marble.BLACK));
        for (int i : Arrays.asList(6, 7, 8, 10)) {
            board.setFields(Marble.BLACK, i);
        }
        board.setFields(Marble.WHITE, 9);
        assertFalse(board.isWinner(Marble.BLACK));
    }
}
