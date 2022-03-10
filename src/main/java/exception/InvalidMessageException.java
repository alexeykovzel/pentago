package exception;

/**
 * Throws an exception if the client message to the server is invalid.
 * e.g. if there is an invalid number of args, or the args are of invalid format.
 *
 * @author Aliaksei Kouzel
 */
public class InvalidMessageException extends IllegalArgumentException {
    public InvalidMessageException() {
        super("Invalid message");
    }
}