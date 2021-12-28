package org.adamalang.api;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

public class SpaceListingResponder {
  public final JsonResponder responder;

  public SpaceListingResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void next(String space, String role, String billing, String created) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.put("space", space);
    _obj.put("role", role);
    _obj.put("billing", billing);
    _obj.put("created", created);
    responder.stream(_obj.toString());
  }

  public void finish(String space, String role, String billing, String created) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.put("space", space);
    _obj.put("role", role);
    _obj.put("billing", billing);
    _obj.put("created", created);
    responder.finish(_obj.toString());
  }

  public void finish() {
    responder.finish("{}");
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
