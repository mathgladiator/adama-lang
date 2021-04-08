/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtDynamic;

/** Very fast Json stream writer. */
public class JsonStreamWriter {
  public enum CommaStateMachine {
    FirstItemSkipComma, IntroduceComma, None
  }

  private static String hexEncode(final char ch) {
    final var hi = zeroPadLeft(Integer.toString(ch / 256, 16));
    final var lo = zeroPadLeft(Integer.toString(ch % 256, 16));
    return "\\u" + hi + lo;
  }

  private static String zeroPadLeft(final String x) {
    if (x.length() == 1) { return "0" + x; }
    return x;
  }

  private final Stack<CommaStateMachine> commas;
  private CommaStateMachine commaStateMachine;
  private final StringBuilder sb = new StringBuilder();

  public JsonStreamWriter() {
    commas = new Stack<>();
    commaStateMachine = CommaStateMachine.None;
  }

  public void beginArray() {
    maybe_comma();
    sb.append("[");
    push_need_comma();
  }

  public void beginObject() {
    maybe_comma();
    sb.append("{");
    push_need_comma();
  }

  public void endArray() {
    sb.append("]");
    pop_need_comma();
  }

  public void endObject() {
    sb.append("}");
    pop_need_comma();
  }

  public void injectJson(final String x) {
    maybe_comma();
    sb.append(x);
  }

  public void writeNtDynamic(NtDynamic value) {
    injectJson(value.json);
  }

  private void maybe_comma() {
    if (commaStateMachine == CommaStateMachine.IntroduceComma) {
      sb.append(",");
    }
    if (commaStateMachine == CommaStateMachine.FirstItemSkipComma) {
      commaStateMachine = CommaStateMachine.IntroduceComma;
    }
  }

  private void pop_need_comma() {
    commaStateMachine = commas.pop();
  }

  private void push_need_comma() {
    commas.push(commaStateMachine);
    commaStateMachine = CommaStateMachine.FirstItemSkipComma;
  }

  @Override
  public String toString() {
    return sb.toString();
  }

  public void writeBoolean(final boolean b) {
    maybe_comma();
    sb.append(b);
  }

  public void writeDouble(final double d) {
    maybe_comma();
    sb.append(d);
  }

  public void writeFastString(final String s) {
    maybe_comma();
    sb.append("\"").append(s).append("\"");
  }

  public void writeInteger(final int x) {
    maybe_comma();
    sb.append(x);
  }

  public void writeLong(final long x) {
    maybe_comma();
    sb.append("\"").append(x).append("\"");
  }

  public void writeNtAsset(final NtAsset a) {
    beginObject();
    writeObjectFieldIntro("id");
    writeLong(a.id);
    writeObjectFieldIntro("size");
    writeLong(a.size);
    writeObjectFieldIntro("name");
    writeString(a.name);
    writeObjectFieldIntro("type");
    writeString(a.contentType);
    writeObjectFieldIntro("md5");
    writeString(a.md5);
    writeObjectFieldIntro("sha384");
    writeString(a.sha384);
    endObject();
  }

  public void writeNtClient(final NtClient c) {
    beginObject();
    writeObjectFieldIntro("agent");
    writeFastString(c.agent);
    writeObjectFieldIntro("authority");
    writeFastString(c.authority);
    endObject();
  }

  public void writeNull() {
    maybe_comma();
    sb.append("null");
  }

  @SuppressWarnings("unchecked")
  public void writeTree(Object tree) {
    if (tree == null) {
      writeNull();
      return;
    }
    if (tree instanceof HashMap) {
      HashMap<String, Object> map = (HashMap<String, Object>) tree;
      beginObject();
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        writeObjectFieldIntro(entry.getKey());
        writeTree(entry.getValue());
      }
      endObject();
    } else if (tree instanceof ArrayList) {
      beginArray();
      for (Object element : (ArrayList) tree) {
        writeTree(element);
      }
      endArray();

    } else if (tree instanceof Boolean) {
      writeBoolean((Boolean) tree);
    } else if (tree instanceof Double) {
      writeDouble((Double) tree);
    } else if (tree instanceof Long) {
      writeLong((Long) tree);
    } else if (tree instanceof Integer) {
      writeInteger((Integer) tree);
    } else if (tree instanceof String) {
      writeString((String) tree);
    } else {
      throw new RuntimeException("unexpected object: " + tree);
    }
  }

  public void writeObjectFieldIntro(final int fieldName) {
    maybe_comma();
    sb.append("\"").append(fieldName).append("\":");
    commaStateMachine = CommaStateMachine.FirstItemSkipComma;
  }

  public void writeObjectFieldIntro(final long fieldName) {
    maybe_comma();
    sb.append("\"").append(fieldName).append("\":");
    commaStateMachine = CommaStateMachine.FirstItemSkipComma;
  }

  public void writeObjectFieldIntro(final String fieldName) {
    maybe_comma();
    sb.append("\"").append(fieldName).append("\":");
    commaStateMachine = CommaStateMachine.FirstItemSkipComma;
  }

  public void writeString(final String s) {
    maybe_comma();
    if (s == null) {
      writeNull();
      return;
    }
    sb.append("\"");
    for (var k = 0; k < s.length(); k++) {
      final var ch = s.charAt(k);
      switch (ch) {
        case '\n':
          sb.append("\\n");
          break;
        case '\r':
          sb.append("\\r");
          break;
        case '\f':
          sb.append("\\f");
          break;
        case '\b':
          sb.append("\\b");
          break;
        case '\t':
          sb.append("\\t");
          break;
        case '\\':
          sb.append("\\\\");
          break;
        case '\"':
          sb.append("\\\"");
          break;
        default:
          if (ch >= 128) {
            sb.append(hexEncode(ch));
          } else {
            sb.append(ch);
          }
      }
    }
    sb.append("\"");
  }
}
