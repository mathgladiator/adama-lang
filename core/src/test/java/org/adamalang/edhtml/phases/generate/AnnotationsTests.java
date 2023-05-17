/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.edhtml.phases.generate;

import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationsTests {
  @Test
  public void flow() {
    Annotations an = new Annotations();
    Assert.assertFalse(an.is("x", "y"));
    Assert.assertFalse(an.has("x"));
    Annotations ch = an.of(Json.parseJsonArray("[\"XYZ\",{\"x\":\"y\"}]"));
    Assert.assertTrue(ch.is("x", "y"));
    Assert.assertTrue(ch.has("x"));
    Assert.assertTrue(ch.has("XYZ"));
    Assert.assertTrue(Annotations.union(an, ch).is("x", "y"));
  }
}
