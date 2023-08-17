/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

public class CapacityListResponder {
  public final JsonResponder responder;

  public CapacityListResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void next(String space, String region, String machine, Boolean override) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.put("space", space);
    _obj.put("region", region);
    _obj.put("machine", machine);
    _obj.put("override", override);
    responder.stream(_obj.toString());
  }

  public void finish() {
    responder.finish(null);
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
