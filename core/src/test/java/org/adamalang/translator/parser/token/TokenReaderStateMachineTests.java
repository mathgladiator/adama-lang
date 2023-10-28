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

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TokenReaderStateMachineTests {
  @Test
  public void symbol_cluster() throws Exception {
    final var list = new ArrayList<Token>();
    final var trsm = new TokenReaderStateMachine("Source", list::add);
    trsm.consume('+');
    trsm.consume('+');
    trsm.consume('+');
    trsm.consume('+');
    trsm.consume('1');
    Assert.assertEquals(4, list.size());
  }

  @Test
  public void template_1() throws Exception {
    final var list = new ArrayList<Token>();
    final var trsm = new TokenReaderStateMachine("Source", list::add);
    for (int cp : "`a`HI\nTHERE`a`".codePoints().toArray()) {
      trsm.consume(cp);
    }
    Assert.assertEquals(1, list.size());
    Token token = list.get(0);
    Assert.assertEquals(MajorTokenType.Template, token.majorType);
    Assert.assertEquals("`a`HI\nTHERE`a`", token.text);
  }

  @Test
  public void template_not_closed() throws Exception {
    final var list = new ArrayList<Token>();
    final var trsm = new TokenReaderStateMachine("Source", list::add);
    for (int cp : "`a`HI THERE`b`".codePoints().toArray()) {
      trsm.consume(cp);
    }
    Assert.assertEquals(0, list.size());
  }
}
