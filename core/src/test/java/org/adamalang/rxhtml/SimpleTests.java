/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

import org.junit.Test;

public class SimpleTests {

  private static void drive(String rxhtml) {
    System.err.println(RxHtmlTool.convertStringToTemplateForest(rxhtml, (e, x) -> System.err.println(x)).toString());
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
