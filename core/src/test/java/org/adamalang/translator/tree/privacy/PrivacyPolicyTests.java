/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.privacy;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.junit.Test;

public class PrivacyPolicyTests {
  @Test
  public void coverage() {
    new PrivatePolicy(null).writePrivacyCheckGuard(null, null, null);
    new PrivatePolicy(null).writeTypeReflectionJson(new JsonStreamWriter());
  }
}
