package org.adamalang.web.io;

import org.adamalang.common.ErrorCodeException;

import java.util.HashMap;
import java.util.concurrent.Executor;

/** a JsonResponder wrapper which will remove the given key from a map on a terminal signal. Note: all mutations to the map are executed in the provided executor */
public class JsonResponderHashMapCleanupProxy<T> implements JsonResponder {
    private final Executor executor;
    private final HashMap<Long, T> map;
    private final long key;
    private final JsonResponder responder;

    public JsonResponderHashMapCleanupProxy(Executor executor, HashMap<Long, T> map, long key, JsonResponder responder) {
        this.executor = executor;
        this.map = map;
        this.key = key;
        this.responder = responder;
    }

    @Override
    public void stream(String json) {
        responder.stream(json);
    }

    @Override
    public void finish(String json) {
        executor.execute(() -> {
            map.remove(key);
        });
        responder.finish(json);
    }

    @Override
    public void error(ErrorCodeException ex) {
        executor.execute(() -> {
            map.remove(key);
        });
        responder.error(ex);
    }
}
