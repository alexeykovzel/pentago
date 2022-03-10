package entity.board.observer;

import entity.board.Board;
import entity.board.Marble;
import entity.board.Move;
import entity.player.Player;

/**
 * Class that represents a board observer, which handles board updates on the client side. It also contains
 * a copy of the board, as it might be initialized on the server side and not visible by the client.
 *
 * @author Aliaksei Kouzel
 */
public class ClientBoardObserver implements BoardObserver {
    private Board boardCopy;
    private String[] players;

    public void updateBoard() {
        System.out.printf("Current field positions:\n\n%s\n", boardCopy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMove(Move move) {
        String player = players[getPlayerIndex(move.getMarble())];
        System.out.printf("Player (%s) '%s' made his move\n", move.getMarble().display, player);
        boardCopy.playMove(move);
        updateBoard();

        if (!boardCopy.isGameOver()) {
            updateTurn();
        }
    }

    /**
     * Notify a player about who moves next.
     */
    private void updateTurn() {
        Marble playerMarble = boardCopy.getTurn();
        int playerIndex = getPlayerIndex(playerMarble);
        System.out.printf("Player (%s) '%s' moves next\n", playerMarble.display, players[playerIndex]);
        System.out.println("Hint: use 'hint' for a possible move\n");
    }

    /**
     * Return the index that is being assigned to a specific player.
     *
     * @param playerMarble player marble
     * @return player index
     */
    private int getPlayerIndex(Marble playerMarble) {
        return playerMarble == Marble.BLACK ? 0 : 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onWinner(String player) {
        System.out.printf("Player '%s' has won!\n", player);
        boardCopy.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDraw() {
        System.out.println("This is a draw");
        boardCopy.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisconnect(String player) {
        System.out.printf("Player '%s' has disconnected\n", player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClose() {
        System.out.println("Closing session...");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart(String... players) {
        this.players = players;
        System.out.println("-".repeat(90) + "\nWelcome to Pentago! Here, you should achieve a streak of 5 marbles (either diagonally,\n" + "vertically or horizontally) to win the game. Each player's move consists of placing a\n" + "marble on one of the 36 fields and then rotating one of the subboards. Good luck!\n" + "-".repeat(90));

        System.out.printf("Today's players: %s (%s) vs %s (%s)\n", players[0], Marble.BLACK.display, players[1], Marble.WHITE.display);

        boardCopy = new Board();
        updateBoard();
        updateTurn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board getBoardCopy() {
        return boardCopy;
    }
}
