/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
