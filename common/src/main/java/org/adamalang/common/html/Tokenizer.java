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
package org.adamalang.common.html;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.PrimitiveIterator;

/** Convert a string into an iterator of tokens */
public class Tokenizer implements Iterator<Token> {
  private final PrimitiveIterator.OfInt codepoints;
  private final LinkedList<Token> buffer;
  private boolean text;
  private boolean quote;
  private final StringBuilder current;
  private int ln;
  private int ch;
  private int p_ln;
  private int p_ch;

  public Tokenizer(PrimitiveIterator.OfInt codepoints) {
    this.codepoints = codepoints;
    this.buffer = new LinkedList<>();
    this.text = true;
    this.quote = false;
    this.current = new StringBuilder();
    this.ln = 0;
    this.ch = 0;
    this.p_ln = 0;
    this.p_ch = 0;
  }

  public static Tokenizer of(String html) {
    return new Tokenizer(html.codePoints().iterator());
  }

  private void push() {
    String val = current.toString();
    if (val.length() == 0) {
      return;
    }
    Type type = Type.Text;
    if (val.startsWith("<!")) {
      type = Type.Comment;
    } else if (val.startsWith("</")) {
      type = Type.ElementClose;
    } else if (val.startsWith("<")) {
      type = Type.ElementOpen;
    }
    // TODO: CDATA, DTD
    buffer.add(new Token(type, val, p_ln, p_ch, ln, ch));
    current.setLength(0);
    p_ln = ln;
    p_ch = ch;
  }

  public void ensure(int count) {
    while (buffer.size() < count && codepoints.hasNext()) {
      int cp = codepoints.nextInt();
      if (text) {
        switch (cp) {
          case '<': {
            if (current.length() > 0) {
              push();
            }
            current.append(Character.toString(cp));
            ch++;
            text = false;
            quote = false;
            break;
          }
          default:
            current.append(Character.toString(cp));
            ch++;
        }
      } else {
        current.append(Character.toString(cp));
        ch++;
        switch (cp) {
          case '"': {
            if (quote) {
              quote = false;
            } else {
              quote = true;
            }
          }
          break;
          case '>': {
            if (!quote) {
              push();
              text = true;
            }
          }
          break;
        }
      }
      if (cp == '\n') {
        ln++;
        ch = 0;
      }
    }
    if (!codepoints.hasNext()) {
      push();
    }
  }

  @Override
  public boolean hasNext() {
    ensure(1);
    return buffer.size() > 0;
  }

  @Override
  public Token next() {
    return buffer.removeFirst();
  }
}
