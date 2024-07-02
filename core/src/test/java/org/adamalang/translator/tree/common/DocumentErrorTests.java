/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.translator.tree.common;

import org.junit.Assert;
import org.junit.Test;

public class DocumentErrorTests {
  @Test
  public void coverage_NullInputs() {
    try {
      new DocumentError(null, "hi");
      Assert.fail();
    } catch (final NullPointerException npe) {
    }
    try {
      new DocumentError(new DocumentPosition(), null);
      Assert.fail();
    } catch (final NullPointerException npe) {
    }
    try {
      new DocumentError(null, null);
      Assert.fail();
    } catch (final NullPointerException npe) {
    }
  }

  @Test
  public void toLSP() {
    final var error = new DocumentError(new DocumentPosition().ingest(42, 4, 10), "something");
    error.json();
  }
}
