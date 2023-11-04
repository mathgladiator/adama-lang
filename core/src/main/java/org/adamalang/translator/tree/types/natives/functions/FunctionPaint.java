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
package org.adamalang.translator.tree.types.natives.functions;

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.Formatter;

import java.util.function.Consumer;

/** painting of a function with various properties */
public class FunctionPaint {
  private static final Token[] EMPTY_TOKENS = new Token[0];
  private final Token[] tokens;
  public final boolean pure;
  public final boolean castArgs;
  public final boolean castReturn;
  public final boolean aborts;
  public final boolean viewer;

  public FunctionPaint(final boolean pure, final boolean castArgs, final boolean castReturn, final boolean aborts) {
    this.tokens = EMPTY_TOKENS;
    this.pure = pure;
    this.castArgs = castArgs;
    this.castReturn = castReturn;
    this.aborts = aborts;
    this.viewer = false;
  }

  public FunctionPaint(Token... tokens) {
    this.tokens = tokens;
    boolean _pure = false;
    boolean _aborts = false;
    boolean _viewer = false;
    for (Token token : tokens) {
      if (token.text.equals("readonly")) {
        _pure = true;
      }
      if (token.text.equals("aborts")) {
        _aborts = true;
      }
      if (token.text.equals("viewer")) {
        _viewer = true;
      }
    }
    this.pure = _pure;
    this.castArgs = false;
    this.castReturn = false;
    this.aborts = _aborts;
    this.viewer = _viewer;
  }

  public void emit(Consumer<Token> yielder) {
    for (Token token : tokens) {
      yielder.accept(token);
    }
  }

  public void format(Formatter formatter) {
  }

  public static final FunctionPaint READONLY_NORMAL = new FunctionPaint(true, false, false, false);
  public static final FunctionPaint CAST_NORMAL = new FunctionPaint(false, true, false, false);
  public static final FunctionPaint NORMAL = new FunctionPaint(false, false, false, false);
  public static final FunctionPaint READONLY_CAST = new FunctionPaint(true, true, false, false);
}
