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
  public static @HiddenType(clazz = String.class) NtMaybe<String> str(NtDynamic dyn, String field) {
    if (dyn.cached() instanceof Map) {
      Object value = ((Map<?, ?>) dyn.cached()).get(field);
      if (value instanceof String) {
        return new NtMaybe<>((String) value);
      }
      if (value instanceof Double || value instanceof Integer || value instanceof Long) {
        return new NtMaybe<>("" + value);
      }
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> i(NtDynamic dyn, String field) {
    if (dyn.cached() instanceof Map) {
      Object value = ((Map<?, ?>) dyn.cached()).get(field);
      if (value instanceof String) {
        try {
          return new NtMaybe<>(Integer.parseInt((String) value));
        } catch (NumberFormatException nfe) {
          return new NtMaybe<>();
        }
      }
      if (value instanceof Integer) {
        return new NtMaybe<>((Integer) value);
      }
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> l(NtDynamic dyn, String field) {
    if (dyn.cached() instanceof Map) {
      Object value = ((Map<?, ?>) dyn.cached()).get(field);
      if (value instanceof String) {
        try {
          return new NtMaybe<>(Long.parseLong((String) value));
        } catch (NumberFormatException nfe) {
          return new NtMaybe<>();
        }
      }
      if (value instanceof Integer) {
        return new NtMaybe<>((long) ((Integer) value));
      }
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = String.class) NtMaybe<Double> d(NtDynamic dyn, String field) {
    if (dyn.cached() instanceof Map) {
      Object value = ((Map<?, ?>) dyn.cached()).get(field);
      if (value instanceof String) {
        try {
          return new NtMaybe<>(Double.parseDouble((String) value));
        } catch (NumberFormatException nfe) {
          return new NtMaybe<>();
        }
      }
      if (value instanceof Double) {
        return new NtMaybe<>((Double) value);
      }
      if (value instanceof Integer) {
        return new NtMaybe<>((double)((Integer) value));
      }
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Boolean.class) NtMaybe<Boolean> b(NtDynamic dyn, String field) {
    if (dyn.cached() instanceof Map) {
      Object value = ((Map<?, ?>) dyn.cached()).get(field);
      if (value instanceof String) {
        if ("true".equals(value)) {
          return new NtMaybe<>(true);
        }
        if ("false".equals(value)) {
          return new NtMaybe<>(false);
        }
        return new NtMaybe<>();
      }
      if (value instanceof Boolean) {
        return new NtMaybe<>((Boolean) value);
      }
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Boolean.class) NtMaybe<Boolean> is_null(NtDynamic dyn, String field) {
    if (dyn.cached() instanceof Map) {
      if (((Map<?, ?>) dyn.cached()).containsKey(field)) {
        return new NtMaybe<>(((Map<?, ?>) dyn.cached()).get(field) == null);
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
