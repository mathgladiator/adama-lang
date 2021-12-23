package org.adamalang.grpc.client;

public class RemoteDocument {
    public final long id;
    private final String agent;
    private final String authority;
    private final String space;
    private final String key;
    private final RemoteDocumentEvents events;
    private boolean connected;

    public RemoteDocument(long id, String agent, String authority, String space, String key, RemoteDocumentEvents events) {
        this.id = id;
        this.agent = agent;
        this.authority = authority;
        this.space = space;
        this.key = key;
        this.events = events;
        this.connected = false;
    }

    public void disconnect() {
        if (connected) {
            connected = false;
            events.disconnected();
        }
    }

    public void connect() {
        if (!connected) {
            connected = true;
            events.connected();
        }
    }
}
