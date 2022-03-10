package server;

import client.ClientHandler;
import client.PentagoClientHandler;
import entity.Expansion;
import entity.player.Player;
import entity.session.GameSession;
import entity.session.PentagoSession;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Network server that allows its clients to play pentago games with each other.
 *
 * @author Aliaksei Kouzel
 */
public class PentagoServer implements NetworkServer {
    public static final Set<Expansion> EXPANSIONS = Set.of();
    private final List<ClientHandler> playQueue = new LinkedList<>();
    private final List<ClientHandler> clients = new LinkedList<>();
    private final List<GameSession> sessions = new LinkedList<>();
    private final ServerProtocol protocol = new PentagoProtocol();
    private final String description;
    private ServerSocket server;
    private Thread serverThread;

    public PentagoServer(String description) {
        this.description = description;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket client = server.accept();
                ClientHandler clientHandler = new PentagoClientHandler(client, this);
                new Thread(clientHandler).start();
                clients.add(clientHandler);
            } catch (IOException e) {
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean start(int port) {
        try {
            server = new ServerSocket(port);
            serverThread = new Thread(this);
            serverThread.start();
            System.out.printf("Starting server on port %d...\n", getPort());
            return true;
        } catch (IOException e) {
            System.out.println("Port is already in use...");
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid port...");
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        try {
            sessions.forEach(GameSession::close);
            clients.forEach(ClientHandler::close);
            server.close();
            serverThread.join();
        } catch (InterruptedException | IOException ignored) {
        } finally {
            System.out.println("Closing server...");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ping() {
        if (clients.size() == 0) {
            System.out.println("There are no online users...");
        } else {
            for (ClientHandler client : clients) {
                client.ping();
            }
        }
    }

    /**
     * Join a client into the queue to play a pentago game. If the client is already in the queue, he/she
     * leaves the queue instead. Also, the pentago session starts if the queue contains more than two clients.
     *
     * @param client client that wants to join/leave the queue to play a pentago game.
     */
    public synchronized void joinQueue(ClientHandler client) {
        if (playQueue.contains(client)) {
            // leave the queue
            playQueue.remove(client);
        } else {
            // join the queue
            playQueue.add(client);

            // start session if enough players
            if (playQueue.size() >= 2) {
                ClientHandler p1 = playQueue.get(0);
                ClientHandler p2 = playQueue.get(1);
                startSession(p1, p2);

                // leave the queue
                playQueue.remove(p1);
                playQueue.remove(p2);
            }
        }
    }

    /**
     * Start a pentago session with provided clients. If one of the clients could not join the session,
     * the session is not being started and clients remain in the queue.
     *
     * @param clients clients that are joining the session
     * @return true if the pentago session is successfully started
     */
    private synchronized boolean startSession(ClientHandler... clients) {
        GameSession session = new PentagoSession();

        // ask clients to join the session
        for (ClientHandler client : clients) {
            boolean isJoined = client.joinSession(session);
            if (!isJoined) return false;
        }

        // if everybody is joined start the session
        session.start();
        sessions.add(session);
        return true;
    }

    /**
     * Handle a client leaving the server.
     *
     * @param client leaving client
     */
    public synchronized void leave(ClientHandler client) {
        Player player = client.getPlayer();
        for (GameSession session : sessions) {
            if (session.isRunning() && session.hasPlayer(player)) {
                session.disconnect(player);
            }
        }
        clients.remove(client);
        playQueue.remove(client);
    }

    /**
     * Get an array of clients currently logged into the server.
     *
     * @return an array of clients currently logged into the server
     */
    public synchronized String[] getOnlineUsers() {
        List<String> usernames = new ArrayList<>();
        for (ClientHandler client : clients) {
            String username = client.getUsername();
            if (username != null) {
                usernames.add(username);
            }
        }
        return usernames.toArray(new String[0]);
    }

    /**
     * Handle a client leaving the pentago session. If the session is currently running,
     * it gets notified about the leaving player.
     *
     * @param session running game session
     * @param player  player that leaves the session
     */
    public synchronized void leaveSession(GameSession session, Player player) {
        if (session.hasPlayer(player)) {
            if (session.isRunning()) {
                session.disconnect(player);
            }
            sessions.remove(session);
        }
    }

    /**
     * Get a server description.
     *
     * @return server description
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return server.getLocalPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerProtocol getProtocol() {
        return protocol;
    }
}
