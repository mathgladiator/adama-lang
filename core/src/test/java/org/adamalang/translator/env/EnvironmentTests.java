/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
