/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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

import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.NtTime;
import org.adamalang.runtime.natives.NtTimeSpan;
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;

public class LibTime {
  @Extension
  public static int toInt(NtTime t) {
    return t.toInt();
  }

  @Extension
  public static NtTime extendWithinDay(NtTime t, NtTimeSpan s) {
    int end = ((int) (t.toInt() * 60 + s.seconds)) / 60;
    if (end >= 1440) end = 1439;
    if (end < 0) end = 0;
    return new NtTime(end / 60, end % 60);
  }

  @Extension
  public static NtTime cyclicAdd(NtTime t, NtTimeSpan s) {
    int next = ((int) (t.toInt() * 60 + s.seconds)) / 60;
    next %= 1400;
    if (next < 0) {
      next += 1400;
    }
    return new NtTime(next / 60, next % 60);
  }

  public static @HiddenType(clazz = NtTime.class) NtMaybe<NtTime> make(int hr, int min) {
    if (0 <= hr && hr <= 23 && 0 <= min && min <= 59) {
      return new NtMaybe<>(new NtTime(hr, min));
    } else {
      return new NtMaybe<>();
    }
  }

  public static boolean overlaps(NtTime aStart, NtTime aEnd, NtTime bStart, NtTime bEnd) {
    return LibMath.intersects(aStart.toInt(), aEnd.toInt(), bStart.toInt(), bEnd.toInt());
  }
}
