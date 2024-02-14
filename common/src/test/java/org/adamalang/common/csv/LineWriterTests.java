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
package org.adamalang.common.csv;

import org.junit.Assert;
import org.junit.Test;

public class LineWriterTests {
  @Test
  public void simple() {
    LineWriter lw = new LineWriter();
    lw.write(123);
    lw.write(3.14);
    lw.write("xyz");
    Assert.assertEquals("123,3.14,xyz", lw.toString());
  }

  @Test
  public void quote1() {
    LineWriter lw = new LineWriter();
    lw.write("Comma,Header");
    lw.write("Header2");
    lw.write("Header3");
    Assert.assertEquals("\"Comma,Header\",Header2,Header3", lw.toString());
  }

  @Test
  public void quote2() {
    LineWriter lw = new LineWriter();
    lw.write("Value with comma(,) and double quote(\")");
    lw.write("Value2");
    lw.write("Value3");
    Assert.assertEquals("\"Value with comma(,) and double quote(\"\")\",Value2,Value3", lw.toString());
  }

  @Test
  public void quote3() {
    LineWriter lw = new LineWriter();
    lw.write("xy\"abc\"\"");
    lw.write("x");
    lw.write("y");
    Assert.assertEquals("\"xy\"\"abc\"\"\"\"\",x,y", lw.toString());
  }

  @Test
  public void quote4() {
    LineWriter lw = new LineWriter();
    lw.write("new\nline");
    lw.write("a");
    lw.write("b");
    Assert.assertEquals("\"new\nline\",a,b", lw.toString());
  }
}
