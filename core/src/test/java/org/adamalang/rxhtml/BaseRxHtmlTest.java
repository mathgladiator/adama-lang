/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

import org.junit.Assert;
import org.junit.Test;

public abstract class BaseRxHtmlTest {
  /** The issues the test is supposed to have */
  public abstract String issues();

  /** the output the test is supposed to generate */
  public abstract String gold();

  /** the source code of the template */
  public abstract String source();

  @Test
  public void stable() throws Exception {
    StringBuilder issuesLive = new StringBuilder();
    Feedback feedback = (element, warning) -> issuesLive.append("WARNING:").append(warning).append("\n");
    String live = RxHtmlTool.convertStringToTemplateForest(source(), feedback);
    Assert.assertEquals(gold(), live.trim());
    Assert.assertEquals(issues(), issuesLive.toString());
  }

  @Test
  public void codegen() throws Exception {
    RxHtmlToAdama.codegen(source());
  }
}
