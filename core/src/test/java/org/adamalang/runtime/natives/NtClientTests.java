/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class NtClientTests {
  @Test
  public void comparisons() {
    final var cv1 = new NtClient("a", "b");
    final var cv2 = new NtClient("b", "b");
    final var cv3 = new NtClient("b", "a");
    final var cv4 = new NtClient("b", "c");
    Assert.assertEquals(-1, cv1.compareTo(cv2));
    Assert.assertEquals(1, cv1.compareTo(cv3));
    Assert.assertEquals(-1, cv1.compareTo(cv4));
    Assert.assertEquals(1, cv2.compareTo(cv1));
    Assert.assertEquals(1, cv2.compareTo(cv3));
    Assert.assertEquals(-1, cv2.compareTo(cv4));
    Assert.assertEquals(-1, cv3.compareTo(cv1));
    Assert.assertEquals(-1, cv3.compareTo(cv2));
    Assert.assertEquals(-2, cv3.compareTo(cv4));
    Assert.assertEquals(1, cv4.compareTo(cv1));
    Assert.assertEquals(1, cv4.compareTo(cv2));
    Assert.assertEquals(2, cv4.compareTo(cv3));
    Assert.assertEquals(0, cv1.compareTo(cv1));
    Assert.assertEquals(0, cv2.compareTo(cv2));
    Assert.assertEquals(0, cv3.compareTo(cv3));
    Assert.assertEquals(0, cv4.compareTo(cv4));
    Assert.assertFalse(cv1.equals(cv2));
    Assert.assertFalse(cv1.equals(cv3));
    Assert.assertFalse(cv1.equals(cv4));
    Assert.assertFalse(cv2.equals(cv1));
    Assert.assertFalse(cv2.equals(cv3));
    Assert.assertFalse(cv2.equals(cv4));
    Assert.assertFalse(cv3.equals(cv1));
    Assert.assertFalse(cv3.equals(cv2));
    Assert.assertFalse(cv3.equals(cv4));
    Assert.assertFalse(cv4.equals(cv1));
    Assert.assertFalse(cv4.equals(cv2));
    Assert.assertFalse(cv4.equals(cv3));
    Assert.assertTrue(cv1.equals(cv1));
    Assert.assertTrue(cv2.equals(cv2));
    Assert.assertTrue(cv3.equals(cv3));
    Assert.assertTrue(cv4.equals(cv4));
    Assert.assertFalse(cv4.equals("sys"));
  }

  @Test
  public void coverage() {
    NtClient.NO_ONE.toString();
  }
}
