/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.template;

import org.adamalang.rxhtml.template.config.Feedback;
import org.junit.Assert;
import org.junit.Test;

public class EnvironmentTests {
  @Test
  public void sanity() {
    Environment env = Environment.fresh(Feedback.NoOp);
    env = env.parentVariable("pv");
    Assert.assertEquals("pv", env.parentVariable);
  }
}
