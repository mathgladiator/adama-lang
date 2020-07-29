/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtList;
import org.adamalang.translator.reflect.HiddenType;
import org.adamalang.translator.reflect.UseName;

/** very simple statistics */
public class LibStatistics {
  @UseName(name = "average")
  public static double avgDoubles(@HiddenType(clazz = Double.class) final NtList<Double> list) {
    if (list.size() == 0) { return 0.0; }
    var sum = 0D;
    for (final Double x : list) {
      sum += x;
    }
    return sum / list.size();
  }

  @UseName(name = "average")
  public static double avgInts(@HiddenType(clazz = Integer.class) final NtList<Integer> list) {
    if (list.size() == 0) { return 0.0; }
    var sum = 0.0;
    for (final Integer x : list) {
      sum += x;
    }
    return sum / list.size();
  }

  @UseName(name = "sum")
  public static double sumDoubles(@HiddenType(clazz = Double.class) final NtList<Double> list) {
    var sum = 0D;
    for (final Double x : list) {
      sum += x;
    }
    return sum;
  }

  @UseName(name = "sum")
  public static int sumInts(@HiddenType(clazz = Integer.class) final NtList<Integer> list) {
    var sum = 0;
    for (final Integer x : list) {
      sum += x;
    }
    return sum;
  }
}
