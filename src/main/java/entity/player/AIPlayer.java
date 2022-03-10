package entity.player;

import entity.board.Board;
import entity.board.Move;
import entity.strategy.Strategy;

/**
 * Player that uses a provided strategy for deciding the next moves on the board.
 * Also, the AI player takes its name from the strategy that is being assigned to it.
 *
 * @author Aliaksei Kouzel
 */
public class AIPlayer extends Player {
    private final Strategy strategy;

    public AIPlayer(Strategy strategy) {
        super(strategy.getName());
        this.strategy = strategy;
    }

    /**
     * Decide the next move on the board using a provided strategy.
     *
     * @param board state of the board
     * @return next move decided using a provided strategy
     * @requires board != null && strategy != null
     */
    @Override
    public Move decideMove(Board board) {
        return strategy.decideMove(board);
    }
}