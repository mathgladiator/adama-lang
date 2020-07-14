/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.jvm;

import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;

public class ByteArrayClassLoaderTests {
  @Test
  public void coverage() throws Exception {
    ByteArrayClassLoader bacl = new ByteArrayClassLoader(new TreeMap<>());
    try {
      bacl.findClass("Ninja");
      Assert.fail();
    } catch (ClassNotFoundException cnfe) {
      Assert.assertEquals("Ninja", cnfe.getMessage());
    }
  }
}
