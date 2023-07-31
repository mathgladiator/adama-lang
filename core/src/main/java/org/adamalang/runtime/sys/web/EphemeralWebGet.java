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
import org.adamalang.runtime.remote.DelayParent;
import org.adamalang.runtime.remote.RxCache;

public class EphemeralWebGet {
  public final RxCache cache;
  public final WebGet get;
  public final Callback<WebResponse> callback;
  public final DelayParent delay;

  public EphemeralWebGet(RxCache cache, WebGet get, Callback<WebResponse> callback, DelayParent delay) {
    this.cache = cache;
    this.get = get;
    this.callback = callback;
    this.delay = delay;
  }
}
