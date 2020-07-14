/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.bridges;

import java.util.ArrayList;
import java.util.Map;
import org.adamalang.runtime.contracts.Bridge;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMap;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** Native to Java types like int, String, double, use bridges to convert to and from JSON */
public abstract class NativeBridge<Ty> implements Bridge<Ty> {
  public static abstract class MapDomainNativeBridge<Ty2> extends NativeBridge<Ty2> {
    public abstract Ty2 fromDomainString(String key);
    public abstract String toDomainString(Ty2 key);
  }

  /** bridge boolean to json */
  public static NativeBridge<Boolean> BOOLEAN_NATIVE_SUPPORT = new NativeBridge<>() {
    @Override
    public void appendTo(final Boolean value, final ArrayNode array) {
      array.add(value);
    }

    @Override
    public Boolean fromJsonNode(final JsonNode node) {
      if (node == null || node.isNull()) { return false; }
      return node.asBoolean(false);
    }

    @Override
    public Boolean[] makeArray(final int n) {
      return new Boolean[n];
    }

    @Override
    public JsonNode toPrivateJsonNode(final NtClient who, final Boolean value) {
      return Utility.MAPPER.getNodeFactory().booleanNode(value);
    }

    @Override
    public void writeTo(final String name, final Boolean value, final ObjectNode node) {
      node.put(name, value);
    }
  };
  /** bridge client to json */
  public static NativeBridge<NtClient> CLIENT_NATIVE_SUPPORT = new NativeBridge<>() {
    @Override
    public void appendTo(final NtClient value, final ArrayNode array) {
      value.dump(array.addObject());
    }

    @Override
    public NtClient fromJsonNode(final JsonNode node) {
      return NtClient.from(node);
    }

    @Override
    public NtClient[] makeArray(final int n) {
      return new NtClient[n];
    }

    @Override
    public JsonNode toPrivateJsonNode(final NtClient who, final NtClient value) {
      final var node = Utility.createObjectNode();
      value.dump(node);
      return node;
    }

    @Override
    public void writeTo(final String name, final NtClient value, final ObjectNode node) {
      value.dump(node.putObject(name));
    }
  };
  /** bridge double to json */
  public static NativeBridge<Double> DOUBLE_NATIVE_SUPPORT = new NativeBridge<>() {
    @Override
    public void appendTo(final Double value, final ArrayNode array) {
      array.add(value);
    }

    @Override
    public Double fromJsonNode(final JsonNode node) {
      if (node == null || node.isNull()) { return 0.0; }
      return node.asDouble(0.0);
    }

    @Override
    public Double[] makeArray(final int n) {
      return new Double[n];
    }

    @Override
    public JsonNode toPrivateJsonNode(final NtClient who, final Double value) {
      return Utility.MAPPER.getNodeFactory().numberNode(value);
    }

    @Override
    public void writeTo(final String name, final Double value, final ObjectNode node) {
      node.put(name, value);
    }
  };
  /** bridge integer to json */
  public static MapDomainNativeBridge<Integer> INTEGER_NATIVE_SUPPORT = new MapDomainNativeBridge<>() {
    @Override
    public void appendTo(final Integer value, final ArrayNode array) {
      array.add(value);
    }

    @Override
    public Integer fromDomainString(final String key) {
      return Integer.parseInt(key);
    }

    @Override
    public Integer fromJsonNode(final JsonNode node) {
      if (node == null || node.isNull()) { return 0; }
      return node.asInt(0);
    }

    @Override
    public Integer[] makeArray(final int n) {
      return new Integer[n];
    }

    @Override
    public String toDomainString(final Integer key) {
      return key.toString();
    }

    @Override
    public JsonNode toPrivateJsonNode(final NtClient who, final Integer value) {
      return Utility.MAPPER.getNodeFactory().numberNode(value);
    }

    @Override
    public void writeTo(final String name, final Integer value, final ObjectNode node) {
      node.put(name, value);
    }
  };
  /** bridge long to json */
  public static MapDomainNativeBridge<Long> LONG_NATIVE_SUPPORT = new MapDomainNativeBridge<>() {
    @Override
    public void appendTo(final Long value, final ArrayNode array) {
      array.add(Long.toString(value));
    }

    @Override
    public Long fromDomainString(final String key) {
      return Long.parseLong(key);
    }

    @Override
    public Long fromJsonNode(final JsonNode node) {
      if (node == null || node.isNull()) { return 0L; }
      return Long.parseLong(node.asText("0"));
    }

    @Override
    public Long[] makeArray(final int n) {
      return new Long[n];
    }

    @Override
    public String toDomainString(final Long key) {
      return key.toString();
    }

    @Override
    public JsonNode toPrivateJsonNode(final NtClient who, final Long value) {
      return Utility.MAPPER.getNodeFactory().textNode(Long.toString(value));
    }

    @Override
    public void writeTo(final String name, final Long value, final ObjectNode node) {
      node.put(name, Long.toString(value));
    }
  };
  /** bridge string to json */
  public static MapDomainNativeBridge<String> STRING_NATIVE_SUPPORT = new MapDomainNativeBridge<>() {
    @Override
    public void appendTo(final String value, final ArrayNode array) {
      array.add(value);
    }

    @Override
    public String fromDomainString(final String key) {
      return key;
    }

    @Override
    public String fromJsonNode(final JsonNode node) {
      if (node == null || node.isNull()) { return ""; }
      if (node.isTextual()) {
        return node.textValue();
      } else if (node.isIntegralNumber() || node.isFloatingPointNumber() || node.isBoolean()) {
        return node.toString();
      } else {
        return "";
      }
    }

    @Override
    public String[] makeArray(final int n) {
      return new String[n];
    }

    @Override
    public String toDomainString(final String key) {
      return key;
    }

    @Override
    public JsonNode toPrivateJsonNode(final NtClient who, final String value) {
      return Utility.MAPPER.getNodeFactory().textNode(value);
    }

    @Override
    public void writeTo(final String name, final String value, final ObjectNode node) {
      node.put(name, value);
    }
  };

  public static <Ty> NativeBridge<Ty[]> WRAP_ARRAY(final Bridge<Ty> elementBridge) {
    return new NativeBridge<>() {
      @Override
      public void appendTo(final Ty[] value, final ArrayNode array) {
        final var toFill = array.addArray();
        for (final Ty element : value) {
          elementBridge.appendTo(element, toFill);
        }
      }

      @Override
      public Ty[] fromJsonNode(final JsonNode node) {
        final var items = new ArrayList<Ty>();
        if (node != null && node.isArray()) {
          final var arrayNode = (ArrayNode) node;
          for (var k = 0; k < arrayNode.size(); k++) {
            items.add(elementBridge.fromJsonNode(arrayNode.get(k)));
          }
        }
        return items.toArray(elementBridge.makeArray(items.size()));
      }

      @Override
      public Ty[][] makeArray(final int n) {
        // TODO: CONSIDER KILLING THE TY and make a NativeArray
        throw new UnsupportedOperationException();
      }

      @Override
      public JsonNode toPrivateJsonNode(final NtClient who, final Ty[] value) {
        final var arrayNode = Utility.createArrayNode();
        for (final Ty item : value) {
          final var itemNode = elementBridge.toPrivateJsonNode(who, item);
          if (itemNode != null) {
            arrayNode.add(itemNode);
          }
        }
        return arrayNode;
      }

      @Override
      public void writeTo(final String name, final Ty[] value, final ObjectNode node) {
        final var toFill = node.putArray(name);
        for (final Ty element : value) {
          elementBridge.appendTo(element, toFill);
        }
      }
    };
  }

  public static <Ty> NativeBridge<NtList<Ty>> WRAP_LIST(final Bridge<Ty> elementBridge) {
    return new NativeBridge<>() {
      @Override
      public void appendTo(final NtList<Ty> value, final ArrayNode array) {
        final var toFill = array.addArray();
        for (final Ty element : value) {
          elementBridge.appendTo(element, toFill);
        }
      }

      @Override
      public NtList<Ty> fromJsonNode(final JsonNode node) {
        final var items = new ArrayList<Ty>();
        if (node != null && node.isArray()) {
          final var arrayNode = (ArrayNode) node;
          for (var k = 0; k < arrayNode.size(); k++) {
            items.add(elementBridge.fromJsonNode(arrayNode.get(k)));
          }
        }
        return new ArrayNtList<>(items, elementBridge);
      }

      @Override
      public NtList<Ty>[] makeArray(final int n) {
        return new NtList[n];
      }

      @Override
      public JsonNode toPrivateJsonNode(final NtClient who, final NtList<Ty> value) {
        final var arrayNode = Utility.createArrayNode();
        for (final Ty item : value) {
          final var itemNode = elementBridge.toPrivateJsonNode(who, item);
          if (itemNode != null) {
            arrayNode.add(itemNode);
          }
        }
        return arrayNode;
      }

      @Override
      public void writeTo(final String name, final NtList<Ty> value, final ObjectNode node) {
        final var toFill = node.putArray(name);
        for (final Ty element : value) {
          elementBridge.appendTo(element, toFill);
        }
      }
    };
  }

  public static <TIn, TOut> NativeBridge<NtMap<TIn, TOut>> WRAP_MAP(final MapDomainNativeBridge<TIn> domainBridge, final NativeBridge<TOut> rangeBridge) {
    return new NativeBridge<>() {
      @Override
      public void appendTo(final NtMap<TIn, TOut> value, final ArrayNode array) {
        final var result = array.addObject();
        for (final Map.Entry<TIn, TOut> entry : value.storage.entrySet()) {
          rangeBridge.writeTo(domainBridge.toDomainString(entry.getKey()), entry.getValue(), result);
        }
      }

      @Override
      public NtMap<TIn, TOut> fromJsonNode(final JsonNode node) {
        if (node == null || node.isNull() || !node.isObject()) { return new NtMap<>(); }
        final var map = new NtMap<TIn, TOut>();
        final var fieldIt = node.fields();
        while (fieldIt.hasNext()) {
          final var entry = fieldIt.next();
          map.storage.put(domainBridge.fromDomainString(entry.getKey()), rangeBridge.fromJsonNode(entry.getValue()));
        }
        return map;
      }

      @Override
      public NtMap<TIn, TOut>[] makeArray(final int n) {
        return new NtMap[n];
      }

      @Override
      public JsonNode toPrivateJsonNode(final NtClient who, final NtMap<TIn, TOut> value) {
        final var result = Utility.createObjectNode();
        for (final Map.Entry<TIn, TOut> entry : value.storage.entrySet()) {
          result.set(domainBridge.toDomainString(entry.getKey()), rangeBridge.toPrivateJsonNode(who, entry.getValue()));
        }
        return result;
      }

      @Override
      public void writeTo(final String name, final NtMap<TIn, TOut> value, final ObjectNode node) {
        final var result = node.putObject(name);
        for (final Map.Entry<TIn, TOut> entry : value.storage.entrySet()) {
          rangeBridge.writeTo(domainBridge.toDomainString(entry.getKey()), entry.getValue(), result);
        }
      }
    };
  }

  public static <Ty> NativeBridge<NtMaybe<Ty>> WRAP_MAYBE(final Bridge<Ty> elementBridge) {
    return new NativeBridge<>() {
      @Override
      public void appendTo(final NtMaybe<Ty> value, final ArrayNode array) {
        if (value.has()) {
          elementBridge.appendTo(value.get(), array);
        } else {
          array.addNull();
        }
      }

      @Override
      public NtMaybe<Ty> fromJsonNode(final JsonNode node) {
        if (node == null || node.isNull()) { return new NtMaybe<>(); }
        return new NtMaybe<>(elementBridge.fromJsonNode(node));
      }

      @Override
      public NtMaybe<Ty>[] makeArray(final int n) {
        return new NtMaybe[n];
      }

      @Override
      public JsonNode toPrivateJsonNode(final NtClient who, final NtMaybe<Ty> value) {
        if (value.has()) { return elementBridge.toPrivateJsonNode(who, value.get()); }
        return Utility.MAPPER.getNodeFactory().nullNode();
      }

      @Override
      public void writeTo(final String name, final NtMaybe<Ty> value, final ObjectNode node) {
        if (value.has()) {
          elementBridge.writeTo(name, value.get(), node);
        } else if (node.has(name)) {
          node.putNull(name);
        }
      }
    };
  }

  public Ty readFromMessageObject(final ObjectNode node, final String field) {
    return fromJsonNode(node.get(field));
  }
}
