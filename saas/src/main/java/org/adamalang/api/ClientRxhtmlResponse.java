/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;

/** generated class for the responder: rxhtml */
public class ClientRxhtmlResponse {
  public final ObjectNode _original;
  public final String rxhtml;

  public ClientRxhtmlResponse(ObjectNode response) {
    this._original = response;
    this.rxhtml = Json.readString(response, "rxhtml");
  }
}
