/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
