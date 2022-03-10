package entity.board;

/**
 * Class that represents a move made in the pentago game. It contains the details of the field position
 * and the subboard rotation that follows after. Also, it contains the marble that is being played.
 *
 * @author Aliaksei Kouzel
 */
public class Move {
    private final int position;
    private final int rotation;
    private Marble marble;

    public Move(int position, int rotation, Marble marble) {
        this.position = position;
        this.rotation = rotation;
        this.marble = marble;
    }

    public Move(int position, int rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Get a field position index on the board.
     *
     * @return field position index
     */
    public int getPosition() {
        return position;
    }

    /**
     * Get a subboard rotation index.
     *
     * @return rotation index
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * Get the move marble.
     *
     * @return move marble
     */
    public Marble getMarble() {
        return marble;
    }
}
