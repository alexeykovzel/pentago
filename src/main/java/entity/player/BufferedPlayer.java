package entity.player;

import entity.board.Board;
import entity.board.Move;

/**
 * Player that stores its next moves into the buffer.
 *
 * @author Aliaksei Kouzel
 */
public class BufferedPlayer extends Player {
    private Move bufferedMove;

    public BufferedPlayer(String username) {
        super(username);
    }

    /**
     * Decide the next move by taking one from the buffer. If there is none, wait until there is.
     *
     * @param board state of the board
     * @return the next move from the buffer
     */
    @Override
    public synchronized Move decideMove(Board board) {
        try {
            while (bufferedMove == null) wait();
            return bufferedMove;
        } catch (InterruptedException e) {
            return null;
        } finally {
            bufferedMove = null;
        }
    }

    /**
     * Store the next move that will be played into the buffer.
     *
     * @param nextMove next move decided by the player
     */
    public synchronized void storeMove(Move nextMove) {
        bufferedMove = nextMove;
        notifyAll();
    }
}
