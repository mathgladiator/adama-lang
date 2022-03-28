package org.adamalang.runtime.sys;

import org.adamalang.runtime.natives.NtClient;

/** wrap common data around a request for policies to exploit */
public class CoreRequestContext {
  public final NtClient who;
  public final String origin;
  public final String ip;
  public final String key;

  public CoreRequestContext(NtClient who, String origin, String ip, String key) {
    this.who = who;
    this.origin = origin;
    this.ip = ip;
    this.key = key;
  }
}
