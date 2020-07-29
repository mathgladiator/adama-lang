/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.parser.token;

import org.junit.Assert;
import org.junit.Test;

public class MinorTokenTypeTests {
  @Test
  public void coverage() {
    for (final MinorTokenType mtt : MinorTokenType.values()) {
      Assert.assertEquals(mtt, MinorTokenType.valueOf(mtt.name()));
    }
  }
}
