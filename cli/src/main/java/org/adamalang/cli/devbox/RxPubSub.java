/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
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
