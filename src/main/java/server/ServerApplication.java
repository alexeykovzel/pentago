package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Class that starts a server on the port provided by the user.
 * Also, it handles other textual input from the user after the server has been started.
 *
 * @author Aliaksei Kouzel
 */
public class ServerApplication {
    public static void main(String[] args) throws IOException {
        NetworkServer server = new PentagoServer("Pentago server");
        var in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            int port = new ServerProxy(System.in, System.out).askPort();

            // start server on the given port
            if (server.start(port)) {
                System.out.println(getHint());

                // listen to user input
                String line;
                while ((line = in.readLine()) != null) {
                    switch (line) {
                        case "help":
                            System.out.println(getHint());
                            break;
                        case "ping":
                            server.ping();
                            break;
                        case "quit":
                            server.stop();
                            System.exit(0);
                        default:
                            System.out.println("Invalid operation");
                    }
                }
            }
        }
    }

    /**
     * Get a manual on possible operations that are available to the user for utilizing the server.
     *
     * @return manual of operations to utilize the server
     */
    public static String getHint() {
        return "Usage: <operation> [...]\n\n" +
                "ping           check the connection with currently online users\n" +
                "quit           stop the server\n\n" +
                "Use 'help' for available options";
    }
}