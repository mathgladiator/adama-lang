/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl;

import org.junit.Test;

public class COE1 {
  @Test
  public void test() {
    String txt = "blad [view:c='{id}'] block [#view:c='{id}'] hidden [/view:c='{id}']";
    Parser.parse(txt);
  }
}
