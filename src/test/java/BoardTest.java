import entity.board.Board;
import entity.board.Marble;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class that tests the board implementation.
 * e.g. field rotations, setting field marbles, etc.
 *
 * @author Aliaksei Kouzel
 */
class BoardTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    /**
     * Test that the board is empty when created.
     */
    @Test
    void hasEmptyFieldsGivenCreated() {
        for (Marble marble : board.getFields()) {
            assertEquals(marble, Marble.EMPTY);
        }
    }

    /**
     * Test setting a black marble on the first field of the board.
     */
    @Test
    void setBlackMarbleGivenFieldIndex() {
        assertEquals(board.getField(0), Marble.EMPTY);
        board.setField(Marble.BLACK, 0);
        assertEquals(board.getField(0), Marble.BLACK);
    }

    /**
     * Test that fields defined by given indexes contain the white marbles.
     */
    @Test
    void hasWhiteMarblesGivenFieldIndexes() {
        assertFalse(board.hasFields(Marble.WHITE, 0, 15, 21));
        board.setFields(Marble.WHITE, 0, 15, 21);
        assertTrue(board.hasFields(Marble.WHITE, 0, 15, 21));
    }

    /**
     * Test that the board is full given that there no empty fields.
     */
    @Test
    void isFullGivenNoEmptyFields() {
        assertFalse(board.isFull());
        for (int i = 0; i < Board.DIM * Board.DIM; i++) {
            board.setField(Marble.BLACK, i);
        }
        assertTrue(board.isFull());
    }

    /**
     * Test rotating the top left subboard clockwise given predefined fields.
     */
    @Test
    void rotateTopLeftClockwise() {
        board.setFields(Marble.BLACK, 0, 7, 14);
        board.setFields(Marble.WHITE, 1, 8);
        board.rotate(true, true, true);

        assertTrue(board.hasFields(Marble.BLACK, 2, 7, 12));
        assertTrue(board.hasFields(Marble.WHITE, 8, 13));
        assertTrue(board.hasFields(Marble.EMPTY, 0, 1, 6, 14));
    }

    /**
     * Test rotating the top left subboard counterclockwise given predefined fields.
     */
    @Test
    void rotateTopLeftCounterclockwise() {
        board.setFields(Marble.BLACK, 1, 6, 13);
        board.setFields(Marble.WHITE, 8, 12);
        board.rotate(true, true, false);

        assertTrue(board.hasFields(Marble.BLACK, 6, 13, 8));
        assertTrue(board.hasFields(Marble.WHITE, 1, 14));
        assertTrue(board.hasFields(Marble.EMPTY, 0, 2, 7, 12));
    }

    /**
     * Test rotating the top right subboard counterclockwise given predefined fields.
     */
    @Test
    void rotateTopRightCounterclockwise() {
        board.setFields(Marble.BLACK, 3, 9, 16);
        board.setFields(Marble.WHITE, 5, 10);
        board.rotate(true, false, false);

        assertTrue(board.hasFields(Marble.BLACK, 15, 16, 11));
        assertTrue(board.hasFields(Marble.WHITE, 3, 10));
        assertTrue(board.hasFields(Marble.EMPTY, 4, 5, 9));
    }

    /**
     * Test rotating the top right subboard clockwise given predefined fields.
     */
    @Test
    void rotateTopRightClockwise() {
        board.setFields(Marble.BLACK, 3, 9, 15);
        board.setFields(Marble.WHITE, 11, 17);
        board.rotate(true, false, true);

        assertTrue(board.hasFields(Marble.BLACK, 3, 4, 5));
        assertTrue(board.hasFields(Marble.WHITE, 15, 16));
        assertTrue(board.hasFields(Marble.EMPTY, 9, 10, 11, 17));
    }

    /**
     * Test rotating the bottom right subboard counterclockwise given predefined fields.
     */
    @Test
    void rotateBottomRightCounterclockwise() {
        board.setFields(Marble.BLACK, 23, 28, 29, 33);
        board.setFields(Marble.WHITE, 22, 34, 35);
        board.rotate(false, false, false);

        assertTrue(board.hasFields(Marble.BLACK, 21, 22, 28, 35));
        assertTrue(board.hasFields(Marble.WHITE, 23, 27, 29));
        assertTrue(board.hasFields(Marble.EMPTY, 33, 34));
    }

    /**
     * Test rotating the bottom right subboard clockwise given predefined fields.
     */
    @Test
    void rotateBottomRightClockwise() {
        board.setFields(Marble.BLACK, 21, 28, 35);
        board.setFields(Marble.WHITE, 34, 27);
        board.rotate(false, false, true);

        assertTrue(board.hasFields(Marble.BLACK, 23, 28, 33));
        assertTrue(board.hasFields(Marble.WHITE, 27, 22));
        assertTrue(board.hasFields(Marble.EMPTY, 21, 29, 34, 35));
    }

    /**
     * Test rotating the bottom left subboard clockwise given predefined fields.
     */
    @Test
    void rotateBottomLeftClockwise() {
        board.setFields(Marble.BLACK, 19, 25);
        board.setFields(Marble.WHITE, 24, 26, 30, 32);
        board.rotate(false, true, true);

        assertTrue(board.hasFields(Marble.BLACK, 25, 26));
        assertTrue(board.hasFields(Marble.WHITE, 18, 19, 30, 31));
        assertTrue(board.hasFields(Marble.EMPTY, 20, 24, 32));
    }

    /**
     * Test rotating the bottom left subboard counterclockwise given predefined fields.
     */
    @Test
    void rotateBottomLeftCounterclockwise() {
        board.setFields(Marble.BLACK, 18, 24, 31, 32);
        board.setFields(Marble.WHITE, 19, 26);
        board.rotate(false, true, false);

        assertTrue(board.hasFields(Marble.BLACK, 30, 31, 20, 26));
        assertTrue(board.hasFields(Marble.WHITE, 24, 19));
        assertTrue(board.hasFields(Marble.EMPTY, 32, 25, 18));
    }
}