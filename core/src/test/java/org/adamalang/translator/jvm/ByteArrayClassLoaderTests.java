/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.jvm;

import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;

public class ByteArrayClassLoaderTests {
  @Test
  public void coverage() throws Exception {
    final var bacl = new ByteArrayClassLoader(new TreeMap<>());
    try {
      bacl.findClass("Ninja");
      Assert.fail();
    } catch (final ClassNotFoundException cnfe) {
      Assert.assertEquals("Ninja", cnfe.getMessage());
    }
  }
}
