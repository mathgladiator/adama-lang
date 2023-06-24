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
