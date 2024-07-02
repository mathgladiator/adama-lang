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
package org.adamalang.runtime.json;

import org.junit.Assert;
import org.junit.Test;

public class JsonAlgebra_PatchTests {
  @Test
  public void patch_empties() {
    Object target = of("{}");
    Object patch = of("{}");
    Object result = JsonAlgebra.merge(target, patch, false);
    is("{}", result);
  }

  private Object of(String json) {
    return new JsonStreamReader(json).readJavaTree();
  }

  private void is(String json, Object result) {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.writeTree(result);
    Assert.assertEquals(json, writer.toString());
  }

  @Test
  public void patch_unchanged() {
    Object target = of("{\"x\":123}");
    Object patch = of("{}");
    Object result = JsonAlgebra.merge(target, patch, false);
    is("{\"x\":123}", result);
  }

  @Test
  public void patch_introduce() {
    Object target = of("{}");
    Object patch = of("{\"x\":123}");
    Object result = JsonAlgebra.merge(target, patch, false);
    is("{\"x\":123}", result);
  }

  @Test
  public void patch_delete() {
    Object target = of("{\"x\":123}");
    Object patch = of("{\"x\":null}");
    Object result = JsonAlgebra.merge(target, patch, false);
    is("{}", result);
  }

  @Test
  public void patch_delete_keep() {
    Object target = of("{\"x\":123}");
    Object patch = of("{\"x\":null}");
    Object result = JsonAlgebra.merge(target, patch, true);
    is("{\"x\":null}", result);
  }

  @Test
  public void patch_obj_overwrite() {
    Object target = of("{\"x\":123}");
    Object patch = of("{\"x\":{}}");
    Object result = JsonAlgebra.merge(target, patch, false);
    is("{\"x\":{}}", result);
  }
}
