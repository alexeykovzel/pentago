package entity.strategy;

import entity.board.Board;
import entity.board.Marble;
import entity.board.Move;

/**
 * Strategy is used by the AI to decide the next moves in the game.
 *
 * @author Aliaksei Kouzel
 */
public interface Strategy {
    /**
     * Get the strategy name.
     *
     * @return strategy name
     */
    String getName();

    /**
     * Decide the next move on the board.
     *
     * @param board state of the board
     * @return the next move played by the AI
     */
    Move decideMove(Board board);
}
