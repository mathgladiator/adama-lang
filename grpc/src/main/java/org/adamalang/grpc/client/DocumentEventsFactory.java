package org.adamalang.grpc.client;

public interface DocumentEventsFactory {

    public DocumentEvents make(MultiplexProtocol.DocumentConnection connection);
}
