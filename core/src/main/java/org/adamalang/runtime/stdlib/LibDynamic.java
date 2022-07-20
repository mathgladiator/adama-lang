/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;

import java.util.Map;

public class LibDynamic {
  @Extension
  public static @HiddenType(clazz = String.class)
  NtMaybe<String> str(NtDynamic dyn, String field) {
    if (dyn.cached() instanceof Map) {
      Object value = ((Map<?, ?>) dyn.cached()).get(field);
      if (value instanceof String) {
        return new NtMaybe<>((String) value);
      }
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = NtDynamic.class)
  NtMaybe<NtDynamic> toDynamic(String json) {
    try {
      return new NtMaybe<>(new JsonStreamReader(json).readNtDynamic());
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }
}
