/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtTime;
import org.adamalang.runtime.natives.NtTimeSpan;
import org.adamalang.translator.reflect.Extension;

public class LibTime {
  @Extension
  public static int toInt(NtTime t) {
    return t.toInt();
  }

  @Extension
  public static NtTime extendWithinDay(NtTime t, NtTimeSpan s) {
    int end = ((int) (t.toInt() * 60 + s.seconds))/60;
    if (end >= 1440) end = 1439;
    if (end < 0) end = 0;
    return new NtTime(end / 60, end % 60);
  }

  public static boolean overlaps(NtTime aStart, NtTime aEnd, NtTime bStart, NtTime bEnd) {
    return LibMath.intersects(aStart.toInt(), aEnd.toInt(), bStart.toInt(), bEnd.toInt());
  }
}
