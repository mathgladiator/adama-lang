/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.json;

import org.adamalang.runtime.json.token.JsonToken;
import org.adamalang.runtime.json.token.JsonTokenType;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtDynamic;

import java.util.*;

public class JsonStreamReader {
  private final String json;
  private final int n;
  private final HashMap<String, String> dedupeStrings;
  private final HashMap<NtClient, NtClient> dedupeClients;
  ArrayDeque<JsonToken> tokens;
  private int index;

  public JsonStreamReader(final String json) {
    this.json = json;
    n = json.length();
    tokens = new ArrayDeque<>();
    this.dedupeStrings = new HashMap<>();
    this.dedupeClients = new HashMap<>();
    this.dedupeClients.put(NtClient.NO_ONE, NtClient.NO_ONE);
  }

  public void ingestDedupe(Set<String> strs) {
    for (String str : strs) {
      dedupeStrings.put(str, str);
    }
  }

  public boolean end() {
    return index >= n;
  }

  private void ensureQueueHappy(final int needs) {
    if (tokens.size() > needs) {
      return;
    }
    while (tokens.size() < 10 + needs) {
      if (index < n) {
        readToken();
      } else {
        if (tokens.size() < needs) {
          throw new RuntimeException("Unable to satisfy minimum limit");
        }
        return;
      }
    }
  }

  public String fieldName() {
    ensureQueueHappy(1);
    return tokens.removeFirst().data;
  }

  public boolean notEndOfArray() {
    ensureQueueHappy(1);
    final var first = tokens.peekFirst();
    if (first.type == JsonTokenType.EndArray) {
      tokens.removeFirst();
      return false;
    }
    return true;
  }

  public boolean notEndOfObject() {
    ensureQueueHappy(1);
    final var first = tokens.peekFirst();
    if (first.type == JsonTokenType.EndObject) {
      tokens.removeFirst();
      return false;
    }
    return true;
  }

  public boolean readBoolean() {
    ensureQueueHappy(1);
    return tokens.removeFirst().type == JsonTokenType.True;
  }

  public double readDouble() {
    ensureQueueHappy(1);
    return Double.parseDouble(tokens.removeFirst().data);
  }

  public int readInteger() {
    ensureQueueHappy(1);
    String toParse = tokens.removeFirst().data;
    try {
      return Integer.parseInt(toParse);
    } catch (NumberFormatException nfe) {
      return (int) Double.parseDouble(toParse);
    }
  }

  public long readLong() {
    ensureQueueHappy(1);
    return Long.parseLong(tokens.removeFirst().data);
  }

  public NtClient readNtClient() {
    var agent = "?";
    var authority = "?";
    if (startObject()) {
      while (notEndOfObject()) {
        switch (fieldName()) {
          case "agent":
            agent = readString();
            break;
          case "authority":
            authority = readString();
            break;
        }
      }
    }

    NtClient lookup = new NtClient(agent, authority);
    NtClient test = dedupeClients.get(lookup);
    if (test == null) {
      dedupeClients.put(lookup, lookup);
      return lookup;
    } else {
      return test;
    }
  }

  public NtComplex readNtCompex() {
    double re = 0.0;
    double im = 0.0;
    if (startObject()) {
      while (notEndOfObject()) {
        switch (fieldName()) {
          case "r":
            re = readDouble();
            break;
          case "i":
            im = readDouble();
            break;
        }
      }
    }
    return new NtComplex(re, im);
  }

  public NtAsset readNtAsset() {
    String id = "";
    String name = "";
    long size = 0;
    String contentType = "";
    String md5 = "";
    String sha384 = "";
    if (startObject()) {
      while (notEndOfObject()) {
        switch (fieldName()) {
          case "id":
            id = readString();
            break;
          case "size":
            size = readLong();
            break;
          case "name":
            name = readString();
            break;
          case "type":
            contentType = readString();
            break;
          case "md5":
            md5 = readString();
            break;
          case "sha384":
            sha384 = readString();
            break;
        }
      }
    }
    return new NtAsset(id, name, contentType, size, md5, sha384);
  }

  public String readString() {
    ensureQueueHappy(1);
    String lookup = tokens.removeFirst().data;
    String test = dedupeStrings.get(lookup);
    if (test == null) {
      dedupeStrings.put(lookup, lookup);
      return lookup;
    } else {
      return test;
    }
  }

  public Object readJavaTree() {
    if (startObject()) {
      LinkedHashMap<String, Object> obj = new LinkedHashMap<>();
      while (notEndOfObject()) {
        String fieldName = fieldName();
        obj.put(fieldName, readJavaTree());
      }
      return obj;
    } else if (startArray()) {
      ArrayList<Object> arr = new ArrayList<>();
      while (notEndOfArray()) {
        arr.add(readJavaTree());
      }
      return arr;
    } else {
      ensureQueueHappy(1);
      JsonToken token = tokens.removeFirst();
      switch (token.type) {
        case Null:
          return null;
        case False:
          return false;
        case True:
          return true;
        case NumberLiteralDouble:
          return Double.parseDouble(token.data);
        case NumberLiteralInteger:
          return Integer.parseInt(token.data);
        case StringLiteral:
          return token.data;
        default:
          throw new RuntimeException("unexpected token: " + token.toString());
      }
    }
  }

  private void readToken() {
    if (index >= json.length()) {
      return;
    }
    final var start = json.charAt(index);
    switch (start) {
      case ' ':
      case '\n':
      case '\r':
      case '\t':
      case ',':
      case ':':
        index++;
        readToken();
        return;
      case '{':
        index++;
        tokens.addLast(new JsonToken(JsonTokenType.StartObject, null));
        return;
      case '}':
        index++;
        tokens.addLast(new JsonToken(JsonTokenType.EndObject, null));
        return;
      case '[':
        index++;
        tokens.addLast(new JsonToken(JsonTokenType.StartArray, null));
        return;
      case ']':
        index++;
        tokens.addLast(new JsonToken(JsonTokenType.EndArray, null));
        return;
      case '\"':
        StringBuilder sb = null;
        for (var j = index + 1; j < n; j++) {
          var ch = json.charAt(j);
          if (ch == '\\') {
            if (sb == null) {
              sb = new StringBuilder();
              sb.append(json, index + 1, j);
            }
            j++;
            ch = json.charAt(j);
            switch (ch) {
              case 'n':
                sb.append('\n');
                break;
              case 't':
                sb.append('\t');
                break;
              case 'r':
                sb.append('\r');
                break;
              case 'f':
                sb.append('\f');
                break;
              case 'b':
                sb.append('\b');
                break;
              case '\\':
                sb.append('\\');
                break;
              case '"':
                sb.append('\"');
                break;
              case 'u':
                sb.append(Character.toString(Integer.parseInt(json.substring(j + 1, j + 5), 16)));
                j += 4;
            }
          } else if (ch == '"') {
            if (sb != null) {
              tokens.addLast(new JsonToken(JsonTokenType.StringLiteral, sb.toString()));
            } else {
              tokens.addLast(
                  new JsonToken(JsonTokenType.StringLiteral, json.substring(index + 1, j)));
            }
            index = j + 1;
            return;
          } else {
            if (sb != null) {
              sb.append(ch);
            }
          }
        }
        throw new UnsupportedOperationException();
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case '-':
      case '+':
        {
          boolean isDouble = false;
          for (var j = index + 1; j < n; j++) {
            final var ch2 = json.charAt(j);
            switch (ch2) {
              case 'E':
              case 'e':
              case '.':
              case '-':
              case '+':
                isDouble = true;
              case '0':
              case '1':
              case '2':
              case '3':
              case '4':
              case '5':
              case '6':
              case '7':
              case '8':
              case '9':
                break;
              default:
                tokens.addLast(
                    new JsonToken(
                        isDouble
                            ? JsonTokenType.NumberLiteralDouble
                            : JsonTokenType.NumberLiteralInteger,
                        json.substring(index, j)));
                index = j;
                return;
            }
          }
          tokens.addLast(
              new JsonToken(
                  isDouble ? JsonTokenType.NumberLiteralDouble : JsonTokenType.NumberLiteralInteger,
                  json.substring(index)));
          index = n;
          return;
        }
      case 'n':
        index += 4;
        tokens.addLast(new JsonToken(JsonTokenType.Null, null));
        return;
      case 't':
        index += 4;
        tokens.addLast(new JsonToken(JsonTokenType.True, null));
        return;
      case 'f':
        index += 5;
        tokens.addLast(new JsonToken(JsonTokenType.False, null));
        return;
      default:
        throw new UnsupportedOperationException();
    }
  }

  public void skipValue() {
    if (startObject()) {
      while (notEndOfObject()) {
        fieldName();
        skipValue();
      }
    } else if (startArray()) {
      while (notEndOfArray()) {
        skipValue();
      }
    } else {
      ensureQueueHappy(1);
      tokens.removeFirst();
    }
  }

  public String skipValueIntoJson() {
    JsonStreamWriter writer = new JsonStreamWriter();
    skipValue(writer);
    return writer.toString();
  }

  public NtDynamic readNtDynamic() {
    return new NtDynamic(skipValueIntoJson());
  }

  public void skipValue(final JsonStreamWriter writer) {
    if (startObject()) {
      writer.beginObject();
      while (notEndOfObject()) {
        writer.writeObjectFieldIntro(fieldName());
        skipValue(writer);
      }
      writer.endObject();
    } else if (startArray()) {
      writer.beginArray();
      while (notEndOfArray()) {
        skipValue(writer);
      }
      writer.endArray();
    } else {
      ensureQueueHappy(1);
      final var token = tokens.removeFirst();
      if (token.type == JsonTokenType.NumberLiteralInteger
          || token.type == JsonTokenType.NumberLiteralDouble) {
        writer.injectJson(token.data);
      } else if (token.type == JsonTokenType.StringLiteral) {
        writer.writeString(token.data);
      } else if (token.type == JsonTokenType.Null) {
        writer.writeNull();
      } else if (token.type == JsonTokenType.True) {
        writer.writeBoolean(true);
      } else if (token.type == JsonTokenType.False) {
        writer.writeBoolean(false);
      }
    }
  }

  public boolean startArray() {
    ensureQueueHappy(1);
    final var first = tokens.peekFirst();
    if (first.type == JsonTokenType.StartArray) {
      tokens.removeFirst();
      return true;
    }
    return false;
  }

  public boolean startObject() {
    ensureQueueHappy(1);
    final var first = tokens.peekFirst();
    if (first.type == JsonTokenType.StartObject) {
      tokens.removeFirst();
      return true;
    }
    return false;
  }

  public boolean testLackOfNull() {
    ensureQueueHappy(1);
    final var first = tokens.peekFirst();
    if (first.type == JsonTokenType.Null) {
      tokens.removeFirst();
      return false;
    }
    return true;
  }
}
