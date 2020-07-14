/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import java.util.function.Function;
import org.adamalang.runtime.LivingDocument;
import org.adamalang.runtime.bridges.RecordBridge;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** a reactive factory for reading values from a json object */
public class RxFactory {
  public static ObjectNode ensureChildNodeExists(final ObjectNode node, final String name) {
    ObjectNode result = null;
    if (node.has(name)) {
      final var resultNode = node.get(name);
      if (resultNode.isObject()) {
        result = (ObjectNode) resultNode;
      }
    }
    if (result == null) {
      result = node.putObject(name);
    }
    return result;
  }

  public static RxBoolean makeRxBoolean(final RxParent parent, final ObjectNode node, final String name, final boolean defaultValue) {
    var value = defaultValue;
    if (node != null && node.has(name)) {
      final var childNode = node.get(name);
      if (childNode.isBoolean()) {
        value = childNode.asBoolean(defaultValue);
      }
    }
    return new RxBoolean(parent, value);
  }

  public static RxClient makeRxClient(final RxParent parent, final ObjectNode node, final String name, final NtClient defaultValue) {
    var value = defaultValue;
    if (node.has(name)) {
      // we have some prior data to inherirt, yeah
      final var childNode = node.get(name);
      if (childNode.isObject()) {
        value = NtClient.from(childNode);
      }
    }
    return new RxClient(parent, value);
  }

  public static RxDouble makeRxDouble(final RxParent parent, final ObjectNode node, final String name, final double defaultValue) {
    var value = defaultValue;
    if (node.has(name)) {
      final var childNode = node.get(name);
      if (childNode.isIntegralNumber() || childNode.isFloatingPointNumber()) {
        value = childNode.asDouble();
      } else if (childNode.isTextual()) {
        value = Double.parseDouble(childNode.asText("" + defaultValue));
      }
    }
    return new RxDouble(parent, value);
  }

  public static RxInt32 makeRxInt32(final RxParent parent, final ObjectNode node, final String name, final int defaultValue) {
    var value = defaultValue;
    if (node.has(name)) {
      final var childNode = node.get(name);
      if (childNode.isIntegralNumber()) {
        value = childNode.asInt(defaultValue);
      } else if (childNode.isTextual()) {
        value = Integer.parseInt(childNode.asText("" + defaultValue));
      }
    }
    return new RxInt32(parent, value);
  }

  public static RxInt64 makeRxInt64(final RxParent parent, final ObjectNode node, final String name, final long defaultValue) {
    var value = defaultValue;
    if (node.has(name)) {
      final var childNode = node.get(name);
      if (childNode.isIntegralNumber()) {
        value = childNode.asLong(defaultValue);
      } else if (childNode.isTextual()) {
        value = Long.parseLong(childNode.asText("" + defaultValue));
      }
    }
    return new RxInt64(parent, value);
  }

  public static <Ty extends RxBase> RxMaybe<Ty> makeRxMaybe(final RxParent parent, final ObjectNode node, final String name, final Function<RxParent, Ty> maker) {
    final var maybe = new RxMaybe<>(parent, maker);
    if (node.has(name)) {
      maybe.make();
      maybe.__commit(name, Utility.createObjectNode());
    }
    return maybe;
  }

  public static RxString makeRxString(final RxParent parent, final ObjectNode node, final String name, final String defaultValue) {
    var value = defaultValue;
    if (node.has(name)) {
      final var childNode = node.get(name);
      if (childNode.isTextual() || childNode.isIntegralNumber()) {
        value = childNode.asText();
      }
    }
    return new RxString(parent, value);
  }

  public static <Ty extends RxRecordBase<Ty>> RxTable<Ty> makeRxTable(final LivingDocument document, final RxParent parent, final ObjectNode node, final String name, final RecordBridge<Ty> bridge) {
    ObjectNode child = null;
    if (node.has(name)) {
      // we have some prior data to inherirt, yeah
      final var childNode = node.get(name);
      if (childNode.isObject() && !childNode.isNull()) {
        child = (ObjectNode) childNode;
      }
    }
    if (child == null) {
      child = node.putObject(name);
    }
    return new RxTable<>(document, name, child, parent, bridge);
  }
}
