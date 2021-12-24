package org.adamalang.grpc.client.contracts;

/** event structure that clients will learn about what happens for a connection to a document */
public interface Events {
    /** the connection was successful, and we can talk to the document via the remote */
    public void connected(Remote remote);

    /** a data change has occurred */
    public void delta(String data);

    /** an error has occurred */
    public void error(int code);

    /** the document was disconnected */
    public void disconnected();
}
