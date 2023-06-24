/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
