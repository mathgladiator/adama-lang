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

import org.adamalang.runtime.natives.NtDate;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;
import org.adamalang.translator.reflect.UseName;

import java.time.LocalDate;
import java.util.ArrayList;

public class LibDate {

  @UseName(name = "calendarViewOf")
  @Extension
  public static @HiddenType(clazz = NtDate.class) NtList<NtDate> calendarViewOf(NtDate day) {
    ArrayList<NtDate> dates = new ArrayList<>();

    // get the first of the given month
    LocalDate first = LocalDate.of(day.year, day.month, 1);
    // Monday -> 1, Sunday -> 7; transform this to days prior to the start of the month
    int offset = (first.getDayOfWeek().getValue() + 0) % 7;

    // The actual start date of the calendar.
    LocalDate start = first.plusDays(-offset);

    { // add the days prior to the month
      for (int k = offset; k > 0; k--) {
        LocalDate at = first.plusDays(-k);
        dates.add(new NtDate(at.getYear(), at.getMonthValue(), at.getDayOfMonth()));
      }
    }
    { // add the days until we have exhausted the month and the calendar has a size divisble by 7
      LocalDate at = first;
      while (dates.size() % 7 != 0 || at.getMonthValue() == day.month) {
        dates.add(new NtDate(at.getYear(), at.getMonthValue(), at.getDayOfMonth()));
        at = at.plusDays(1);
      }
    }
    return new ArrayNtList<>(dates);
  }
}
