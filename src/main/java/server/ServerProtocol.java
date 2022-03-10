package server;

import exception.InvalidMessageException;
import entity.Expansion;
import entity.session.GameResult;
import entity.MessageType;

import java.util.Map;

/**
 * Class that is used for the client-server bidirectional communication.
 *
 * @author Aliaksei Kouzel
 */
public interface ServerProtocol {
    /**
     * Get a message by its type and args according to the protocol.
     *
     * @param type message type
     * @param args message args
     * @return converted message according to the protocol
     */
    String convert(MessageType type, String... args) throws InvalidMessageException;

    /**
     * Process a message by retrieving its type and args.
     *
     * @param message  message value
     * @param toServer true if the message was sent to the server
     * @return data retrieved from the message
     */
    Map<String, Object> process(String message, boolean toServer);

    /**
     * Get a string value of a server expansion.
     *
     * @param expansion server expansion
     * @return string representation of the expansion
     */
    String valueOf(Expansion expansion);

    /**
     * Get a string value of a game result.
     *
     * @param result game result
     * @return string representation of the game result
     */
    String valueOf(GameResult result);

    /**
     * Get a delimiter between message args.
     *
     * @return message delimiter
     */
    String getDelimiter();
}
