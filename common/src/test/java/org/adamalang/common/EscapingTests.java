/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
}
