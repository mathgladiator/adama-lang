/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.connection.Session;
import org.adamalang.web.io.*;

/** Update the viewer state of the document.
  * 
  * The viewer state is accessible to bubbles to provide view restriction and filtering.
  * For example, the viewer state is how a document can provide real-time search or pagination. */
public class ConnectionUpdateRequest {
  public final Long connection;
  public final ObjectNode viewerState;

  public ConnectionUpdateRequest(final Long connection, final ObjectNode viewerState) {
    this.connection = connection;
    this.viewerState = viewerState;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<ConnectionUpdateRequest> callback) {
    try {
      final Long connection = request.getLong("connection", true, 405505);
      final ObjectNode viewerState = request.getObject("viewer-state", false, 0);
      nexus.executor.execute(new NamedRunnable("connectionupdate-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new ConnectionUpdateRequest(connection, viewerState));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("connectionupdate-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
  }
}
