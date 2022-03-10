package entity.session;

import entity.board.Marble;
import entity.board.observer.BoardObserver;
import entity.board.Board;
import entity.board.Move;
import entity.player.Player;

import java.util.*;

/**
 * Class that is used to play a pentago session simultaneously with other sessions.
 *
 * @author Aliaksei Kouzel
 */
public class PentagoSession implements GameSession {
    private final Marble[] marbles = new Marble[]{Marble.BLACK, Marble.WHITE};
    private final List<BoardObserver> observers = new ArrayList<>();
    private final Map<Marble, Player> players = new HashMap<>();
    private final Board board = new Board();
    private boolean isRunning = true;
    private Thread session;

    public PentagoSession(Player... players) {
        for (Player player : players) {
            join(player);
        }
    }

    @Override
    public void run() {
        try {
            greetPlayers();
            while (isRunning) handleMove();
        } catch (NullPointerException ignored) { // if a player disconnects during greeting
        } finally {
            for (BoardObserver observer : observers) {
                observer.onClose();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Thread start() {
        session = new Thread(this);
        session.start();
        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean join(Player player) {
        int freeTurn = getFreeTurn();
        return join(player, freeTurn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean join(Player player, int turn) {
        if (player.getUsername() == null) return false;
        if (!isFreeTurn(turn)) return false;

        Marble freeMarble = marbles[turn];
        player.setTurn(freeMarble);
        players.put(freeMarble, player);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(Player player) {
        for (BoardObserver observer : observers) {
            observer.onDisconnect(player.getUsername());
        }
        close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        isRunning = false;
    }

    /**
     * Handle the next move that should be made by the current player.
     */
    private void handleMove() {
        Player currentPlayer = players.get(board.getTurn());
        Move nextMove = currentPlayer.decideMove(board);

        // null means that the player has disconnected
        if (nextMove == null) {
            disconnect(currentPlayer);
        } else if (isValidMove(nextMove)) {
            playMove(nextMove);
        }
    }

    /**
     * Play the next move. After that, check if the game is over and if so stop the session.
     *
     * @param move the next move that is being played
     */
    private void playMove(Move move) {
        board.playMove(move);
        for (BoardObserver observer : observers) {
            observer.onMove(move);
        }

        // check if the game is over
        if (board.isGameOver()) {
            isRunning = false;
            announceResults();
        }
    }

    /**
     * Returns the next available turn.
     *
     * @return free turn or -1 if there is none
     */
    private int getFreeTurn() {
        for (int i = 0; i < marbles.length; i++) {
            if (!players.containsKey(marbles[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Verify if the provided turn is available.
     *
     * @param turn turn that is being verified
     * @return true if the turn is available
     */
    private boolean isFreeTurn(int turn) {
        if (turn == -1) return false;
        return !players.containsKey(marbles[turn]);
    }

    /**
     * Greet players that are being joined into the session.
     */
    private void greetPlayers() {
        for (BoardObserver observer : observers) {
            String p1 = players.get(Marble.BLACK).getUsername();
            String p2 = players.get(Marble.WHITE).getUsername();
            observer.onStart(p1, p2);
        }
    }

    /**
     * Announce the game results if there is a draw or the victory by one of the players.
     */
    private void announceResults() {
        if (board.isFull()) {
            for (BoardObserver observer : observers) {
                observer.onDraw();
            }
        } else {
            Marble winningMarble = board.isWinner(Marble.BLACK) ? Marble.BLACK : Marble.WHITE;
            for (BoardObserver observer : observers) {
                observer.onWinner(players.get(winningMarble).getUsername());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addObserver(BoardObserver observer) {
        observers.add(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidMove(Move move) {
        return board.isValidMove(move);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPlayer(Player player) {
        return players.containsValue(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Marble getTurn() {
        return board.getTurn();
    }
}
