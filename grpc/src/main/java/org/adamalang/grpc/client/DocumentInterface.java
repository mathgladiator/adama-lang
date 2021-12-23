package org.adamalang.grpc.client;

public interface DocumentInterface {

    // TODO: attach

    public void send(String channel, String marker, String message);

    public void disconnect();
}
