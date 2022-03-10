package exception;

/**
 * Throws an exception if the provided username is unavailable.
 * e.g. if the server does not accept clients with the same username and somebody tries
 * to log in using the username that is already used by another client.
 *
 * @author Aliaksei Kouzel
 */
public class UnavailableUsernameException extends IllegalArgumentException {
    public UnavailableUsernameException(String message) {
        super(message);
    }
}
