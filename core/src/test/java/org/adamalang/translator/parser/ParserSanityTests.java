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
package org.adamalang.translator.parser;

import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.parser.token.TokenEngine;
import org.junit.Test;

public class ParserSanityTests {
  @Test
  public void testGithub138_a() throws Exception {
    Parser p = new Parser(new TokenEngine("source", "procedure foo(complex x) -> double { return x.re();//\n}".codePoints().iterator()), Scope.makeRootDocument());
    p.document();
  }

  @Test
  public void testGithub138_b() throws Exception {
    Parser p = new Parser(new TokenEngine("source", "procedure foo(complex x) -> double { return x.re();/* yay */\n}".codePoints().iterator()), Scope.makeRootDocument());
    p.document();
  }
}
