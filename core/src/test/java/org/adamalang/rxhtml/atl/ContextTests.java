/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl;

import org.junit.Assert;
import org.junit.Test;

public class ContextTests {
  @Test
  public void flow() {
    Context context = Context.makeClassContext();
    context.cssTrack("fragment");
    context.cssTrack("fragment");
    context.cssTrack("nope");
    Assert.assertEquals(2, (int) context.freq.get("fragment"));
    Assert.assertEquals(1, (int) context.freq.get("nope"));
    Assert.assertNull(Context.DEFAULT.freq);
  }
}
