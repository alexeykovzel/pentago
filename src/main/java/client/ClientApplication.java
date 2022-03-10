package client;

import entity.board.Move;
import entity.strategy.ExpertStrategy;
import entity.strategy.NaiveStrategy;
import server.ServerProxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Class that starts and connects the client to the pentago server by the provided host address and the port.
 * Also, it listens to other textual input from the user to utilize the client.
 *
 * @author Aliaksei Kouzel
 */
public class ClientApplication {
    private static GameClient client;

    public static void main(String[] args) {
        client = new PentagoClient("Local client");
        var in = new BufferedReader(new InputStreamReader(System.in));

        try {
            var proxy = new ServerProxy(System.in, System.out);
            proxy.tryConnect(client);
            proxy.tryLogin(client);
            Operation.HELP.execute();

            // listen to user input
            String line;
            while ((line = in.readLine()) != null) {
                String[] operationArgs = line.split(" ");
                Operation operation = Operation.byValue(operationArgs[0]);
                if (operation != null) {
                    try {
                        operation.execute(operationArgs);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("Invalid operation");
                }
            }
        } catch (IOException ignored) {
            /* Should not be thrown */
        }
    }

    private enum Operation {
        HELP("help") {
            @Override
            void execute(String[] args) {
                String hint = "Usage : <operation> [...]\n\n" +
                        "list                           List currently online users on the server\n" +
                        "ping                           Check the connection to the server\n" +
                        "quit                           Disconnect from the server\n" +
                        "hint                           Get a hint during the game\n" +
                        "move {position} {rotation}     Make a move during the game\n" +
                        "play {difficulty level}        Play a pentago game. If you want an AI to play instead of you,\n" +
                        "                               enter one of the following difficulty levels as a parameter: 1, 2\n\n" +
                        "Use 'help' for available options";
                System.out.println(hint);
            }
        },

        HINT("hint") {
            @Override
            void execute(String[] args) {
                Move possibleMove = client.getMoveHint();
                System.out.printf("To play a move, use 'move {position} {rotation}' operation where:\n" +
                                "position (0-35) - is the field index of the next marble position\n" +
                                "rotation (0-7) - is the subboard rotation index\n\n" +
                                "Possible move: 'move %d %d'\n",
                        possibleMove.getPosition(), possibleMove.getRotation());
            }
        },

        PLAY("play") {
            @Override
            void execute(String[] args) {
                if (args.length == 1) {
                    client.play();
                } else if (args.length == 2) {
                    switch (args[1]) {
                        case "1":
                            client.play(new NaiveStrategy());
                            break;
                        case "2":
                            client.play(new ExpertStrategy());
                            break;
                        default:
                            throw new IllegalArgumentException("Illegal difficulty level");
                    }
                } else {
                    throw new IllegalArgumentException("Illegal args");
                }
            }
        },

        MOVE("move") {
            @Override
            void execute(String[] args) {
                if (args.length != 3) throw new IllegalArgumentException("Illegal args");
                int position = Integer.parseInt(args[1]);
                int rotation = Integer.parseInt(args[2]);
                client.playMove(position, rotation);
            }
        },

        LIST("list") {
            @Override
            void execute(String[] args) {
                client.list();
            }
        },

        PING("ping") {
            @Override
            void execute(String[] args) {
                client.ping();
            }
        },

        QUIT("quit") {
            @Override
            void execute(String[] args) {
                client.close();
            }
        };

        private final String value;

        abstract void execute(String... args);

        Operation(String value) {
            this.value = value;
        }

        public static Operation byValue(String value) {
            for (Operation operation : values()) {
                if (operation.value.equals(value)) {
                    return operation;
                }
            }
            return null;
        }
    }
}
