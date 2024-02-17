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
package org.adamalang.translator.tree.privacy;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.TokenizedItem;

import java.util.ArrayList;
import java.util.function.Consumer;

/** a way to indicate that a bubble is protected by data */
public class Guard extends DocumentPosition {
  public final Token open;
  public final ArrayList<TokenizedItem<String>> policies;
  public final Token close;

  public Guard(Token open, ArrayList<TokenizedItem<String>> policies, Token close) {
    this.open = open;
    this.policies = policies;
    this.close = close;
    ingest(open);
    ingest(close);
  }

  public void emit(final Consumer<Token> yielder) {
    yielder.accept(open);
    for (TokenizedItem<String> policy : policies) {
      policy.emitBefore(yielder);
      policy.emitAfter(yielder);
    }
    yielder.accept(close);
  }

  public void format(Formatter formatter) {
  }

  public void writeReflect(JsonStreamWriter writer) {
    writer.beginArray();
    for (TokenizedItem<String> policy : policies) {
      writer.writeString(policy.item);
    }
    writer.endArray();
  }
}
