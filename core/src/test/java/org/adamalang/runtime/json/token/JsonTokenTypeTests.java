/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json.token;

import org.junit.Assert;
import org.junit.Test;

public class JsonTokenTypeTests {
  @Test
  public void coverage() {
    Assert.assertEquals(JsonTokenType.Null, JsonTokenType.valueOf("Null"));
  }
}
