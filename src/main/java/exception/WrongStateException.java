package exception;

/**
 * Throws an exception if the current state is invalid.
 * e.g. if the client tries to play a game but did not log in yet.
 *
 * @author Aliaksei Kouzel
 */
public class WrongStateException extends IllegalArgumentException {
    public WrongStateException(String message) {
        super(message);
    }
}
