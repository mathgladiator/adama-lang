package org.adamalang.grpc.client;

public interface DocumentEvents {

    public void delta(String data);

    public void connected();

    public void disconnected();

    public void error(int code);
}
