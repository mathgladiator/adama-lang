/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.bridges;

import org.adamalang.runtime.contracts.Bridge;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.reactives.RxRecordBase;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** this bridges records to and from JSON. This has the role of bridging types within the living document into JSON and back. */
public abstract class RecordBridge<Ty extends RxRecordBase<Ty>> implements Bridge<Ty> {
  @Override
  public void appendTo(final Ty value, final ArrayNode array) {
    throw new UnsupportedOperationException();
  }

  public abstract Ty construct(ObjectNode item, RxParent parent);

  @Override
  public Ty fromJsonNode(final JsonNode node) {
    ObjectNode tree;
    if (node instanceof ObjectNode) {
      tree = (ObjectNode) node;
    } else {
      tree = Utility.createObjectNode();
    }
    return construct(tree, null);
  }

  public abstract int getNumberColumns();

  @Override
  public JsonNode toPrivateJsonNode(final NtClient who, final Ty value) {
    if (value.__privacyPolicyAllowsCache()) {
      var cached = value.getCachedObjectNode();
      if (cached != null) { return cached; }
      cached = (ObjectNode) value.getPrivateViewFor(who);
      value.setCachedObjectNode(cached);
      return cached;
    }
    return value.getPrivateViewFor(who);
  }

  @Override
  public void writeTo(final String name, final Ty value, final ObjectNode node) {
    throw new UnsupportedOperationException();
  }
}
