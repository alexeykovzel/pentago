package exception;

/**
 * Throws an exception if the provided move is invalid.
 * e.g. if trying to place a marble over another marble.
 *
 * @author Aliaksei Kouzel
 */
public class InvalidMoveException extends IllegalArgumentException {
    public InvalidMoveException(String message) {
        super(message);
    }
}
