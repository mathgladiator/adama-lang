/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
