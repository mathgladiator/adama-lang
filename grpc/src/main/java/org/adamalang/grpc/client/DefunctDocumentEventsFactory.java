package org.adamalang.grpc.client;

public interface DefunctDocumentEventsFactory {

    public RemoteDocumentEvents make(MultiplexProtocol.DocumentConnection connection);
}
