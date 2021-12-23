package org.adamalang.grpc.client;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public class ClientState {
    private final Executor executor;
    private final HashMap<Long, RemoteDocument> documents;
    private AtomicLong nextId;
    private SocketUpstream upstream;

    public ClientState(Executor executor) {
        this.executor = executor;
        this.documents = new HashMap<>();
        this.nextId = new AtomicLong(0);
        this.upstream = null;
    }

    public long generateId() {
        return nextId.getAndIncrement();
    }

    public void connect(RemoteDocument document) {
        executor.execute(() -> {
            documents.put(document.id, document);
            if (this.upstream != null) {
                // TODO: write the create out
            }
        });
    }

    public void handle(long id, Object payload) {
        executor.execute(() -> {
            RemoteDocument document = documents.get(id);
            // TODO: send payload to document
        });
    }

    public void disconnect(RemoteDocument document) {
        executor.execute(() -> {
            documents.remove(document.id);
            document.disconnect();
        });
    }

    public void completeSend(long sendId, int seq) {
        executor.execute(() -> {
            // FIND callback
            // GIVE SEQ
        });
    }

    public void send(RemoteDocument document, String channel, String marker, String message) {
        long sendId = nextId.getAndIncrement();
        executor.execute(() -> {
            if (upstream != null) {
                // ASSOCIATE a callback
                upstream.send(document, channel, marker, message);
            } else {
                // SEND AN ERROR DIRECTLY OR QUEUE DIRECTLY in the document
            }
        });
    }

    public void disconnectAll() {
        executor.execute(() -> {
            // Error out all active callbacks for send
            for (RemoteDocument document : documents.values()) {
                document.disconnect();
            }
        });
    }
}
