import entity.board.Board;
import entity.board.Marble;
import entity.board.Move;
import entity.board.observer.ClientBoardObserver;
import entity.player.AIPlayer;
import entity.session.PentagoSession;
import entity.strategy.ExpertStrategy;
import entity.strategy.NaiveStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class that tests random pentago sessions given both predefined moves and random moves made by the AI.
 *
 * @author Aliaksei Kouzel
 */
public class RandomGameTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    /**
     * Test the random pentago session with AI players.
     * Board observer is optional and is used to textually observe the game situation.
     *
     * @throws InterruptedException in case the session has been interrupted
     */
    @Test
    void startPentagoSessionWithAI() throws InterruptedException {
        var player1 = new AIPlayer(new NaiveStrategy());
        var player2 = new AIPlayer(new ExpertStrategy());
        var session = new PentagoSession(player1, player2);

        var observer = new ClientBoardObserver();
        session.addObserver(observer); // optional

        Thread sessionThread = session.start();
        sessionThread.join();
    }

    /**
     * Test starting three pentago sessions with AI players.
     *
     * @throws InterruptedException in case one of the sessions has been interrupted
     */
    @Test
    void startThreePentagoSessionsWithAI() throws InterruptedException {
        Thread[] sessionThreads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            var player1 = new AIPlayer(new NaiveStrategy());
            var player2 = new AIPlayer(new ExpertStrategy());
            var session = new PentagoSession(player1, player2);
            Thread thread = session.start();
            sessionThreads[i] = thread;
        }

        for (Thread thread : sessionThreads) {
            thread.join();
        }
    }

    /**
     * Test the pentago session with predefined moves and resulting in the black player's victory by
     * placing the row streak of marbles.
     */
    @Test
    void winByBlackWithPredefinedMovesGivenRowStreak() {
        assertFalse(board.isGameOver());
        board.playMove(new Move(0, 6, Marble.BLACK));
        board.playMove(new Move(18, 7, Marble.WHITE));
        assertFalse(board.isGameOver());

        board.playMove(new Move(1, 5, Marble.BLACK));
        board.playMove(new Move(14, 4, Marble.WHITE));
        assertFalse(board.isGameOver());

        board.playMove(new Move(2, 3, Marble.BLACK));
        board.playMove(new Move(17, 7, Marble.WHITE));
        assertFalse(board.isGameOver());

        board.playMove(new Move(3, 6, Marble.BLACK));
        board.playMove(new Move(15, 4, Marble.WHITE));
        assertFalse(board.isGameOver());

        board.playMove(new Move(4, 7, Marble.BLACK));
        assertTrue(board.isGameOver());
    }

    /**
     * Test the pentago session with predefined moves and resulting in the white player's victory by
     * placing a diagonal streak of marbles.
     */
    @Test
    void winByWhiteWithPredefinedMovesGivenDiagonalStreak() {
        assertFalse(board.isGameOver());
        board.playMove(new Move(2, 2, Marble.BLACK));
        board.playMove(new Move(0, 5, Marble.WHITE));
        assertFalse(board.isGameOver());

        board.playMove(new Move(35, 3, Marble.BLACK));
        board.playMove(new Move(7, 4, Marble.WHITE));
        assertFalse(board.isGameOver());

        board.playMove(new Move(5, 2, Marble.BLACK));
        board.playMove(new Move(14, 5, Marble.WHITE));
        assertFalse(board.isGameOver());

        board.playMove(new Move(17, 2, Marble.BLACK));
        board.playMove(new Move(21, 4, Marble.WHITE));
        assertFalse(board.isGameOver());

        board.playMove(new Move(18, 5, Marble.BLACK));
        board.playMove(new Move(28, 2, Marble.WHITE));
        assertTrue(board.isGameOver());
    }
}
