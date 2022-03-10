package entity.board.observer;

import client.ClientHandler;
import entity.board.Board;
import entity.board.Marble;
import entity.session.GameResult;
import entity.MessageType;
import entity.board.Move;
import server.NetworkServer;
import server.ServerProtocol;

import java.io.*;

/**
 * Class that represents a board observer, which handles board changes on the server side. It contains a protocol,
 * as the board updates are automatically transmitted to the client using a provided protocol.
 *
 * @author Aliaksei Kouzel
 */
public class ServerBoardObserver implements BoardObserver {
    private final ServerProtocol protocol;
    private final ClientHandler client;

    public ServerBoardObserver(ClientHandler client) {
        this.protocol = client.getProtocol();
        this.client = client;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart(String... players) {
        client.sendMessage(MessageType.NEW_GAME, players[0], players[1]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMove(Move move) {
        String position = String.valueOf(move.getPosition());
        String rotation = String.valueOf(move.getRotation());
        client.sendMessage(MessageType.MAKE_MOVE, position, rotation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onWinner(String winner) {
        String result = protocol.valueOf(GameResult.VICTORY);
        client.sendMessage(MessageType.GAME_OVER, result, winner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDraw() {
        String result = protocol.valueOf(GameResult.DRAW);
        client.sendMessage(MessageType.GAME_OVER, result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisconnect(String player) {
        String result = protocol.valueOf(GameResult.DISCONNECT);
        client.sendMessage(MessageType.GAME_OVER, result, player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClose() {
        client.leaveSession();
    }

    /**
     * Returns nothing as the server does not keep the copy of the board.
     *
     * @return null
     */
    @Override
    public Board getBoardCopy() {
        return null;
    }
}
