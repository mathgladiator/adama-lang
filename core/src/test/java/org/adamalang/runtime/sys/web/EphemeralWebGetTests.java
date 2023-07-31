/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.web;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.DelayParent;
import org.adamalang.runtime.remote.RxCache;
import org.junit.Test;

import java.util.TreeMap;

public class EphemeralWebGetTests {
  @Test
  public void trivial() {
    EphemeralWebGet ewg = new EphemeralWebGet(new RxCache(null, null), new WebGet(new WebContext(NtPrincipal.NO_ONE, "origin", "ip"), "uri", new TreeMap<>(), new NtDynamic("{}")), new Callback<WebResponse>() {
      @Override
      public void success(WebResponse value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    }, new DelayParent());
  }
}
