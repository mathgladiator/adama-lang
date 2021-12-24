package org.adamalang.grpc.client.contracts;

/** event structure that clients will learn about what happens */
public interface DocumentEvents {

    public void connected();

    public void delta(String data);

    public void error(int code);

    public void disconnected();

}
