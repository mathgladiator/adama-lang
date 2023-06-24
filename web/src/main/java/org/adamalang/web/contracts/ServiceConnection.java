/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.contracts;

import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;

/** represents a single connection via a WebSocket */
public interface ServiceConnection {

  /** the client is executing a single request */
  void execute(JsonRequest request, JsonResponder responder);

  /** periodically, make sure the client and downstream services are healthy */
  boolean keepalive();

  /** the connection has been severed */
  void kill();
}
