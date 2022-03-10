package client;

import entity.MessageType;
import entity.player.Player;
import entity.session.GameSession;
import server.ServerProtocol;

/**
 * Client handler communicates with a client connected to the server. It responds to client's messages
 * and also provides an opportunity to join server sessions (e.g. for playing a game).
 *
 * @author Aliaksei Kouzel
 */
public interface ClientHandler extends Runnable {
    /**
     * Send a message to the client.
     *
     * @param type message type
     * @param args message args
     */
    void sendMessage(MessageType type, String... args);

    /**
     * Handle a message sent by the client.
     *
     * @param message message sent by the client
     */
    void handleMessage(String message);

    /**
     * Join a game session on the server.
     *
     * @param session game session
     * @return true if successfully joined the session
     */
    boolean joinSession(GameSession session);

    /**
     * Leave from a game session that the client is currently assigned to.
     */
    void leaveSession();

    /**
     * Check the connection with the client by sending a ping message.
     * The other party must immediately respond with a pong message.
     */
    void ping();

    /**
     * Disconnect the client from the server.
     */
    void close();

    /**
     * Get a player instance of the client.
     *
     * @return player instance of the client
     */
    Player getPlayer();

    /**
     * Get the client username.
     *
     * @return client username
     */
    String getUsername();

    /**
     * Get the server protocol.
     *
     * @return server protocol
     */
    ServerProtocol getProtocol();
}
