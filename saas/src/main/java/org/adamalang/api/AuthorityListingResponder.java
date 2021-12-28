package org.adamalang.api;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

public class AuthorityListingResponder {
  public final JsonResponder responder;

  public AuthorityListingResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void next(String authority) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.put("authority", authority);
    responder.stream(_obj.toString());
  }

  public void finish(String authority) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.put("authority", authority);
    responder.finish(_obj.toString());
  }

  public void finish() {
    responder.finish("{}");
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
