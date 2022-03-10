package entity.player;

import entity.board.Board;
import entity.board.Marble;
import entity.board.Move;

/**
 * Player decides where to put a marble and how to rotate the subboard.
 *
 * @author Aliaksei Kouzel
 */
public abstract class Player {
    private final String username;
    private Marble turn;

    public Player(String username) {
        this.username = username;
    }

    /**
     * Decide the next move given the state of the board.
     *
     * @param board state of the board
     * @requires board != null
     */
    public abstract Move decideMove(Board board);

    /**
     * Get a player username.
     *
     * @return player username
     * @pure
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get a player turn.
     *
     * @return player turn
     */
    public Marble getTurn() {
        return turn;
    }

    /**
     * Set a player turn.
     *
     * @param turn player turn
     */
    public void setTurn(Marble turn) {
        this.turn = turn;
    }
}
