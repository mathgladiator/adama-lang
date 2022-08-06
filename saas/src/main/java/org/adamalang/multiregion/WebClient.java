/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.multiregion;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.contracts.AdamaStream;
import org.adamalang.web.client.WebClientConnection;
import org.adamalang.web.contracts.WebJsonStream;
import org.adamalang.web.contracts.WebLifecycle;


public class WebClient implements WebLifecycle {
  private WebClientConnection connection;

  public WebClient() {
    connection = null;
  }

  public AdamaStream connect(String ip, String origin, String agent, String authority, String space, String key, String viewerState, String assetKey, SimpleEvents events) {
    if (connection != null) {
      ObjectNode request = Json.newJsonObject();
      // IDENTITY = String ip, String origin, String agent, String authority, assetKey
      String identity = "...";

      // Forge an identity
      // String space, String key, String viewerState

      request.put("identity", identity);
      request.put("space", space);
      request.put("key", key);
      request.put("viewer-state", viewerState);

      connection.execute(request, new WebJsonStream() {
        boolean sentConnected = false;
        @Override
        public void data(int connection, ObjectNode node) {
          if (!sentConnected) {
            events.connected();
            sentConnected = true;
          }
          // Extract data
        }

        @Override
        public void complete() {
          events.disconnected();
        }

        @Override
        public void failure(int code) {
          events.error(code);
        }
      });
    }
    return null;

  }

  @Override
  public void connected(WebClientConnection connection) {
    this.connection = connection;
  }

  @Override
  public void ping(int latency) {

  }

  @Override
  public void failure(Throwable t) {

  }

  @Override
  public void disconnected() {

  }
}
