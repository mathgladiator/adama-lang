/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.common;

import org.junit.Assert;
import org.junit.Test;

public class DocumentErrorTests {
  @Test
  public void coverage_NullInputs() {
    try {
      new DocumentError(null, "hi", null);
      Assert.fail();
    } catch (final NullPointerException npe) {
    }
    try {
      new DocumentError(new DocumentPosition(), null, null);
      Assert.fail();
    } catch (final NullPointerException npe) {
    }
    try {
      new DocumentError(null, null, null);
      Assert.fail();
    } catch (final NullPointerException npe) {
    }
  }

  @Test
  public void toLSP() {
    final var error = new DocumentError(new DocumentPosition().ingest(42, 4, 10), "something", null);
    error.json();
  }
}
