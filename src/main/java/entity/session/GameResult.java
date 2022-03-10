package entity.session;

/**
 * Class that contain possible results of the game.
 *
 * @author Aliaksei Kouzel
 */
public enum GameResult {
    /**
     * When one of the players has won the game.
     */
    VICTORY,

    /**
     * When the game has ended with a draw.
     */
    DRAW,

    /**
     * When one of the players has disconnected during the game.
     */
    DISCONNECT
}
