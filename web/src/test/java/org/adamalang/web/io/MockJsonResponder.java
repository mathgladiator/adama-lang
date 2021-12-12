package org.adamalang.web.io;

import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.util.ArrayList;

public class MockJsonResponder implements JsonResponder {
    public final ArrayList<String> events;

    public MockJsonResponder() {
        this.events = new ArrayList<>();
    }

    @Override
    public void stream(String json) {
        events.add("STREAM:" + json);
    }

    @Override
    public void finish(String json) {
        events.add("FINISH:" + json);
    }

    @Override
    public void error(ErrorCodeException ex) {
        events.add("ERROR:" + ex.code);
    }
}
