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
package org.adamalang.translator.parser.token;

import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.tree.SymbolIndex;
import org.adamalang.translator.tree.expressions.constants.TimeSpanConstant;
import org.junit.Assert;
import org.junit.Test;

public class TokenEngineTests {
  @Test
  public void coverage() {
    String xml = "<X>";
    new TokenEngine("demo", xml.codePoints().iterator()).position();
  }

  @Test
  public void timespan_neg() throws Exception {
    TokenEngine engine = new TokenEngine("demo", "@timespan -1 min".codePoints().iterator());
    Scope rootScope = Scope.makeRootDocument();
    Parser p = new Parser(engine, new SymbolIndex(), rootScope);
    TimeSpanConstant tsc = (TimeSpanConstant) p.atomic(rootScope);
    StringBuilder sb = new StringBuilder();
    tsc.emit((t) -> sb.append("[" + t.text + "]"));
    Assert.assertEquals("[@timespan][-1][min]", sb.toString());
  }

  @Test
  public void timespan_neg_space() throws Exception {
    TokenEngine engine = new TokenEngine("demo", "@timespan - 1 min".codePoints().iterator());
    Scope rootScope = Scope.makeRootDocument();
    Parser p = new Parser(engine, new SymbolIndex(), rootScope);
    TimeSpanConstant tsc = (TimeSpanConstant) p.atomic(rootScope);
    StringBuilder sb = new StringBuilder();
    tsc.emit((t) -> sb.append("[" + t.text + "]"));
    Assert.assertEquals("[@timespan][- 1][min]", sb.toString());
  }
}
