/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml.template;

import org.junit.Assert;
import org.junit.Test;

public class StatePathTests {
  @Test
  public void simple() {
    StatePath sp = StatePath.resolve("simple", "S");
    Assert.assertEquals("S", sp.command);
    Assert.assertEquals("simple", sp.name);
    Assert.assertTrue(sp.simple);
  }

  @Test
  public void view_simple() {
    StatePath sp = StatePath.resolve("view:name", "S");
    Assert.assertEquals("$.pV(S)", sp.command);
    Assert.assertEquals("name", sp.name);
    Assert.assertFalse(sp.simple);
  }

  @Test
  public void dive() {
    StatePath sp = StatePath.resolve("path/simple", "S");
    Assert.assertEquals("$.pI(S,'path')", sp.command);
    Assert.assertEquals("simple", sp.name);
    Assert.assertFalse(sp.simple);
  }

  @Test
  public void multidive() {
    StatePath sp = StatePath.resolve("path1/path2/name", "S");
    Assert.assertEquals("$.pI($.pI(S,'path1'),'path2')", sp.command);
    Assert.assertEquals("name", sp.name);
    Assert.assertFalse(sp.simple);
  }

  @Test
  public void root() {
    StatePath sp = StatePath.resolve("/name", "S");
    Assert.assertEquals("$.pR(S)", sp.command);
    Assert.assertEquals("name", sp.name);
    Assert.assertFalse(sp.simple);
  }

  @Test
  public void root_down_up() {
    StatePath sp = StatePath.resolve("/down/../name", "S");
    Assert.assertEquals("$.pU($.pI($.pR(S),'down'))", sp.command);
    Assert.assertEquals("name", sp.name);
    Assert.assertFalse(sp.simple);
  }

  @Test
  public void dive_up() {
    StatePath sp = StatePath.resolve("path1/../name", "S");
    Assert.assertEquals("$.pU($.pI(S,'path1'))", sp.command);
    Assert.assertEquals("name", sp.name);
    Assert.assertFalse(sp.simple);
  }
}
