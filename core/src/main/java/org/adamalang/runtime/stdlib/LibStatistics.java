/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
