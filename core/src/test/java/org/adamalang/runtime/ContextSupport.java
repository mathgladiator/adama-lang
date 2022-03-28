package org.adamalang.runtime;

import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.CoreRequestContext;

public class ContextSupport {
  public static CoreRequestContext WRAP(NtClient who) {
    return new CoreRequestContext(who, "test", "123.0.0.1", "key");
  }
}
