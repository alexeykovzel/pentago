package entity.board;

/**
 * Class that represents a mark in the Pentago game.
 * There are three possible values: BLACK, WHITE and EMPTY.
 *
 * @author Aliaksei Kouzel
 */
public enum Marble {
    BLACK("□"), WHITE("■"), EMPTY("");

    public final String display;

    Marble(String display) {
        this.display = display;
    }

    /**
     * Get the opposite mark if it is 'BLACK' or 'WHITE', otherwise 'EMPTY'.
     *
     * @return the opposite mark.
     * @ensures (this = = BLACK = = > \ result = = WHITE) && (this == WHITE ==> \result == BLACK);
     */
    public Marble reverse() {
        if (this == BLACK) return WHITE;
        if (this == WHITE) return BLACK;
        return EMPTY;
    }
}
