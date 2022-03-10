# Pentago
Pentago is a project which provides the server-driven "Pentago" gaming.

## Setup
Building a project requires to run the following:
```shell
./gradlew build
```
This will ensure that the required libraries with their appropriate versions are installed.

## Testing
All tests related to the project are located at the 'src/test/java' package, which cover:

* Game logic:
    * Board implementation (incl. subboard rotations, setting fields, etc.)
    * Game over conditions
    * Playing a game with predefined moves
    * Playing a random game played by the AI

* Networking:
    * Connecting and logging several clients to the server
    * Starting a server on the provided port

## Starting a game in IntelliJ IDEA

In order to play a pentago game, the user should first start a server on the given port, then connect and login two clients, and finally joining them into the queue on the server. Then, the server will automatically start a new session with both of these clients.

Starting a server requires the user to run the main method of the ServerApplication located at 'src/main/java/server/ServerApplication'. After that, the following exchange in the command-line should follow:
```shell
Server port: 8080
Starting server on port 8080...
```

Connecting and logging in two clients requires the user to run two instances of the ClientApplication located at 'src/main/java/client/ClientApplication'. After that, the following exchange in the command-line should follow:
```shell
Server address: localhost
Server port: 8080
[OUTGOING] 'HELLO~Local client'
[INCOMING] 'HELLO~Pentago server'
Please enter a username: Alex
[OUTGOING] 'LOGIN~Alex'
[INCOMING] 'LOGIN'
Successfully logging in...
```

Joining the game on the server requires the user to write the following command in both of these instances:
```shell
play
```
Or, optionally, the user can indicate the difficulty level (1 or 2) as a parameter if they want an AI to play instead:
```shell
play 1
```
After that, the user will be allowed to play moves by writing the following commands:
```shell
move 3 4
```
Where the 1-st parameter is the field position from 0 to 35, and the 2-nd parameter is the subboard rotation index from 0 to 7. Moreover, the user can write the 'hint' command to get a hint on the possible legal moves.
