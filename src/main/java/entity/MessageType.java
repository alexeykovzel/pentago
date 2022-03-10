package entity;

/**
 * Class that contains types of messages that are being communicated between the client and the server.
 *
 * @author Aliaksei Kouzel
 */
public enum MessageType {
    /**
     * (client) Sent as an initial message by the client once the connection has been established.
     * <p>
     * (server) Sent as a response to the initial message by the client with the same details.
     */
    INIT,

    /**
     * (client) Sent as to claim a username on the server. If there is already a client logged in with this username,
     * the server should respond with "already logged in" message, and the client should try a different username.
     * <p>
     * (server) Sent as a response to a successful login attempt by the client.
     * This marks the end of the initialization sequence.
     */
    LOGIN,

    /**
     * (server) Sent as a reply to a "login" message by the client, when there is already a client
     * connected to the server using this username.
     */
    ALREADY_LOGGED_IN,

    /**
     * (client) Sent to request a list of clients who are currently logged into the server.
     * Allowed at any point once the client himself has logged in.
     * <p>
     * (server) Sent as a reply to a "list" message from the client. Lists the different usernames that are currently
     * logged into the server, including the requesting client. The order of the usernames can be arbitrary.
     */
    LIST_USERS,

    /**
     * (client) Sent by the client to indicate that it wants to participate in a game.
     * The server will place the client at the back of the queue of waiting players.
     * When the message is sent the second time, the client is removed from the queue.
     */
    JOIN_QUEUE,

    /**
     * (server) Sent by the server to all players that are put into a newly-started game.
     * Only players that were queued are allowed to be put into a game.
     */
    NEW_GAME,

    /**
     * (server) Sent by the server to indicate the end of the game.
     * The server provides the winning player with the reason for the end of the game.
     */
    GAME_OVER,

    /**
     * (client) Sent by the client to indicate the next field position and the subboard rotation
     * the player wants to push. Only allowed when it is the player's turn.
     * <p>
     * (server) Sent by the server to indicate the next move that is being played.
     * Sent to all players in the game, including the player who executed the move.
     */
    MAKE_MOVE,

    /**
     * Sent by client or server. The other party must immediately return a 'pong' message.
     */
    PING,

    /**
     * Sent by client or server in response to 'ping' message.
     */
    PONG,

    /**
     * Sent by client or server to ask the other party to disconnect.
     */
    QUIT,

    /**
     * Send by client or server to indicate an error message.
     */
    ERROR
}
