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
package org.adamalang.language;

import org.adamalang.common.Pathing;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.SymbolIndex;

import java.util.*;

/** an index of all symbols */
public class BuiltSymbolsIndex {
  private final HashSet<String> sources;
  private final HashMap<String, TreeMap<StartPoint, Token>> tokens;
  private final HashMap<String, Token> definitions;

  public BuiltSymbolsIndex(SymbolIndex index) {
    this.sources = new HashSet<>();
    this.tokens = new HashMap<>();
    this.definitions = new HashMap<>();
    for (Token token : index.usages) {
      ingest(token);
    }
    for (Token token : index.definitions) {
      definitions.put(token.text, token);
      ingest(token);
    }
  }

  private void ingest(Token token) {
    TreeMap<StartPoint, Token> bySource = tokens.get(token.sourceName);
    sources.add(token.sourceName);
    if (bySource == null) {
      bySource = new TreeMap<>();
      tokens.put(token.sourceName, bySource);
    }
    bySource.put(new StartPoint(token.lineStart, token.charStart), token);
  }

  public class StartPoint implements Comparable<StartPoint> {
    public final int ln;
    public final int ch;

    public StartPoint(int ln, int ch) {
      this.ln = ln;
      this.ch = ch;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      StartPoint that = (StartPoint) o;
      return ln == that.ln && ch == that.ch;
    }

    @Override
    public int hashCode() {
      return Objects.hash(ln, ch);
    }

    @Override
    public int compareTo(StartPoint o) {
      int delta = Integer.compare(ln, o.ln);
      if (delta != 0) {
        return delta;
      }
      return Integer.compare(ch, o.ch);
    }
  }

  public String findBestMatch(String uri) {
    int winnerLength = 0;
    String winnerFile = null;
    for (String file : sources) {
      String candidate = Pathing.maxSharedSuffix(uri, file);
      int candidateLength = candidate.length();
      if (candidateLength > winnerLength) {
        winnerLength = candidateLength;
        winnerFile = file;
      }
    }
    return winnerFile;
  }

  public Token tokenAt(String file, int ln, int ch) {
    TreeMap<StartPoint, Token> forFile = tokens.get(file);
    if (forFile == null) {
      return null;
    }
    StartPoint search = new StartPoint(ln, ch);
    Map.Entry<StartPoint, Token> floor = forFile.floorEntry(search);
    if (floor != null) {
      Token result = floor.getValue();
      if (result.lineStart <= ln && ln <= result.lineEnd && result.charStart <= ch && ch <= result.charEnd) {
        return result;
      }
    }
    return null;
  }

  public Token findDefinition(Token token) {
    if (token != null) {
      return definitions.get(token.text);
    }
    return null;
  }
}
