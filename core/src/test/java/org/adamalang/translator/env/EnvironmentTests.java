/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.env;

import org.adamalang.translator.tree.Document;
import org.junit.Assert;
import org.junit.Test;

public class EnvironmentTests {
  @Test
  public void coverage() {
    CompilerOptions options = CompilerOptions.start().make();
    Environment environment = Environment.fresh(new Document(), new EnvironmentState(GlobalObjectPool.createPoolWithStdLib(), options));
    Assert.assertNull(environment.getMostRecentReturnType());
    Assert.assertFalse(environment.state.isPure());
    Assert.assertTrue(environment.scopeAsPureFunction().state.isPure());
    Assert.assertTrue(environment.scopeAsPureFunction().scope().state.isPure());
    Assert.assertFalse(environment.state.isTesting());
    Assert.assertTrue(environment.scopeAsUnitTest().state.isTesting());
    Assert.assertTrue(environment.scopeAsUnitTest().scope().state.isTesting());
  }
}
