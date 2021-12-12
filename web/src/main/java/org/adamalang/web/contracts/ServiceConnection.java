package org.adamalang.web.contracts;

import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;

/** represents a single connection via a WebSocket */
public interface ServiceConnection {

    /** the client is executing a single request */
    public void execute(JsonRequest request, JsonResponder responder);

    /** periodically, make sure the client and downstream services are healthy */
    public boolean keepalive();

    /** the connection has been severed */
    public void kill();
}
