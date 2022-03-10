package entity.strategy;

import entity.board.Board;
import entity.board.Marble;
import entity.board.Move;

import java.util.ArrayList;
import java.util.List;

/**
 * This pentago strategy is based on pure luck. All moves are determined randomly.
 *
 * @author Aliaksei Kouzel
 */
public class NaiveStrategy implements Strategy {

    @Override
    public String getName() {
        return "native-strategy";
    }

    /**
     * Decide the next move by randomly picking an empty field on the board and the random subboard rotation.
     *
     * @param board state of the board
     * @return the random possible move
     * @requires board != null && board.getPossibleMove() != null
     */
    @Override
    public Move decideMove(Board board) {
        return board.getPossibleMove();
    }
}
