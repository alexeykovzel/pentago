package entity.session;

import entity.board.Marble;
import entity.board.Move;
import entity.board.observer.BoardObserver;
import entity.player.Player;

/**
 * Game session is used to play a game session separately from other sessions.
 *
 * @author Aliaksei Kouzel
 */
public interface GameSession extends Runnable {
    /**
     * Join a player by setting the next free turn.
     *
     * @param player joining player into the session
     * @return true if the player joined successfully
     */
    boolean join(Player player);

    /**
     * Start a session in a separate thread.
     *
     * @return the thread in which the session has been started
     */
    Thread start();

    /**
     * Join a player by setting the provided turn if available.
     *
     * @param player joining player into the session
     * @param turn   turn that the other party wants to assign to the player
     * @return true if the player joined successfully
     */
    boolean join(Player player, int turn);

    /**
     * Handle a player leaving the session.
     *
     * @param player leaving player
     */
    void disconnect(Player player);

    /**
     * Close a currently running session.
     */
    void close();

    /**
     * Check if there is such player in the session.
     *
     * @param player player that is being checked
     * @return true if such player exists in the session
     */
    boolean hasPlayer(Player player);

    /**
     * Check the validity of the move.
     *
     * @param move move that is being checked
     * @return true if the move is valid
     */
    boolean isValidMove(Move move);

    /**
     * Add an observer of the session board.
     *
     * @param observer board observer
     */
    void addObserver(BoardObserver observer);

    /**
     * Check if the session is running.
     *
     * @return true if the session is running
     */
    boolean isRunning();

    /**
     * Return the current turn of the session.
     *
     * @return current turn of the session
     */
    Marble getTurn();
}
