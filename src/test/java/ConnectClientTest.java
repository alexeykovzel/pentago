import client.GameClient;
import client.PentagoClient;
import entity.ClientState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.PentagoServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class that tests clients connecting and logging to the server.
 *
 * @author Aliaksei Kouzel
 */
public class ConnectClientTest {
    private static final int PORT = 8080;
    private InetAddress host;

    @BeforeEach
    void setUp() throws UnknownHostException {
        new PentagoServer("Test server").start(PORT);
        host = InetAddress.getByName("localhost");
    }

    /**
     * Test connecting a single client to the server.
     */
    @Test
    void connectSingleClient() {
        var client = new PentagoClient("Test client");
        boolean connected = client.connect(host, PORT);
        assertTrue(connected);
    }

    /**
     * Test the client status upon connecting and initialing on the server.
     */
    @Test
    void initializeSingleClient() {
        var client = new PentagoClient("Test client");
        client.connect(host, PORT);
        assertEquals(ClientState.NOT_LOGGED_IN, client.getState());
    }

    /**
     * Test logging an already connected client to the server.
     */
    @Test
    void loginSingleClient() {
        var client = new PentagoClient("Test client");
        client.connect(host, PORT);
        client.login("Test");
        assertEquals(ClientState.LOGGED_IN, client.getState());
    }

    /**
     * Test logging six already connected clients to the server.
     */
    @Test
    void loginSixClients() {
        List<GameClient> clients = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            var client = new PentagoClient("Test client");
            clients.add(client);
            client.connect(host, PORT);
            client.login("Test" + i);
        }

        for (var client : clients) {
            assertEquals(ClientState.LOGGED_IN, client.getState());
        }
    }
}
