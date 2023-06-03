/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.apikit.model;

import org.junit.Assert;
import org.junit.Test;

public class CommonTests {
  @Test
  public void validate() {
    Assert.assertEquals("IAmHere", Common.camelize("i-am-here"));
    Assert.assertEquals("iAmHere", Common.camelize("i-am-here", true));
    Assert.assertEquals("NopeNope", Common.camelize("nope/nope"));
    Assert.assertEquals("nopeNopeFortyTwo", Common.camelize("nope/nope-forty-two", true));
  }
}
