package client;

import entity.ClientState;
import entity.board.Move;
import entity.strategy.Strategy;
import exception.InvalidMoveException;
import entity.MessageType;

import java.net.InetAddress;

/**
 * Class that represents a game client that is used to communicate with the network server.
 *
 * @author Aliaksei Kouzel
 */
public interface GameClient extends Runnable {
    /**
     * Connect to a network server by its host address and the port.
     *
     * @param address server address
     * @param port    server port
     * @return true if successfully connected to the server
     */
    boolean connect(InetAddress address, int port);

    /**
     * Handle a message sent by the server.
     *
     * @param message server message
     */
    void handleMessage(String message);

    /**
     * Initialize the client once the connection with the server has been established.
     *
     * @param description client description
     * @return true if successfully initialized on the server
     */
    boolean init(String description);

    /**
     * Login to the server.
     *
     * @param username client username
     * @return true if successfully logged in
     */
    boolean login(String username);

    /**
     * Close the connection with the server.
     */
    void close();

    /**
     * Request the server to send a list of currently online users.
     */
    void list();

    /**
     * Check the connection with the server by sending a ping message.
     * The other party must immediately respond with a pong message.
     */
    void ping();

    /**
     * Join or leave the queue on the server to play a game.
     */
    void play();

    /**
     * Join or leave the queue on the server to play a game as an AI player using a provided strategy.
     *
     * @param strategy strategy that the future AI player will utilize
     */
    void play(Strategy strategy);

    /**
     * Decide a move during the game session.
     *
     * @param position field position
     * @param rotation subboard position
     * @throws InvalidMoveException if the move details are invalid
     */
    void playMove(int position, int rotation) throws InvalidMoveException;

    /**
     * Return a hint containing the information on how to make a move,
     * along with a random possible move on the board.
     *
     * @return hint on possible moves
     */
    Move getMoveHint();

    /**
     * Send a message to the server.
     *
     * @param type message type
     * @param args message args
     */
    void sendMessage(MessageType type, String[] args);

    /**
     * Get the current client state.
     *
     * @return client state
     */
    ClientState getState();
}
