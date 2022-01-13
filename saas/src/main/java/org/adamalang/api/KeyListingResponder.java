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

public class KeyListingResponder {
  public final JsonResponder responder;

  public KeyListingResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void next(String key, String created, String updated, Integer seq) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.put("key", key);
    _obj.put("created", created);
    _obj.put("updated", updated);
    _obj.put("seq", seq);
    responder.stream(_obj.toString());
  }

  public void finish(String key, String created, String updated, Integer seq) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.put("key", key);
    _obj.put("created", created);
    _obj.put("updated", updated);
    _obj.put("seq", seq);
    responder.finish(_obj.toString());
  }

  public void finish() {
    responder.finish("{}");
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
