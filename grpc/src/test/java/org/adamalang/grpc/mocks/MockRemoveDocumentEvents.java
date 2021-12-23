package org.adamalang.grpc.mocks;

import org.adamalang.grpc.client.RemoteDocumentEvents;

import java.util.ArrayList;

public class MockRemoveDocumentEvents implements RemoteDocumentEvents {
    private final ArrayList<String> history;

    public MockRemoveDocumentEvents() {
        this.history = new ArrayList<>();
    }

    private synchronized void write(String x) {
        history.add(x);
    }


    @Override
    public void delta(String data) {
        write("DELTA:" + data);
    }

    @Override
    public void connected() {
        write("CONNECTED");
    }

    @Override
    public void disconnected() {
        write("DISCONNECTED");
    }

    @Override
    public void error(int code) {
        write("ERROR:" + code);
    }
}
