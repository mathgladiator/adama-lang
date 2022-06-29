package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.translator.reflect.Extension;

import java.util.Map;

public class LibDynamic {
  @Extension
  public NtMaybe<String> str(NtDynamic dyn, String field) {
    if (dyn.cached() instanceof Map) {
      Object value = ((Map<?, ?>) dyn.cached()).get(field);
      if (value instanceof String) {
        return new NtMaybe<>((String) value);
      }
    }
    return new NtMaybe<>();
  }
}
