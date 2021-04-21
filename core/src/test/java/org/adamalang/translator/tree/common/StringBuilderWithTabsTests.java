/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
