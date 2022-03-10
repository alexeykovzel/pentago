package entity;

/**
 * Class that contains different states of the client.
 *
 * @author Aliaksei Kouzel
 */
public enum ClientState {
    /**
     * During this state, the client and the server are handshaking their supported extensions and
     * descriptions (almost seamlessly for both sides). If the handshaking has been successful,
     * the client switches to NOT_LOGGED_IN state.
     */
    CONNECTING,

    /**
     * During this state, the client has restricted options for communicating with the server unless he/she
     * decides to go through the authorization process. If successful, the client moves to LOGGED_IN state.
     */
    NOT_LOGGED_IN,

    /**
     * During this state, the client is fully authorized on the server and can start playing games.
     */
    LOGGED_IN,

    /**
     * During this state, the client is playing a game and has a possibility to make moves.
     */
    IN_GAME,
}