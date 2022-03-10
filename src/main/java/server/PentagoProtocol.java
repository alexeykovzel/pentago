package server;

import exception.InvalidMessageException;
import entity.Expansion;
import entity.session.GameResult;
import entity.MessageType;
import exception.InvalidMoveException;

import java.util.*;

import static java.util.Map.entry;

/**
 * Class that is used as the default protocol for the pentago server.
 *
 * @author Aliaksei Kouzel
 */
public class PentagoProtocol implements ServerProtocol {
    public final static Map<String, MessageType> MESSAGE_TYPES = Map.ofEntries(
            entry("HELLO", MessageType.INIT),
            entry("LOGIN", MessageType.LOGIN),
            entry("ALREADYLOGGEDIN", MessageType.ALREADY_LOGGED_IN),
            entry("LIST", MessageType.LIST_USERS),
            entry("QUEUE", MessageType.JOIN_QUEUE),
            entry("MOVE", MessageType.MAKE_MOVE),
            entry("NEWGAME", MessageType.NEW_GAME),
            entry("GAMEOVER", MessageType.GAME_OVER),
            entry("PING", MessageType.PING),
            entry("PONG", MessageType.PONG),
            entry("QUIT", MessageType.QUIT),
            entry("ERROR", MessageType.ERROR)
    );

    public final static Map<String, Expansion> EXPANSIONS = Map.ofEntries(
            entry("AUTH", Expansion.AUTH),
            entry("CRYPT", Expansion.CRYPT),
            entry("CHAT", Expansion.CHAT),
            entry("RANK", Expansion.RANK)
    );

    public final static Map<String, GameResult> GAME_RESULTS = Map.ofEntries(
            entry("DISCONNECT", GameResult.DISCONNECT),
            entry("VICTORY", GameResult.VICTORY),
            entry("DRAW", GameResult.DRAW)
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public String convert(MessageType type, String... args) {
        // retrieve message type value
        String typeValue = getKey(MESSAGE_TYPES, type);
        if (typeValue == null) throw new InvalidMessageException();

        // add message type to its arguments
        var resultsArgs = new String[args.length + 1];
        resultsArgs[0] = typeValue;
        System.arraycopy(args, 0, resultsArgs, 1, args.length);

        // combine arguments using a delimiter
        return String.join(getDelimiter(), resultsArgs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> process(String message, boolean toServer) {
        // define the message type and its args
        String[] args = message.split(getDelimiter());
        MessageType type = MESSAGE_TYPES.get(args[0]);
        if (type == null) throw new InvalidMessageException();

        // process the message according to its type
        Map<String, Object> data = new HashMap<>();
        data.put("type", type);
        switch (type) {
            case ERROR:
                if (args.length != 2) throw new IllegalArgumentException();
                data.put("description", args[1]);
                return data;
            case INIT:
                processInitMessage(args, data);
                return data;
            case LOGIN:
                processLoginMessage(args, data, toServer);
                return data;
            case LIST_USERS:
                processListMessage(args, data, toServer);
                return data;
            case MAKE_MOVE:
                processMoveMessage(args, data);
                return data;
            case GAME_OVER:
                processGameOverMessage(args, data);
                return data;
            case NEW_GAME:
                processNewGameMessage(args, data);
                return data;
            case ALREADY_LOGGED_IN:
            case JOIN_QUEUE:
            case PING:
            case PONG:
            case QUIT:
                if (args.length != 1) {
                    String errorMessage = "This message should not contain any args";
                    throw new IllegalArgumentException(errorMessage);
                }
                return data;
            default:
                throw new InvalidMessageException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String valueOf(Expansion expansion) {
        return getKey(EXPANSIONS, expansion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String valueOf(GameResult result) {
        return getKey(GAME_RESULTS, result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDelimiter() {
        return "~";
    }

    /**
     * Process an init message carrying the client/server description and supported expansions.
     *
     * @param args message args
     * @param data message data
     */
    private void processInitMessage(String[] args, Map<String, Object> data) {
        if (args.length < 2) throw new IllegalArgumentException();

        // retrieve supported expansions
        var expansions = new Expansion[args.length - 2];
        for (int i = 2; i < args.length; i++) {
            Expansion expansion = EXPANSIONS.get(args[i]);
            if (expansion == null) throw new IllegalArgumentException("Illegal expansion");
            expansions[i - 2] = expansion;
        }

        data.put("description", args[1]);
        data.put("expansions", expansions);
    }

    /**
     * Process a login message possibly carrying the username of the logging client.
     *
     * @param args     message args
     * @param data     message data
     * @param toServer true if the message is sent to the server
     */
    private void processLoginMessage(String[] args, Map<String, Object> data, boolean toServer) {
        if (toServer) {
            if (args.length != 2) throw new IllegalArgumentException();
            data.put("username", args[1]);
        } else {
            if (args.length != 1) throw new IllegalArgumentException();
        }
    }

    /**
     * Process a list message possibly carrying the list of the usernames of the currently online users.
     *
     * @param args     message args
     * @param data     message data
     * @param toServer true if the message is sent to the server
     */
    private void processListMessage(String[] args, Map<String, Object> data, boolean toServer) {
        if (!toServer) {
            String[] users = Arrays.copyOfRange(args, 1, args.length);
            data.put("users", users);
        }
    }

    /**
     * Process a move message carrying the move details such as the next field position and the subboard rotation.
     *
     * @param args message args
     * @param data message data
     */
    private void processMoveMessage(String[] args, Map<String, Object> data) {
        if (args.length != 3) throw new IllegalArgumentException();
        int position = Integer.parseInt(args[1]);
        int rotation = Integer.parseInt(args[2]);
        if (position < 0 || position > 35) throw new InvalidMoveException("Invalid position");
        if (rotation < 0 || rotation > 7) throw new InvalidMoveException("Invalid rotation");
        data.put("position", position);
        data.put("rotation", rotation);
    }

    /**
     * Process a game over message carrying the game result and optionally the player that
     * is being referred (e.g. in case of player disconnection or victory).
     *
     * @param args message args
     * @param data message data
     */
    private void processGameOverMessage(String[] args, Map<String, Object> data) {
        GameResult result = GAME_RESULTS.get(args[1]);
        if (args.length != 3) throw new IllegalArgumentException();
        if (result == null) throw new IllegalArgumentException();

        data.put("result", result.name());
        if (result == GameResult.DISCONNECT || result == GameResult.VICTORY) {
            data.put("player", args[2]);
        }
    }

    /**
     * Process a new game message carrying the usernames of the players.
     *
     * @param args message args
     * @param data data carrying the processed args of the message
     */
    private void processNewGameMessage(String[] args, Map<String, Object> data) {
        if (args.length != 3) throw new IllegalArgumentException();
        data.put("players", new String[]{args[1], args[2]});
    }

    /**
     * Return an entry key by its value.
     *
     * @return entry key
     */
    public <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}