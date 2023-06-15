/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.control;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.common.Callback;
import org.adamalang.common.Stream;
import org.adamalang.web.client.socket.MultiWebClientRetryPool;
import org.adamalang.web.client.socket.WebClientConnection;

public class SelfClient {
private final MultiWebClientRetryPool pool;
  
  public SelfClient(MultiWebClientRetryPool pool) {
    this.pool = pool;
  }

  /** machine/start */
  public void machineStart(ClientMachineStartRequest request, Callback<ClientMachineStartResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "machine/start");
    node.put("machine-identity", request.machineIdentity);
    node.put("role", request.role);
    pool.requestResponse(node, (obj) -> new ClientMachineStartResponse(obj), callback);
  }
}
