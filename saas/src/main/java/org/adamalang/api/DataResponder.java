/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

public class DataResponder {
  public final JsonResponder responder;

  public DataResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void next(ObjectNode delta) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    if (delta != null) {
      _obj.set("delta", delta);
    }
    responder.stream(_obj.toString());
  }

  public void finish(ObjectNode delta) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    if (delta != null) {
      _obj.set("delta", delta);
    }
    responder.finish(_obj.toString());
  }

  public void finish() {
    responder.finish("{}");
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
