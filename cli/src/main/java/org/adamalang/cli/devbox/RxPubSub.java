package org.adamalang.cli.devbox;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.api.DataResponder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RxPubSub {
    private final AtomicInteger currentId;
    public final ConcurrentHashMap<Integer, DataResponder> responders;
    public final Boolean preserveViewstate;

    public RxPubSub(Boolean preserveViewstate) {
        currentId = new AtomicInteger(1);
        this.responders = new ConcurrentHashMap<>();
        this.preserveViewstate = preserveViewstate;
    }

    public Integer getNextId() {
        return currentId.getAndIncrement();
    }

    public Runnable subscribe(DataResponder responder) {
        Integer id = currentId.getAndIncrement();
        responders.put(id, responder);
        return () -> {
            responders.remove(id);
        };
    }

    public void notifyReload() {
        ObjectNode _obj = new JsonMapper().createObjectNode();
        ObjectNode _reload = new JsonMapper().createObjectNode();
        _reload.put("preserve-view", preserveViewstate);
        _obj.set("reload", _reload);
        responders.forEach((id, responder) -> responder.next(_obj));
    }
}
