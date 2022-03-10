package entity;

/**
 * Class that contains possibly supported expansions by either the client or the server.
 *
 * @author Aliaksei Kouzel
 */
public enum Expansion {
    /**
     * The AUTH extension allows the client and the server to verify each other's
     * identities by digitally signing an authentication challenge.
     */
    AUTH,

    /**
     * The CRYPT extension ensures confidentiality of messages exchanged between client and server.
     */
    CRYPT,

    /**
     * The ranking extension allows the server to communicate the current player
     * rankings on the server to the different clients.
     */
    RANK,

    /**
     * The chat extension allows client to communicate both during and outside games via text messages.
     */
    CHAT
}