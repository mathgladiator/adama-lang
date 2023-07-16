/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

import org.adamalang.rxhtml.template.config.Feedback;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
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
    String live = RxHtmlTool.convertStringToTemplateForest(source(), ShellConfig.start().withFeedback(feedback).end()).toString();
    Assert.assertEquals(gold(), live.trim());
    Assert.assertEquals(issues().trim(), issuesLive.toString().trim());
  }

  @Test
  public void codegen() throws Exception {
    String adamaCode = RxHtmlToAdama.codegen(source());
    Parser parser = new Parser(new TokenEngine("test", adamaCode.codePoints().iterator()));
    parser.document();
  }
}
