/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;
import org.adamalang.translator.reflect.UseName;

import java.util.Arrays;

/** very simple statistics */
public class LibStatistics {
  @UseName(name = "average")
  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> avgDoubles(@HiddenType(clazz = Double.class) final NtList<Double> list) {
    if (list.size() > 0) {
      var sum = 0D;
      for (final Double x : list) {
        sum += x;
      }
      return new NtMaybe<>(sum / list.size());
    }
    return new NtMaybe<>();
  }

  @UseName(name = "average")
  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> avgInts(@HiddenType(clazz = Integer.class) final NtList<Integer> list) {
    if (list.size() > 0) {
      double sum = 0.0;
      for (final Integer x : list) {
        sum += x;
      }
      return new NtMaybe<>(sum / list.size());
    }
    return new NtMaybe<>();
  }

  @UseName(name = "average")
  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> avgLongs(@HiddenType(clazz = Long.class) final NtList<Long> list) {
    if (list.size() > 0) {
      double sum = 0.0;
      for (final Long x : list) {
        sum += x;
      }
      return new NtMaybe<>(sum / list.size());
    }
    return new NtMaybe<>();
  }

  @UseName(name = "sum")
  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> sumDoubles(@HiddenType(clazz = Double.class) final NtList<Double> list) {
    if (list.size() > 0) {
      var sum = 0D;
      for (final Double x : list) {
        sum += x;
      }
      return new NtMaybe<>(sum);
    }
    return new NtMaybe<>();
  }

  @UseName(name = "sum")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> sumInts(@HiddenType(clazz = Integer.class) final NtList<Integer> list) {
    if (list.size() > 0) {
      var sum = 0;
      for (final Integer x : list) {
        sum += x;
      }
      return new NtMaybe<>(sum);
    }
    return new NtMaybe<>();
  }

  @UseName(name = "sum")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> sumLongs(@HiddenType(clazz = Long.class) final NtList<Long> list) {
    if (list.size() > 0) {
      var sum = 0L;
      for (final Long x : list) {
        sum += x;
      }
      return new NtMaybe<>(sum);
    }
    return new NtMaybe<>();
  }

  @UseName(name = "maximum")
  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> maxDoubles(@HiddenType(clazz = Double.class) final NtList<Double> list) {
    if (list.size() > 0) {
      double val = list.lookup(0).get();
      for (final Double x : list) {
        if (x > val) {
          val = x;
        }
      }
      return new NtMaybe<>(val);
    }
    return new NtMaybe<>();
  }

  @UseName(name = "maximum")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> maxInts(@HiddenType(clazz = Integer.class) final NtList<Integer> list) {
    if (list.size() > 0) {
      int val = list.lookup(0).get();
      for (final Integer x : list) {
        if (x > val) {
          val = x;
        }
      }
      return new NtMaybe<>(val);
    }
    return new NtMaybe<>();
  }

  @UseName(name = "maximum")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> maxLongs(@HiddenType(clazz = Long.class) final NtList<Long> list) {
    if (list.size() > 0) {
      long val = list.lookup(0).get();
      for (final Long x : list) {
        if (x > val) {
          val = x;
        }
      }
      return new NtMaybe<>(val);
    }
    return new NtMaybe<>();
  }

  @UseName(name = "minimum")
  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> minDoubles(@HiddenType(clazz = Double.class) final NtList<Double> list) {
    if (list.size() > 0) {
      double val = list.lookup(0).get();
      for (final Double x : list) {
        if (x < val) {
          val = x;
        }
      }
      return new NtMaybe<>(val);
    }
    return new NtMaybe<>();
  }

  @UseName(name = "minimum")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> minInts(@HiddenType(clazz = Integer.class) final NtList<Integer> list) {
    if (list.size() > 0) {
      int val = list.lookup(0).get();
      for (final Integer x : list) {
        if (x < val) {
          val = x;
        }
      }
      return new NtMaybe<>(val);
    } else {
      return new NtMaybe<>();
    }
  }

  @UseName(name = "minimum")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> minLongs(@HiddenType(clazz = Long.class) final NtList<Long> list) {
    if (list.size() > 0) {
      long val = list.lookup(0).get();
      for (final Long x : list) {
        if (x < val) {
          val = x;
        }
      }
      return new NtMaybe<>(val);
    }
    return new NtMaybe<>();
  }

  @UseName(name = "median")
  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> medianDoubles(@HiddenType(clazz = Double.class) final NtList<Double> list) {
    // TODO: I HATE this because we can do it better, but need to import a library
    if (list.size() > 0) {
      Double[] values = list.toArray((n) -> new Double[n]);
      Arrays.sort(values);
      int index = values.length / 2;
      if (values.length % 2 == 0) {
        return new NtMaybe<>((values[index] + values[index - 1]) / 2.0);
      } else {
        return new NtMaybe<>(values[index]);
      }
    }
    return new NtMaybe<>();
  }

  @UseName(name = "median")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> medianInts(@HiddenType(clazz = Integer.class) final NtList<Integer> list) {
    // TODO: I HATE this because we can do it better, but need to import a library
    if (list.size() > 0) {
      Integer[] values = list.toArray((n) -> new Integer[n]);
      Arrays.sort(values);
      int index = values.length / 2;
      if (values.length % 2 == 0) {
        return new NtMaybe<>((values[index] + values[index - 1]) / 2);
      } else {
        return new NtMaybe<>(values[index]);
      }
    }
    return new NtMaybe<>();
  }

  @UseName(name = "median")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> medianLongs(@HiddenType(clazz = Long.class) final NtList<Long> list) {
    // TODO: I HATE this because we can do it better, but need to import a library
    if (list.size() > 0) {
      Long[] values = list.toArray((n) -> new Long[n]);
      Arrays.sort(values);
      int index = values.length / 2;
      if (values.length % 2 == 0) {
        return new NtMaybe<>((values[index] + values[index - 1]) / 2);
      } else {
        return new NtMaybe<>(values[index]);
      }
    }
    return new NtMaybe<>();
  }

  @UseName(name = "percentile")
  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> percentileDoubles(@HiddenType(clazz = Double.class) final NtList<Double> list, double percent) {
    // TODO: I HATE this because we can do it better, but need to import a library
    if (list.size() > 0 && percent >= 0.0 && percent <= 1.0) {
      Double[] values = list.toArray((n) -> new Double[n]);
      Arrays.sort(values);
      return new NtMaybe<>(values[(int) (values.length * percent)]);
    }
    return new NtMaybe<>();
  }

  @UseName(name = "percentile")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> percentileInts(@HiddenType(clazz = Integer.class) final NtList<Integer> list, double percent) {
    // TODO: I HATE this because we can do it better, but need to import a library
    if (list.size() > 0 && percent >= 0.0 && percent <= 1.0) {
      Integer[] values = list.toArray((n) -> new Integer[n]);
      Arrays.sort(values);
      return new NtMaybe<>(values[(int) (values.length * percent)]);
    }
    return new NtMaybe<>();
  }

  @UseName(name = "percentile")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> percentileLongs(@HiddenType(clazz = Long.class) final NtList<Long> list, double percent) {
    // TODO: I HATE this because we can do it better, but need to import a library
    if (list.size() > 0 && percent >= 0.0 && percent <= 1.0) {
      Long[] values = list.toArray((n) -> new Long[n]);
      Arrays.sort(values);
      return new NtMaybe<>(values[(int) (values.length * percent)]);
    }
    return new NtMaybe<>();
  }
}
