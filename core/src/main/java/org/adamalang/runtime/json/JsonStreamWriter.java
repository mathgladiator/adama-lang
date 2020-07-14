/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import java.util.Stack;

/** Very fast Json stream writer. */
public class JsonStreamWriter {
  public enum CommaStateMachine {
    FirstItemSkipComma, IntroduceComma, None
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

  public void writeBool(final boolean b) {
    maybe_comma();
    sb.append(b);
  }

  public void writeDouble(final double d) {
    maybe_comma();
    sb.append(d);
  }

  public void writeInt(final int x) {
    maybe_comma();
    sb.append(x);
  }

  public void writeObjectFieldIntro(final String fieldName) {
    maybe_comma();
    sb.append("\"").append(fieldName).append("\":");
    commaStateMachine = CommaStateMachine.FirstItemSkipComma;
  }

  public void writeString(final String s) {
    maybe_comma();
    sb.append("\"").append(s).append("\"");
  }
}
