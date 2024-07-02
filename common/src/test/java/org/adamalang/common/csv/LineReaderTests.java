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
package org.adamalang.common.csv;

import org.junit.Assert;
import org.junit.Test;

public class LineReaderTests {
  @Test
  public void simple() {
    String[] line = LineReader.parse("a,b,c");
    Assert.assertEquals(3, line.length);
    Assert.assertEquals("a", line[0]);
    Assert.assertEquals("b", line[1]);
    Assert.assertEquals("c", line[2]);
  }
  @Test
  public void quoted1() {
    String[] line = LineReader.parse("\"Comma,Header\",Header2,Header3");
    Assert.assertEquals(3, line.length);
    Assert.assertEquals("Comma,Header", line[0]);
    Assert.assertEquals("Header2", line[1]);
    Assert.assertEquals("Header3", line[2]);
  }

  @Test
  public void quoted2() {
    String[] line = LineReader.parse("Xyz,\"1,2,3\",\"\"\"Hi\"\"\"");
    Assert.assertEquals(3, line.length);
    Assert.assertEquals("Xyz", line[0]);
    Assert.assertEquals("1,2,3", line[1]);
    Assert.assertEquals("\"Hi\"", line[2]);
  }

  @Test
  public void quoted3() {
    String[] line = LineReader.parse("\"xy\"\"abc\"\"\"\"\",x,y");
    Assert.assertEquals(3, line.length);
    Assert.assertEquals("xy\"abc\"\"", line[0]);
    Assert.assertEquals("x", line[1]);
    Assert.assertEquals("y", line[2]);
  }

  @Test
  public void bad_quote() {
    String[] line = LineReader.parse("\"x\"1\",y");
    Assert.assertEquals(2, line.length);
    Assert.assertEquals("x", line[0]);
    Assert.assertEquals(",y", line[1]);
  }

  @Test
  public void solo() {
    String[] line = LineReader.parse("x");
    Assert.assertEquals(1, line.length);
    Assert.assertEquals("x", line[0]);
  }
}
