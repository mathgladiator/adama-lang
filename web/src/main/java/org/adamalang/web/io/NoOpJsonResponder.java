package org.adamalang.web.io;

import org.adamalang.common.ErrorCodeException;

public class NoOpJsonResponder implements JsonResponder {
    @Override
    public void stream(String json) {

    }

    @Override
    public void finish(String json) {

    }

    @Override
    public void error(ErrorCodeException ex) {

    }

    public static final NoOpJsonResponder INSTANCE = new NoOpJsonResponder();
}
