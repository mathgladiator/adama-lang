/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.control;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.*;
import org.adamalang.web.io.*;
import org.adamalang.ErrorCodes;

public abstract class DevBoxRouter {

  public void route(JsonRequest request, JsonResponder responder) {
    try {
      long requestId = request.id();
      String method = request.method();
      switch (method) {
      }
      responder.error(new ErrorCodeException(ErrorCodes.API_METHOD_NOT_FOUND));
    } catch (ErrorCodeException ex) {
      responder.error(ex);
    }
  }
}
