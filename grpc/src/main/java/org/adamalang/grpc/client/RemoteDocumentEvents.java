package org.adamalang.grpc.client;

/** event structure that clients will learn about what happens */
public interface RemoteDocumentEvents {

    public void delta(String data);

    public void connected();

    public void disconnected();

    public void error(int code);
}
