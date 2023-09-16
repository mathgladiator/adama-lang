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
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class EscapingTests {
  @Test
  public void noop1() {
    Assert.assertEquals("Hi", new Escaping("Hi").go());
  }

  @Test
  public void noop2() {
    Assert.assertEquals("猿も木から落ちる", new Escaping("猿も木から落ちる").go());
  }

  @Test
  public void newline() {
    Assert.assertEquals("hi\\nthere", new Escaping("hi\nthere").toString());
  }

  @Test
  public void return_go() {
    Assert.assertEquals("hithere", new Escaping("hi\rthere").go());
  }

  @Test
  public void return_stay() {
    Assert.assertEquals("hi\\rthere", new Escaping("hi\rthere").keepReturns().toString());
  }

  @Test
  public void return_preserve() {
    Assert.assertEquals("hi\rthere", new Escaping("hi\rthere").keepReturns().dontEscapeReturns().toString());
  }

  @Test
  public void quoting1() {
    Assert.assertEquals("Hi 'there' \\\"yo\\\"", new Escaping("Hi 'there' \"yo\"").go());
  }

  @Test
  public void quoting2() {
    Assert.assertEquals("Hi \\'there\\' \"yo\"", new Escaping("Hi 'there' \"yo\"").switchQuotes().toString());
  }

  @Test
  public void slash() {
    Assert.assertEquals("a \\\\ b", new Escaping("a \\ b").switchQuotes().go());
  }

  @Test
  public void keeplash() {
    Assert.assertEquals("a \\ b", new Escaping("a \\ b").keepSlashes().go());
  }

  @Test
  public void killnewlines() {
    Assert.assertEquals("a  b", new Escaping("a \n b").removeNewLines().go());
  }
}
