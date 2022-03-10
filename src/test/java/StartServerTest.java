import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.PentagoServer;
import server.NetworkServer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class that tests starting a server on a provided port.
 *
 * @author Aliaksei Kouzel
 */
public class StartServerTest {
    private NetworkServer server;

    @BeforeEach
    void setUp() {
        server = new PentagoServer("Test server");
    }

    /**
     * Test starting a server by providing a valid port.
     */
    @Test
    void startServerGivenValidPort() {
        boolean isStarted = server.start(10000);
        assertTrue(isStarted);
    }

    /**
     * Test failing to start a server by providing a port that is either reserved or taken by another server.
     */
    @Test
    void failStartServerGivenTakenPort() {
        boolean isStarted = server.start(1);
        assertFalse(isStarted);
    }

    /**
     * Test failing to start a server by providing an invalid port.
     */
    @Test
    void failStartServerGivenInvalidPort() {
        boolean isStarted = server.start(-10);
        assertFalse(isStarted);
    }

    /**
     * Test failing to start a server by providing a port out of range.
     */
    @Test
    void failStartServerGivenOutOfRangePort() {
        boolean isStarted = server.start(100000);
        assertFalse(isStarted);
    }
}
