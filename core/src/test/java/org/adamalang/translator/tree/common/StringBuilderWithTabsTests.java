/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.common;

import org.junit.Assert;
import org.junit.Test;

public class StringBuilderWithTabsTests {
  @Test
  public void tabAndTabUpAndTabDownWithNewLines() {
    final var sb = new StringBuilderWithTabs();
    sb.tab().tabUp().writeNewline().tabDown().writeNewline();
    Assert.assertEquals("  \n  \n", sb.toString());
    sb.tabDown().tabDown().tabDown().tabDown().writeNewline();
    Assert.assertEquals("  \n  \n\n", sb.toString());
  }
}
