package client;

import entity.board.Board;
import entity.board.observer.BoardObserver;
import entity.player.AIPlayer;
import entity.player.Player;
import entity.strategy.Strategy;
import exception.InvalidMoveException;
import exception.WrongStateException;
import entity.ClientState;
import entity.Expansion;
import entity.board.Marble;
import entity.board.Move;
import entity.board.observer.ClientBoardObserver;
import exception.InvalidMessageException;
import entity.MessageType;
import server.PentagoProtocol;
import server.ServerProtocol;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

/**
 * Class that represents a client which can connect to a pentago server by its address and port.
 * Then, using the provided server protocol, send and accept messages. Also, the client is able to
 * play pentago sessions with other clients on the server.
 *
 * @author Aliaksei Kouzel
 */
public class PentagoClient implements GameClient {
    private static final Expansion[] CLIENT_EXPANSIONS = new Expansion[]{};
    private final ServerProtocol protocol = new PentagoProtocol();
    private final String description;
    private Set<Expansion> expansions;
    private String serverDescription;
    private String bufferedUsername;
    private BoardObserver observer;
    private ClientState state;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private Socket client;
    private Long pingSent;
    private Player player;

    private boolean inQueue;
    private boolean isWaitingMessage;

    public PentagoClient(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean connect(InetAddress address, int port) {
        try {
            client = new Socket();
            client.connect(new InetSocketAddress(address, port), 1000);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            state = ClientState.CONNECTING;
            new Thread(this).start();
            return init(description);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        // listen to messages sent by the server
        Thread messageListener = new Thread(new MessageListener());

        // wait until the connection is lost or interrupted
        try {
            messageListener.start();
            messageListener.join();
        } catch (InterruptedException ignored) {
        } finally {
            System.out.println("Disconnecting from server...");
        }

        // client should self-destruct
        System.exit(0);
    }

    /**
     * Runnable class that listens to messages sent by the server.
     */
    private class MessageListener implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.printf("[INCOMING] '%s'\n", message); // for testing
                    if (message.equals("")) continue;
                    handleMessage(message);
                }
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            sendMessage(MessageType.QUIT);
            client.close();
        } catch (IOException e) {
            /* Should not be thrown */
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean init(String description) {
        int argsCount = CLIENT_EXPANSIONS.length + 1;
        var args = new String[argsCount];
        args[0] = description;
        for (int i = 1; i < argsCount; i++) {
            args[i] = protocol.valueOf(CLIENT_EXPANSIONS[i - 1]);
        }
        sendMessage(MessageType.INIT, args);
        isWaitingMessage = true;

        try {
            while (isWaitingMessage) wait();
            return state == ClientState.NOT_LOGGED_IN;
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean login(String username) {
        if (username.contains(protocol.getDelimiter())) return false;
        bufferedUsername = username;
        sendMessage(MessageType.LOGIN, username);
        isWaitingMessage = true;

        try {
            while (isWaitingMessage) wait();
            return state == ClientState.LOGGED_IN;
        } catch (InterruptedException e) {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void play() {
        if (state != ClientState.IN_GAME) {
            sendMessage(MessageType.JOIN_QUEUE);
            String message = (inQueue ? "Leaving" : "Joining") + " the queue...";
            System.out.println(message);
            inQueue = !inQueue;
        } else {
            System.out.println("Client is already playing...");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void play(Strategy strategy) {
        if (state != ClientState.IN_GAME) {
            player = new AIPlayer(strategy);
            play();
        } else {
            System.out.println("Client is already playing...");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void playMove(int position, int rotation) throws InvalidMoveException {
        if (state != ClientState.IN_GAME) throw new WrongStateException("Client is not playing...");
        if (position < 0 || position > 35) throw new InvalidMoveException("Invalid position...");
        if (rotation < 0 || rotation > 7) throw new InvalidMoveException("Invalid rotation...");
        sendMessage(MessageType.MAKE_MOVE, String.valueOf(position), String.valueOf(rotation));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Move getMoveHint() {
        if (state != ClientState.IN_GAME) throw new WrongStateException("Client is not playing...");
        return observer.getBoardCopy().getPossibleMove();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void list() {
        sendMessage(MessageType.LIST_USERS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ping() {
        pingSent = System.nanoTime();
        sendMessage(MessageType.PING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(MessageType type, String... args) {
        try {
            String message = protocol.convert(type, args);
            System.out.printf("[OUTGOING] '%s'\n", message);
            out.println(message);
            out.flush();
        } catch (InvalidMessageException e) {
            System.out.printf("Invalid message of type %s...", type);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void handleMessage(String message) {
        try {
            Map<String, Object> data = protocol.process(message, false);
            MessageType messageType = (MessageType) data.get("type");
            switch (messageType) {
                case INIT:
                    onInit(data);
                    break;
                case LOGIN:
                    onLogin(data);
                    break;
                case ALREADY_LOGGED_IN:
                    onAlreadyLoggedIn(data);
                    break;
                case LIST_USERS:
                    onList(data);
                    break;
                case MAKE_MOVE:
                    onMove(data);
                    break;
                case NEW_GAME:
                    onNewGame(data);
                    break;
                case GAME_OVER:
                    onGameOver(data);
                    break;
                case PING:
                    sendMessage(MessageType.PONG);
                    break;
                case PONG:
                    onPong(data);
                    break;
                case QUIT:
                    close();
                    break;
                case ERROR:
                    break;
                default:
                    throw new InvalidMessageException();
            }
        } catch (InvalidMessageException | WrongStateException e) {
            sendMessage(MessageType.ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendMessage(MessageType.ERROR, "Illegal arguments");
        } catch (IOException e) {
            sendMessage(MessageType.ERROR, "Impossible happened");
        } finally {
            isWaitingMessage = false;
            notifyAll();
        }
    }

    /**
     * Respond to an 'init' message sent by the server.
     *
     * @param data message data
     */
    private void onInit(Map<String, Object> data) {
        if (state != ClientState.CONNECTING) throw new WrongStateException("Server is already initialized");

        // store server description
        serverDescription = (String) data.get("description");

        // store expansions supported by both sides
        expansions = new HashSet<>(Set.of((Expansion[]) data.get("expansions")));
        expansions.retainAll(Set.of(CLIENT_EXPANSIONS));

        // finish initialization process
        state = ClientState.NOT_LOGGED_IN;
    }

    /**
     * Respond to a 'login' message sent by the server.
     *
     * @param data message data
     */
    private void onLogin(Map<String, Object> data) {
        if (bufferedUsername != null) {
            this.username = bufferedUsername;
            bufferedUsername = null;
            state = ClientState.LOGGED_IN;
            System.out.println("Successfully logging in...");
        }
    }

    /**
     * Respond to an 'already logged in' message sent by the server.
     *
     * @param data message data
     */
    private void onAlreadyLoggedIn(Map<String, Object> data) {
        System.out.println("Client is already logged in...");
    }

    /**
     * Respond to a 'list' message sent by the server.
     *
     * @param data message data
     */
    private void onList(Map<String, Object> data) {
        String[] usernames = (String[]) data.get("users");
        System.out.println("Currently online users:");
        for (String username : usernames) {
            System.out.println(username);
        }
    }

    /**
     * Respond to a 'move' message sent by the server.
     *
     * @param data message data
     */
    private void onMove(Map<String, Object> data) {
        int position = (int) data.get("position");
        int rotation = (int) data.get("rotation");

        // display move on the board
        Board boardCopy = observer.getBoardCopy();
        var nextMove = new Move(position, rotation, boardCopy.getTurn());
        observer.onMove(nextMove);

        if (player != null) {
            if (player.getTurn() == boardCopy.getTurn() && !boardCopy.isGameOver()) {
                Move move = player.decideMove(boardCopy);
                playMove(move.getPosition(), move.getRotation());
            }
        }
    }

    /**
     * Respond to a 'new game' message sent by the server.
     *
     * @param data message data
     */
    private void onNewGame(Map<String, Object> data) throws IOException {
        if (state != ClientState.LOGGED_IN) throw new WrongStateException("Client cannot start new game");
        String[] players = (String[]) data.get("players");

        state = ClientState.IN_GAME;
        observer = new ClientBoardObserver();
        observer.onStart(players[0], players[1]);
        inQueue = false;

        if (player != null) {
            Marble firstTurn = observer.getBoardCopy().getTurn();
            if (username.equals(players[0])) {
                Move move = player.decideMove(observer.getBoardCopy());
                playMove(move.getPosition(), move.getRotation());
                player.setTurn(firstTurn);
            } else {
                player.setTurn(firstTurn.reverse());
            }
        }
    }

    /**
     * Respond to a 'game over' message sent by the server.
     *
     * @param data message data
     */
    private void onGameOver(Map<String, Object> data) {
        switch (PentagoProtocol.GAME_RESULTS.get((String) data.get("result"))) {
            case DISCONNECT:
                observer.onDisconnect((String) data.get("player"));
                break;
            case VICTORY:
                observer.onWinner((String) data.get("player"));
                break;
            case DRAW:
                observer.onDraw();
                break;
        }

        player = null;
        state = ClientState.LOGGED_IN;
        observer.onClose();
    }

    /**
     * Respond to a 'pong' message sent by the server.
     *
     * @param data message data
     */
    private void onPong(Map<String, Object> data) {
        if (pingSent != null) {
            long pongReceived = System.nanoTime();
            double elapsedTime = (double) (pongReceived - pingSent) / 1_000_000;
            System.out.printf("Elapsed time for '%s' is %.2f ms\n", serverDescription, elapsedTime);
            pingSent = null;
        }
    }

    /**
     * Get the current client state.
     *
     * @return client state
     */
    @Override
    public ClientState getState() {
        return state;
    }

    /**
     * Get an array of expansions supported by both client and server.
     *
     * @return supported expansions by both client and server
     */
    public Set<Expansion> getExpansions() {
        return expansions;
    }
}