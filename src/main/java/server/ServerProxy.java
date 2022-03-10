package server;

import client.GameClient;
import client.PentagoClient;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Class that helps in retrieving textual data from the user.
 * Also, it helps in executing different repetitive operations (e.g. connecting to server).
 *
 * @author Aliaksei Kouzel
 */
public class ServerProxy {
    private final BufferedReader in;
    private final PrintWriter out;

    public ServerProxy(InputStream in, OutputStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = new PrintWriter(out, true);
    }

    /**
     * Repeat asking the user to input the inet address until it is valid.
     *
     * @return inet address retrieved by the user
     * @throws IOException in case the input stream is closed
     */
    public InetAddress askInetAddress() throws IOException {
        while (true) {
            out.print("Server address: ");
            out.flush();
            try {
                return InetAddress.getByName(in.readLine());
            } catch (UnknownHostException e) {
                out.println("Invalid host address. Try again");
            }
        }
    }

    /**
     * Repeat asking the user to input the server port until it is valid.
     *
     * @return server port retrieved by the user
     * @throws IOException in case the input stream is closed
     */
    public int askPort() throws IOException {
        while (true) {
            try {
                out.print("Server port: ");
                out.flush();
                int port = Integer.parseInt(in.readLine());

                // check the validity of the given port
                if (port > 0 && port < 1024) {
                    throw new IllegalArgumentException("Port is already in use");
                } else if (port < 0 || port > 65536) {
                    throw new IllegalArgumentException("Port is out of range");
                }

                return port;
            } catch (NumberFormatException e) {
                out.println("Invalid port. Try again");
            } catch (IllegalArgumentException e) {
                out.println(e.getMessage() + ". Try again");
            }
        }
    }

    /**
     * Repeat trying to connect the client to the server until successfully connected.
     *
     * @param client client trying to connect
     * @throws IOException if the input stream is interrupted
     */
    public void tryConnect(GameClient client) throws IOException {
        while (true) {
            InetAddress address = askInetAddress();
            int port = askPort();

            // try to connect to the server
            boolean isConnected = client.connect(address, port);
            if (isConnected) break;

            // try again if failed to connect
            out.println("Could not connect to server. Try again");
        }
    }

    /**
     * Repeat trying to log in the client to the server until successfully logged in.
     *
     * @param client client trying to log in
     * @throws IOException in case the input stream is closed
     */
    public void tryLogin(GameClient client) throws IOException {
        while (true) {
            out.print("Please enter a username: ");
            out.flush();

            // try to log in
            boolean logged = client.login(in.readLine());
            if (logged) break;

            out.println("Could not log in to server. Try again");
        }
    }
}
