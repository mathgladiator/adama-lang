/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.common;

import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class DocumentErrorTests {
  @Test
  public void coverage_NullInputs() {
    try {
      new DocumentError(null, "hi", null);
      Assert.fail();
    } catch (final NullPointerException npe) {}
    try {
      new DocumentError(new DocumentPosition(), null, null);
      Assert.fail();
    } catch (final NullPointerException npe) {}
    try {
      new DocumentError(null, null, null);
      Assert.fail();
    } catch (final NullPointerException npe) {}
  }

  @Test
  public void toLSP() {
    final var error = new DocumentError(new DocumentPosition().ingest(42, 4), "something", null);
    error.json();
  }
}
