/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
