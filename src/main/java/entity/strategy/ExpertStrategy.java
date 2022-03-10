package entity.strategy;

import entity.board.Board;
import entity.board.Marble;
import entity.board.Move;

/**
 * This pentago strategy thinks one move ahead. It understands if the next move is winning or loosing
 * for the player and makes corresponding moves.
 *
 * @author Aliaksei Kouzel
 */
public class ExpertStrategy implements Strategy {
    @Override
    public String getName() {
        return "expert-strategy";
    }

    /**
     * Decide the next move by thinking one move ahead.
     *
     * @param board state of the board
     * @return the next move made by the AI player
     * @requires board != null
     */
    @Override
    public Move decideMove(Board board) {
        Marble turn = board.getTurn();

        // looks for the winning move
        Move winningMove = getWinningMove(board, turn);
        if (winningMove != null) return winningMove;

        // looks for the loosing move
        Move loosingMove = getWinningMove(board, turn.reverse());
        if (loosingMove != null) return loosingMove;

        // returns a random possible move
        return board.getPossibleMove();
    }

    /**
     * Return the next winning move given the state of the board (if exists).
     *
     * @param board state of the board
     * @param turn  turn of the player
     * @return the next winning move, null if there is none
     */
    private Move getWinningMove(Board board, Marble turn) {
        for (int position : board.getIndexesOfEmptyFields()) {
            for (int rotation = 0; rotation < 8; rotation++) {
                Move nextMove = new Move(position, rotation, turn);
                if (isWinningMove(board, nextMove)) {
                    return nextMove;
                }
            }
        }
        return null;
    }

    /**
     * Decide if the provided move is winning given the state of the board.
     *
     * @param board state of the board
     * @param move  move that is being checked
     * @return true if the provided move is winning
     */
    private boolean isWinningMove(Board board, Move move) {
        Board boardCopy = board.deepCopy();
        boardCopy.playMove(move);
        return boardCopy.isWinner(board.getTurn());
    }
}
