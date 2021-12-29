/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.web.contracts;

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
