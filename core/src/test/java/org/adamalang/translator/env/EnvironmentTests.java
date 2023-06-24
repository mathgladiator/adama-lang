/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.env;

import org.adamalang.translator.tree.Document;
import org.junit.Assert;
import org.junit.Test;

public class EnvironmentTests {
  @Test
  public void coverage() {
    final var options = CompilerOptions.start().make();
    final var environment =
        Environment.fresh(
            new Document(), new EnvironmentState(GlobalObjectPool.createPoolWithStdLib(), options));
    Assert.assertNull(environment.getMostRecentReturnType());
    Assert.assertFalse(environment.state.isPure());
    Assert.assertTrue(environment.scopeAsPureFunction().state.isPure());
    Assert.assertTrue(environment.scopeAsPureFunction().scope().state.isPure());
    Assert.assertFalse(environment.state.isTesting());
    Assert.assertTrue(environment.scopeAsUnitTest().state.isTesting());
    Assert.assertTrue(environment.scopeAsUnitTest().scope().state.isTesting());
  }
}
