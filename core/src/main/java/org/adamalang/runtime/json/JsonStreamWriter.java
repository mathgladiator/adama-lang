/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.json;

import org.adamalang.runtime.natives.*;
import org.adamalang.translator.parser.token.Token;

import java.util.*;

/** Very fast Json stream writer. */
public class JsonStreamWriter {
  private final Stack<CommaStateMachine> commas;
  private final StringBuilder sb = new StringBuilder();
  private CommaStateMachine commaStateMachine;
  private boolean trackAssets;
  private TreeSet<String> assetIdsSeen;
  private long assetBytesWritten;

  public JsonStreamWriter() {
    commas = new Stack<>();
    commaStateMachine = CommaStateMachine.None;
    trackAssets = false;
  }

  /** when assets are written, track the unique bytes associated to the document */
  public void enableAssetTracking() {
    trackAssets = true;
    assetIdsSeen = new TreeSet<>();
  }

  /** if asset tracking has been enabled, then return the number of bytes pointed to by this document */
  public long getAssetBytes() {
    return assetBytesWritten;
  }

  public void beginArray() {
    maybe_comma();
    sb.append("[");
    push_need_comma();
  }

  public void endArray() {
    sb.append("]");
    pop_need_comma();
  }

  public void writeNtDynamic(NtDynamic value) {
    injectJson(value.json);
  }

  public void injectJson(final String x) {
    maybe_comma();
    sb.append(x);
  }

  private void maybe_comma() {
    if (commaStateMachine == CommaStateMachine.IntroduceComma) {
      sb.append(",");
    }
    if (commaStateMachine == CommaStateMachine.FirstItemSkipComma) {
      commaStateMachine = CommaStateMachine.IntroduceComma;
    }
  }

  public void force_comma_introduction() {
    commaStateMachine = CommaStateMachine.IntroduceComma;
  }

  public void force_comma() {
    sb.append(",");
  }

  @Override
  public String toString() {
    return sb.toString();
  }

  public void writeBoolean(final boolean b) {
    maybe_comma();
    sb.append(b);
  }

  public void writeNtComplex(final NtComplex c) {
    beginObject();
    writeObjectFieldIntro("r");
    writeDouble(c.real);
    writeObjectFieldIntro("i");
    writeDouble(c.imaginary);
    endObject();
  }

  public void beginObject() {
    maybe_comma();
    sb.append("{");
    push_need_comma();
  }

  public <T> void writeObjectFieldIntro(final T fieldName) {
    maybe_comma();
    sb.append("\"").append(fieldName).append("\":");
    commaStateMachine = CommaStateMachine.FirstItemSkipComma;
  }

  public void writeDouble(final double d) {
    maybe_comma();
    sb.append(d);
  }

  public void endObject() {
    sb.append("}");
    pop_need_comma();
  }

  private void push_need_comma() {
    commas.push(commaStateMachine);
    commaStateMachine = CommaStateMachine.FirstItemSkipComma;
  }

  private void pop_need_comma() {
    commaStateMachine = commas.pop();
  }

  public void writeNtDate(final NtDate d) {
    writeString(d.toString());
  }

  public void writeString(final String s) {
    maybe_comma();
    if (s == null) {
      sb.append("null");
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

  public void writeNull() {
    maybe_comma();
    sb.append("null");
  }

  private static String hexEncode(final char ch) {
    final var hi = zeroPadLeft(Integer.toString(ch / 256, 16));
    final var lo = zeroPadLeft(Integer.toString(ch % 256, 16));
    return "\\u" + hi + lo;
  }

  private static String zeroPadLeft(final String x) {
    if (x.length() == 1) {
      return "0" + x;
    }
    return x;
  }

  public void writeToken(final Token token) {
    if (token.isStringLiteral() || token.isNumberLiteral()) {
      maybe_comma();
      sb.append(token.text);
    } else {
      writeString(token.text);
    }
  }

  public void writeNtDateTime(final NtDateTime d) {
    writeString(d.dateTime.toString());
  }

  public void writeNtTime(final NtTime d) {
    writeString(d.toString());
  }

  public void writeNtTimeSpan(final NtTimeSpan d) {
    writeDouble(d.seconds);
  }

  public void inline(final String s) {
    sb.append(s);
  }

  public void writeInteger(final int x) {
    maybe_comma();
    sb.append(x);
  }

  public void writeNtAsset(final NtAsset a) {
    beginObject();
    writeObjectFieldIntro("id");
    writeString(a.id);
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
    writeObjectFieldIntro("@gc");
    writeString("@yes");
    endObject();
    if (trackAssets) {
      if (!assetIdsSeen.contains(a.id)) {
        assetIdsSeen.add(a.id);
        assetBytesWritten += a.size;
      }
    }
  }

  public void writeLong(final long x) {
    maybe_comma();
    sb.append("\"").append(x).append("\"");
  }

  public void writeNtPrincipal(final NtPrincipal c) {
    beginObject();
    writeObjectFieldIntro("agent");
    writeFastString(c.agent);
    writeObjectFieldIntro("authority");
    writeFastString(c.authority);
    endObject();
  }

  public void writeFastString(final String s) {
    maybe_comma();
    sb.append("\"").append(s).append("\"");
  }

  @SuppressWarnings("unchecked")
  public void writeTree(Object tree) {
    if (tree == null) {
      writeNull();
      return;
    }
    if (tree instanceof Map<?,?>) {
      Map<String, Object> map = (Map<String, Object>) tree;
      beginObject();
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        writeObjectFieldIntro(entry.getKey());
        writeTree(entry.getValue());
      }
      endObject();
    } else if (tree instanceof List<?>) {
      beginArray();
      for (Object element : (List) tree) {
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

  public enum CommaStateMachine {
    FirstItemSkipComma, IntroduceComma, None
  }
}
