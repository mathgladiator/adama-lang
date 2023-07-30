/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.control;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

public class FoundResponder {
  public final JsonResponder responder;

  public FoundResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void complete(Long id, Integer locationType, String archive, String region, String machine) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.put("id", id);
    _obj.put("locationType", locationType);
    _obj.put("archive", archive);
    _obj.put("region", region);
    _obj.put("machine", machine);
    responder.finish(_obj.toString());
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
