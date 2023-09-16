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
package org.adamalang.runtime.ops;

import org.adamalang.runtime.json.JsonStreamReader;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class TestReportBuilderTests {
  @Test
  @SuppressWarnings("unchecked")
  public void flow() {
    final var trb = new TestReportBuilder();
    trb.begin("xyz");
    trb.annotate("x", (HashMap<String, Object>) new JsonStreamReader("{}").readJavaTree());
    trb.end(new AssertionStats(50, 0));
    Assert.assertEquals(0, trb.getFailures());
    trb.begin("t2");
    trb.end(new AssertionStats(50, 4));
    Assert.assertEquals(4, trb.getFailures());
    Assert.assertEquals(
        "TEST[xyz] = 100.0%\n" + "TEST[t2] = 92.0% (HAS FAILURES)\n", trb.toString());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void flow2() {
    final var trb = new TestReportBuilder();
    trb.begin("xyz");
    trb.end(new AssertionStats(0, 0));
    trb.begin("zx");
    trb.annotate(
        "dump", (HashMap<String, Object>) new JsonStreamReader("{\"x\":true}").readJavaTree());
    trb.end(new AssertionStats(0, 0));
    Assert.assertEquals(
        "TEST[xyz] HAS NO ASSERTS\n" + "TEST[zx]...DUMP:{\"x\":true}\n" + " HAS NO ASSERTS\n",
        trb.toString());
  }
}
