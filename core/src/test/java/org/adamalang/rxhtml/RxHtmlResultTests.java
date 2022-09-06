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

import org.adamalang.rxhtml.template.Shell;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class RxHtmlResultTests {
  @Test
  public void testing() {
    ArrayList<String> patterns = new ArrayList<>();
    patterns.add("/hi/there");
    patterns.add("/hi/$name/ok");
    RxHtmlResult result = new RxHtmlResult("js", "css", new Shell(Feedback.NoOp), patterns);
    Assert.assertFalse(result.test("/"));
    Assert.assertFalse(result.test("/hi"));
    Assert.assertFalse(result.test("/hi/nope"));
    Assert.assertTrue(result.test("/hi/there"));
    Assert.assertFalse(result.test("/hi/xyz/nope"));
    Assert.assertTrue(result.test("/hi/xyz/ok"));
    result.toString();
  }
}
