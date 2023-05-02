/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
