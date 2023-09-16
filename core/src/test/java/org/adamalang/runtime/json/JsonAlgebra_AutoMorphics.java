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
package org.adamalang.runtime.json;

import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.junit.Assert;
import org.junit.Test;

public class JsonAlgebra_AutoMorphics {
  @Test
  public void merge_stream() {
    AutoMorphicAccumulator<String> accum = JsonAlgebra.mergeAccumulator();
    Assert.assertTrue(accum.empty());
    accum.next("{\"x\":1}");
    Assert.assertFalse(accum.empty());
    accum.next("{\"x\":2}");
    accum.next("{\"x\":3}");
    accum.next("{\"x\":4}");
    Assert.assertEquals("{\"x\":4}", accum.finish());
  }
}
