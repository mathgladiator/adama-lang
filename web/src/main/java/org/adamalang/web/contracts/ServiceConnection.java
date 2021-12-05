package org.adamalang.web.contracts;

import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;

public interface ServiceConnection {
    public void execute(JsonRequest request, JsonResponder responder);

    public boolean keepalive();

    public void kill();
}
