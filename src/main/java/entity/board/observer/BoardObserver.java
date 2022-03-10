package entity.board.observer;

import entity.board.Board;
import entity.board.Marble;
import entity.board.Move;

/**
 * Board observer listens and reacts to board updates.
 *
 * @author Aliaksei Kouzel
 */
public interface BoardObserver {
    /**
     * Handle the next move on the board.
     *
     * @param move the next move that is being played
     */
    void onMove(Move move);

    /**
     * Announce the winner of the game.
     *
     * @param player winner of the game
     */
    void onWinner(String player);

    /**
     * Handle a situation when one of the players disconnects.
     *
     * @param player disconnected player
     */
    void onDisconnect(String player);

    /**
     * Announce the draw of the game.
     */
    void onDraw();

    /**
     * Handle closing the board.
     */
    void onClose();

    /**
     * Handle starting the game. The method also carries the usernames of the players.
     */
    void onStart(String... players);

    /**
     * Return a copy of the board.
     *
     * @return copy of the baord
     */
    Board getBoardCopy();
}
