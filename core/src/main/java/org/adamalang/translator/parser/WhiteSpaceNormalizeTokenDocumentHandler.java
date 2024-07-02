/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.translator.parser;

import org.adamalang.translator.parser.token.MajorTokenType;
import org.adamalang.translator.parser.token.Token;

import java.util.ArrayList;

public class WhiteSpaceNormalizeTokenDocumentHandler extends TokenDocumentHandler {
  public final StringBuilder builder = new StringBuilder();

  private static ArrayList<Token> normalize(ArrayList<Token> list) {
    if (list == null) {
      return null;
    }
    ArrayList<Token> next = new ArrayList<>();
    for(Token token : list) {
      if (token.majorType == MajorTokenType.Whitespace) {
        next.add(token.cloneWithNewText(" "));
      } else {
        next.add(token);
      }
    }
    return next;
  }

  private static ArrayList<Token> remove(ArrayList<Token> list) {
    if (list == null) {
      return null;
    }
    ArrayList<Token> next = new ArrayList<>();
    for(Token token : list) {
      if (token.majorType != MajorTokenType.Whitespace) {
        next.add(token);
      }
    }
    return next;
  }

  public static void normalize(Token token) {
    token.nonSemanticTokensAfter = normalize(token.nonSemanticTokensAfter);
    token.nonSemanticTokensPrior = normalize(token.nonSemanticTokensPrior);
  }

  public static void remove(Token token) {
    token.nonSemanticTokensAfter = remove(token.nonSemanticTokensAfter);
    token.nonSemanticTokensPrior = remove(token.nonSemanticTokensPrior);
  }

  @Override
  public void accept(final Token token) {
    if (token.isSymbolWithTextEq(";")) {
      remove(token);
    } else {
      normalize(token);
    }
  }
}
