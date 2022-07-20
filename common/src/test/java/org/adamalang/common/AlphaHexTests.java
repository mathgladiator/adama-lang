/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class AlphaHexTests {
  @Test
  public void coverage() {
    Assert.assertEquals("AA", AlphaHex.encode(new byte[] { 0 }));
    Assert.assertEquals("AB", AlphaHex.encode(new byte[] { 1 }));
    Assert.assertEquals("AC", AlphaHex.encode(new byte[] { 2 }));
    Assert.assertEquals("AD", AlphaHex.encode(new byte[] { 3 }));
    Assert.assertEquals("AE", AlphaHex.encode(new byte[] { 4 }));
    Assert.assertEquals("AF", AlphaHex.encode(new byte[] { 5 }));
    Assert.assertEquals("AG", AlphaHex.encode(new byte[] { 6 }));
    Assert.assertEquals("AH", AlphaHex.encode(new byte[] { 7 }));
    Assert.assertEquals("AI", AlphaHex.encode(new byte[] { 8 }));
    Assert.assertEquals("AJ", AlphaHex.encode(new byte[] { 9 }));
    Assert.assertEquals("AK", AlphaHex.encode(new byte[] { 10 }));
    Assert.assertEquals("AL", AlphaHex.encode(new byte[] { 11 }));
    Assert.assertEquals("AM", AlphaHex.encode(new byte[] { 12 }));
    Assert.assertEquals("AN", AlphaHex.encode(new byte[] { 13 }));
    Assert.assertEquals("AO", AlphaHex.encode(new byte[] { 14 }));
    Assert.assertEquals("AP", AlphaHex.encode(new byte[] { 15 }));
    Assert.assertEquals("BA", AlphaHex.encode(new byte[] { 16 }));
    Assert.assertEquals("BAAEHNBIAMDEHLDDAB", AlphaHex.encode(new byte[] { 16, 4, 125, 24, 12, 52, 123, 51, 1 }));
  }
}
