package server;

/**
 * Network server that can be started on a specific port.
 * Also, it can communicate with its clients by using a shared protocol.
 *
 * @author Aliaksei Kouzel
 */
public interface NetworkServer extends Runnable {
    /**
     * Start a server using the provided port.
     *
     * @return true if the server started successfully
     */
    boolean start(int port);

    /**
     * Stop the server.
     */
    void stop();

    /**
     * Get a port on which the server has been started.
     *
     * @return server port
     */
    int getPort();

    /**
     * Check the connection with the users currently logged into the server.
     */
    void ping();

    /**
     * Get a server protocol that is being used to for the server-client communication.
     *
     * @return server protocol
     */
    ServerProtocol getProtocol();
}
