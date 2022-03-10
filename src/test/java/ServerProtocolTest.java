import entity.Expansion;
import entity.MessageType;
import entity.session.GameResult;
import exception.InvalidMessageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.PentagoProtocol;
import server.ServerProtocol;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Class that tests the pentago server protocol.
 * e.g. converting and processing messages and their arguments.
 *
 * @author Aliaksei Kouzel
 */
public class ServerProtocolTest {
    private ServerProtocol protocol;

    @BeforeEach
    void setUp() {
        protocol = new PentagoProtocol();
    }

    /**
     * Test converting the 'init' message sent by the server/client carrying the description and
     * the supported expansions.
     */
    @Test
    void convertInitMessage() {
        String e1 = protocol.valueOf(Expansion.AUTH);
        String e2 = protocol.valueOf(Expansion.CHAT);
        String message = protocol.convert(MessageType.INIT, "Test description", e1, e2);
        assertEquals("HELLO~Test description~AUTH~CHAT", message);
    }

    /**
     * Test converting the 'login' message sent by the client carrying the client's username.
     */
    @Test
    void convertLoginMessage() {
        String message = protocol.convert(MessageType.LOGIN, "Alex");
        assertEquals("LOGIN~Alex", message);
    }

    /**
     * Test converting the 'already logged in' message sent by the server.
     */
    @Test
    void convertAlreadyLoggedInMessage() {
        String message = protocol.convert(MessageType.ALREADY_LOGGED_IN);
        assertEquals("ALREADYLOGGEDIN", message);
    }

    /**
     * Test converting the 'list users' message sent by the server carrying the names of the online users.
     */
    @Test
    void convertListUsersMessage() {
        String message = protocol.convert(MessageType.LIST_USERS, "Alex", "Vlad", "Steve");
        assertEquals("LIST~Alex~Vlad~Steve", message);
    }

    /**
     * Test converting the 'join queue' message sent by the client to join/leave a pentago session.
     */
    @Test
    void convertJoinQueueMessage() {
        String message = protocol.convert(MessageType.JOIN_QUEUE);
        assertEquals("QUEUE", message);
    }

    /**
     * Test converting the 'new game' message sent by the server carrying the names of the players that
     * participate in the game.
     */
    @Test
    void convertNewGameMessage() {
        String message = protocol.convert(MessageType.NEW_GAME, "Alex", "Vlad");
        assertEquals("NEWGAME~Alex~Vlad", message);
    }

    /**
     * Test converting the 'game over' message sent by the server carrying the game result and optionally
     * the name of the referring player (if case of player disconnection or victory).
     */
    @Test
    void convertGameOverMessage() {
        String result = protocol.valueOf(GameResult.DISCONNECT);
        String message = protocol.convert(MessageType.GAME_OVER, result, "Steve");
        assertEquals("GAMEOVER~DISCONNECT~Steve", message);
    }

    /**
     * Test converting the 'make move' message sent by the client carrying the move details.
     */
    @Test
    void convertMakeMoveMessage() {
        String message = protocol.convert(MessageType.MAKE_MOVE, "31", "7");
        assertEquals("MOVE~31~7", message);
    }

    /**
     * Test converting the 'ping' message sent by either the client or the server to check the connection.
     */
    @Test
    void convertPingMessage() {
        String message = protocol.convert(MessageType.PING);
        assertEquals("PING", message);
    }

    /**
     * Test converting the 'pong' message sent by either the client or the server to respond to the ping message.
     */
    @Test
    void convertPongMessage() {
        String message = protocol.convert(MessageType.PONG);
        assertEquals("PONG", message);
    }

    /**
     * Test converting the 'quit' message sent by either the client ot the server to stop the connection.
     */
    @Test
    void convertQuitMessage() {
        String message = protocol.convert(MessageType.QUIT);
        assertEquals("QUIT", message);
    }

    /**
     * Test converting the error message carrying the error description.
     */
    @Test
    void convertErrorMessage() {
        String message = protocol.convert(MessageType.ERROR, "Error message");
        assertEquals("ERROR~Error message", message);
    }

    /**
     * Test processing the 'init' message by retrieving the client's/server's description and
     * the supported expansions.
     */
    @Test
    void processInitMessage() {
        Map<String, Object> args = protocol.process("HELLO~Test description~AUTH", true);
        assertEquals(MessageType.INIT, args.get("type"));
        assertEquals("Test description", args.get("description"));
        Expansion[] expansions = (Expansion[]) args.get("expansions");
        assertEquals(Expansion.AUTH, expansions[0]);
    }

    /**
     * Test failing to process the 'init' message by specifying the invalid expansion.
     */
    @Test
    void failProcessInitMessageGivenInvalidExpansion() {
        assertThrows(IllegalArgumentException.class, () -> protocol.process("HELLO~Test description~arg", true));
    }

    /**
     * Test processing the 'login' message by retrieving the username of the client.
     */
    @Test
    void processLoginMessage() {
        Map<String, Object> args = protocol.process("LOGIN~Alex", true);
        assertEquals(MessageType.LOGIN, args.get("type"));
        assertEquals("Alex", args.get("username"));
    }

    /**
     * Test failing to process the 'login' message by giving the out or range number of arguments.
     */
    @Test
    void failProcessLoginMessageGivenOutOfRangeArgs() {
        assertThrows(IllegalArgumentException.class, () -> protocol.process("LOGIN~arg1~arg2", true));
    }

    /**
     * Test processing the 'already logged in' message sent by the server.
     */
    @Test
    void processAlreadyLoggedInMessage() {
        Map<String, Object> args = protocol.process("ALREADYLOGGEDIN", true);
        assertEquals(MessageType.ALREADY_LOGGED_IN, args.get("type"));
    }

    /**
     * Test failing to process the 'already logged in' message by providing the invalid arguments.
     */
    @Test
    void failProcessAlreadyLoggedInMessageGivenInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> protocol.process("ALREADYLOGGEDIN~arg1~arg2", true));
    }

    /**
     * Test processing the 'list users' message by retrieving the names of the online users on the server.
     */
    @Test
    void processListUsersMessage() {
        Map<String, Object> args = protocol.process("LIST~Alex~Vlad", false);
        assertEquals(MessageType.LIST_USERS, args.get("type"));
        String[] users = (String[]) args.get("users");
        assertEquals("Alex", users[0]);
        assertEquals("Vlad", users[1]);
    }

    /**
     * Test failing to process the message by providing the invalid message type.
     */
    @Test
    void failProcessMessageGivenInvalidType() {
        assertThrows(InvalidMessageException.class, () -> protocol.process("HELLLO", true));
    }
}
