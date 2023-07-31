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
