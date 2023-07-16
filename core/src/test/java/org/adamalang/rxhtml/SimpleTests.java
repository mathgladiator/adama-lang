/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

import org.adamalang.rxhtml.template.config.ShellConfig;
import org.junit.Test;

public class SimpleTests {

  private static void drive(String rxhtml) {
    System.err.println(RxHtmlTool.convertStringToTemplateForest(rxhtml, ShellConfig.start().withFeedback((e, x) -> System.err.println(x)).end()));
  }

  @Test
  public void emptyvalue() {
    drive("<template name=\"foo\">how<input checked /></template>");
  }

  @Test
  public void basic() {
    drive("<template name=\"foo\">how<b class=\"foo bar\">d</b>y<img src=\"imgurl\"/></template>");
  }

  @Test
  public void single_var() {
    drive("<template name=\"foo\"><lookup name=\"x\"/></template>");
  }

  @Test
  public void repeat_var() {
    drive("<template name=\"foo\"><lookup name=\"x\"/><lookup name=\"x\"/><lookup name=\"x\"/></template>");
  }

  @Test
  public void sanityStyle() {
    drive("<forest><style>XYZ</style></forest>");
  }
}
