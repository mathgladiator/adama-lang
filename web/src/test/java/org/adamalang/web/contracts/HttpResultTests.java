/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.contracts;

import org.adamalang.runtime.natives.NtAsset;
import org.junit.Test;

public class HttpResultTests {
  @Test
  public void trivial() {
    HttpHandler.HttpResult a = new HttpHandler.HttpResult("", null, true);
    HttpHandler.HttpResult b = new HttpHandler.HttpResult("space", "key", NtAsset.NOTHING, true);
  }
}
