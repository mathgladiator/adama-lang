package org.adamalang.cli.devbox;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.api.DataResponder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RxPubSub {
    private final AtomicInteger currentId;
    public final ConcurrentHashMap<Integer, DataResponder> responders;
    public final static RxPubSub instance = new RxPubSub();

    public RxPubSub() {
        currentId = new AtomicInteger(1);
        this.responders = new ConcurrentHashMap<>();
    }

    public Integer getNextId() {
        return currentId.getAndIncrement();
    }

    public void subscribe(Integer id, DataResponder responder) {
        responders.put(id, responder);
    }

    public void unsubscribe(Integer id) {
        if (responders.containsKey(id)) {
            responders.remove(id);
        }
    }

    public void notifyReload() {
        ObjectNode _obj = new JsonMapper().createObjectNode();
        _obj.set("reload", _obj.booleanNode(true));
        responders.forEach((id, responder) -> responder.next(_obj));
    }
}
