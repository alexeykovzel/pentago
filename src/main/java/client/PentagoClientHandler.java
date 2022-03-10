package client;

import entity.board.Move;
import exception.WrongStateException;
import exception.UnavailableUsernameException;
import entity.ClientState;
import entity.Expansion;
import entity.board.observer.ServerBoardObserver;
import entity.player.BufferedPlayer;
import entity.player.Player;
import exception.InvalidMessageException;
import entity.MessageType;
import entity.session.GameSession;
import server.PentagoServer;
import server.ServerProtocol;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Class that represents a client handler that communicates with a pentago server client.
 *
 * @author Aliaksei Kouzel
 */
public class PentagoClientHandler implements ClientHandler {
    private final String serverDescription;
    private final ServerProtocol protocol;
    private final PentagoServer server;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Socket client;
    private String clientDescription;
    private Set<Expansion> expansions;
    private ClientState clientState;
    private BufferedPlayer player;
    private GameSession session;
    private String username;
    private Long pingSent;

    public PentagoClientHandler(Socket client, PentagoServer server) throws IOException {
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
        serverDescription = server.getDescription();
        protocol = server.getProtocol();
        this.server = server;
        this.client = client;
        clientState = ClientState.CONNECTING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean joinSession(GameSession session) {
        player = new BufferedPlayer(username);
        if (!session.join(player)) {
            sendMessage(MessageType.ERROR, "Failed to join session...");
            return false;
        }

        // observer the game board
        var observer = new ServerBoardObserver(this);
        session.addObserver(observer);

        clientState = ClientState.IN_GAME;
        this.session = session;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void leaveSession() {
        clientState = ClientState.LOGGED_IN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void ping() {
        if (pingSent != null) {
            System.out.println("Ping is already sent...");
        } else {
            pingSent = System.nanoTime();
            sendMessage(MessageType.PING);
        }
    }

    /**
     * {@inheritDoc}
     */
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
        } finally {
//            if (session != null) leaveSession();
            server.leave(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            /* Should not be thrown */
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerProtocol getProtocol() {
        return protocol;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void handleMessage(String message) {
        try {
            Map<String, Object> data = protocol.process(message, true);
            MessageType type = (MessageType) data.get("type");
            switch (type) {
                case INIT:
                    onInit(data);
                    break;
                case LOGIN:
                    onLogin(data);
                    break;
                case LIST_USERS:
                    onList(data);
                    break;
                case JOIN_QUEUE:
                    onQueue(data);
                    break;
                case MAKE_MOVE:
                    onMove(data);
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
            }
        } catch (WrongStateException | IOException | UnavailableUsernameException e) {
            sendMessage(MessageType.ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendMessage(MessageType.ERROR, "Illegal arguments");
        }
    }

    /**
     * Respond to an 'init' message sent by the client.
     *
     * @param data message data
     */
    private void onInit(Map<String, Object> data) {
        if (clientState != ClientState.CONNECTING) throw new WrongStateException("Client is already initialized");

        // store client description
        this.clientDescription = (String) data.get("description");

        // store expansions supported by both sides
        expansions = new HashSet<>(Set.of((Expansion[]) data.get("expansions")));
        expansions.retainAll(PentagoServer.EXPANSIONS);

        // construct a response message
        int i = 1;
        var args = new String[PentagoServer.EXPANSIONS.size() + 1];
        args[0] = serverDescription;
        for (Expansion expansion : PentagoServer.EXPANSIONS) {
            args[i] = protocol.valueOf(expansion);
            i++;
        }

        // initialize client
        clientState = ClientState.NOT_LOGGED_IN;
        sendMessage(MessageType.INIT, args);
    }

    /**
     * Respond to a 'login' message sent by the client.
     *
     * @param data message data
     */
    private void onLogin(Map<String, Object> data) throws IOException, UnavailableUsernameException {
        if (clientState == ClientState.CONNECTING) {
            throw new WrongStateException("Client is not initialized...");
        }

        if (clientState != ClientState.NOT_LOGGED_IN) {
            sendMessage(MessageType.ALREADY_LOGGED_IN);
        } else {
            String username = (String) data.get("username");
            if (username.contains(protocol.getDelimiter())) {
                throw new UnavailableUsernameException("Username contradicts server protocol");
            }
            for (String user : server.getOnlineUsers()) {
                if (user.equals(username)) {
                    throw new UnavailableUsernameException("This username is not available...");
                }
            }
            this.username = username;
            clientState = ClientState.LOGGED_IN;
            sendMessage(MessageType.LOGIN);
            System.out.println(username + " has logged in...");
        }
    }

    /**
     * Respond to a 'list' message sent by the client.
     *
     * @param data message data
     */
    private void onList(Map<String, Object> data) {
        if (clientState == ClientState.CONNECTING) throw new WrongStateException("Client is not initialized...");
        if (clientState == ClientState.NOT_LOGGED_IN) throw new WrongStateException("Client is not logged in...");
        String[] users = server.getOnlineUsers();
        sendMessage(MessageType.LIST_USERS, users);
    }

    /**
     * Respond to a 'queue' message sent by the client.
     *
     * @param data message data
     */
    private void onQueue(Map<String, Object> data) {
        if (clientState == ClientState.CONNECTING) throw new WrongStateException("Client is not initialized...");
        if (clientState == ClientState.NOT_LOGGED_IN) throw new WrongStateException("Client is not logged in...");
        if (session != null && session.isRunning()) throw new WrongStateException("Client is already playing...");
        server.joinQueue(this);
    }

    /**
     * Respond to a 'move' message sent by the client.
     *
     * @param data message data
     */
    private void onMove(Map<String, Object> data) {
        if (session != null && session.isRunning()) {
            if (session.getTurn() != player.getTurn()) {
                sendMessage(MessageType.ERROR, "This is not your turn...");
            } else {
                int position = (int) data.get("position");
                int rotation = (int) data.get("rotation");
                Move nextMove = new Move(position, rotation, player.getTurn());

                if (session.isValidMove(nextMove)) {
                    player.storeMove(nextMove);
                } else {
                    sendMessage(MessageType.ERROR, "Illegal move. Try again");
                }
            }
        }
    }

    /**
     * Respond to a 'pong' message sent by the client.
     *
     * @param data message data
     */
    private void onPong(Map<String, Object> data) {
        if (pingSent != null) {
            long pongReceived = System.nanoTime();
            double elapsedTime = (double) (pongReceived - pingSent) / 1_000_000;
            System.out.printf("Elapsed time for '%s' is %.2f ms\n", clientDescription, elapsedTime);
            pingSent = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void sendMessage(MessageType type, String... args) {
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
    public String getUsername() {
        return username;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Get a set of supported expansions by both client and server.
     *
     * @return supported expansions by both client and server
     */
    public Set<Expansion> getExpansions() {
        return expansions;
    }
}
