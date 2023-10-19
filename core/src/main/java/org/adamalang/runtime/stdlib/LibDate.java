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

import org.adamalang.runtime.natives.*;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Locale;

/** date math for just days */
public class LibDate {
  public static @HiddenType(clazz = NtDateTime.class) NtMaybe<NtDateTime> construct(NtDate date, NtTime time, double seconds, String zone) {
    try {
      int nano = (int) ((seconds - (int) seconds) * 1E9);
      return new NtMaybe<>(new NtDateTime(ZonedDateTime.of(date.toLocalDate(), LocalTime.of(time.hour, time.minute, (int) seconds, nano), ZoneId.of(zone))));
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz = NtDate.class) NtList<NtDate> calendarViewOf(NtDate day) {
    ArrayList<NtDate> dates = new ArrayList<>();
    // get the first of the given month
    LocalDate first = LocalDate.of(day.year, day.month, 1);
    // Monday -> 1, Sunday -> 7; transform this to days prior to the start of the month
    int offset = (first.getDayOfWeek().getValue()) % 7;
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

  @Extension
  public static @HiddenType(clazz = NtDate.class) NtList<NtDate> weekViewOf(NtDate day) {
    ArrayList<NtDate> dates = new ArrayList<>();

    // convert and snap the day to the first day of the week
    LocalDate first = day.toLocalDate();
    first = first.minusDays((first.getDayOfWeek().getValue()) % 7); // Sunday is 7 which is really 0

    { // build out the view
      for (int k = 0; k < 7; k++) {
        LocalDate at = first.plusDays(k);
        dates.add(new NtDate(at.getYear(), at.getMonthValue(), at.getDayOfMonth()));
      }
    }
    return new ArrayNtList<>(dates);
  }

  @Extension
  public static @HiddenType(clazz = NtDate.class) NtList<NtDate> neighborViewOf(NtDate day, int days) {
    ArrayList<NtDate> dates = new ArrayList<>();

    // convert and snap the day to the first day of the week
    LocalDate at = day.toLocalDate().minusDays(days);

    { // build out the view
      for (int k = 0; k < 2 * days + 1; k++) {
        dates.add(new NtDate(at.getYear(), at.getMonthValue(), at.getDayOfMonth()));
        at = at.plusDays(1);
      }
    }
    return new ArrayNtList<>(dates);
  }


  public static int patternOf(boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday) {
    int p = 0;
    if (monday) {
      p |= 0x01;
    }
    if (tuesday) {
      p |= 0x02;
    }
    if (wednesday) {
      p |= 0x04;
    }
    if (thursday) {
      p |= 0x08;
    }
    if (friday) {
      p |= 0x10;
    }
    if (saturday) {
      p |= 0x20;
    }
    if (sunday) {
      p |= 0x40;
    }
    return p;
  }

  @Extension
  public static boolean satisfiesWeeklyPattern(NtDate date, int pattern) {
    return ((1 << date.toLocalDate().getDayOfWeek().ordinal()) & pattern) > 0;
  }

  @Extension
  public static @HiddenType(clazz = NtDate.class) NtList<NtDate> inclusiveRange(NtDate from, NtDate to) {
    ArrayList<NtDate> dates = new ArrayList<>();
    LocalDate at = LocalDate.of(from.year, from.month, from.day);
    LocalDate toLD = LocalDate.of(to.year, to.month, to.day);
    while (at.compareTo(toLD) <= 0) {
      dates.add(new NtDate(at.getYear(), at.getMonthValue(), at.getDayOfMonth()));
      at = at.plusDays(1);
    }
    return new ArrayNtList<>(dates);
  }

  @Extension
  public static @HiddenType(clazz = NtDate.class) NtList<NtDate> inclusiveRangeSatisfiesWeeklyPattern(NtDate from, NtDate to, int pattern) {
    ArrayList<NtDate> dates = new ArrayList<>();
    LocalDate at = LocalDate.of(from.year, from.month, from.day);
    LocalDate toLD = LocalDate.of(to.year, to.month, to.day);
    while (at.compareTo(toLD) <= 0) {
      boolean add = ((1 << (at.getDayOfWeek().ordinal())) & pattern) > 0;
      if (add) {
        dates.add(new NtDate(at.getYear(), at.getMonthValue(), at.getDayOfMonth()));
      }
      at = at.plusDays(1);
    }
    return new ArrayNtList<>(dates);
  }

  @Extension
  public static int dayOfWeek(NtDate day) {
    // 1 = Monday, 7 = Sunday
    return day.toLocalDate().getDayOfWeek().getValue();
  }

  @Extension
  public static String dayOfWeekEnglish(NtDate day) {
    // 1 = Monday, 7 = Sunday
    return day.toLocalDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
  }

  @Extension
  public static String monthNameEnglish(NtDate day) {
    return day.toLocalDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
  }

  @Extension
  public static NtDateTime future(NtDateTime present, NtTimeSpan span) {
    return new NtDateTime(present.dateTime.plusSeconds((long) span.seconds));
  }

  @Extension
  public static NtDateTime past(NtDateTime present, NtTimeSpan span) {
    return new NtDateTime(present.dateTime.minusSeconds((long) span.seconds));
  }

  @Extension
  public static NtDate offsetMonth(NtDate day, int offset) {
    LocalDate d = day.toLocalDate().plusMonths(offset);
    return new NtDate(d.getYear(), d.getMonthValue(), d.getDayOfMonth());
  }

  @Extension
  public static NtDate offsetDay(NtDate day, int offset) {
    LocalDate d = day.toLocalDate().plusDays(offset);
    return new NtDate(d.getYear(), d.getMonthValue(), d.getDayOfMonth());
  }

  @Extension
  public static NtDate date(NtDateTime dt) {
    LocalDate d = dt.dateTime.toLocalDate();
    return new NtDate(d.getYear(), d.getMonthValue(), d.getDayOfMonth());
  }

  @Extension
  public static NtTime time(NtDateTime dt) {
    LocalTime lt = dt.dateTime.toLocalTime();
    return new NtTime(lt.getHour(), lt.getMinute());
  }

  @Extension
  public static @HiddenType(clazz=NtDateTime.class) NtMaybe<NtDateTime> adjustTimeZone(NtDateTime dt, String newTimeZone) {
    try {
      return new NtMaybe<>(new NtDateTime(dt.dateTime.withZoneSameInstant(ZoneId.of(newTimeZone))));
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz=String.class) NtMaybe<String> format(NtDateTime dt, String pattern, String lang) {
    try {
      return new NtMaybe<>(dt.dateTime.format(DateTimeFormatter.ofPattern(pattern, Locale.forLanguageTag(lang))));
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz=String.class) NtMaybe<String> format(NtDateTime dt, String pattern) {
    try {
      return new NtMaybe<>(dt.dateTime.format(DateTimeFormatter.ofPattern(pattern, Locale.US)));
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static double periodYearsFractional(NtDate from, NtDate to) {
    Period p = Period.between(from.toLocalDate(), to.toLocalDate());
    return p.getYears() + p.getMonths() / 12.0;
  }

  @Extension
  public static int periodMonths(NtDate from, NtDate to) {
    Period p = Period.between(from.toLocalDate(), to.toLocalDate());
    return p.getYears() * 12 + p.getMonths();
  }

}
