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

import org.adamalang.runtime.natives.*;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;
import org.adamalang.translator.reflect.Skip;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Locale;

/** date math for just days */
public class LibDate {
  @Extension
  public static @HiddenType(clazz = NtDateTime.class) NtMaybe<NtDateTime> construct(NtDate date, NtTime time, double seconds, String zone) {
    try {
      int nano = (int) ((seconds - (int) seconds) * 1E9);
      return new NtMaybe<>(new NtDateTime(ZonedDateTime.of(date.toLocalDate(), LocalTime.of(time.hour, time.minute, (int) seconds, nano), ZoneId.of(zone))));
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }

  public static @HiddenType(clazz = NtDate.class) NtMaybe<NtDate> make(int year, int mo, int day) {
    try {
      NtDate d = new NtDate(year, mo, day);
      d.toLocalDate();
      return new NtMaybe<>(d);
    } catch (Exception e) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static boolean valid(NtDate d) {
    return d.year > 1;
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
  public static @HiddenType(clazz=String.class) NtMaybe<String> format(NtDate d, String pattern, String lang) {
    NtMaybe<NtDateTime> dt = construct(d, new NtTime(0, 0), 0, "UTC");
    if (dt.has()) {
      return format(dt.get(), pattern, lang);
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz=String.class) NtMaybe<String> format(NtDate d, String pattern) {
    NtMaybe<NtDateTime> dt = construct(d, new NtTime(0, 0), 0, "UTC");
    if (dt.has()) {
      return format(dt.get(), pattern);
    }
    return new NtMaybe<>();
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

  @Extension
  public static NtTimeSpan between(NtDateTime from, NtDateTime to) {
    return new NtTimeSpan(ChronoUnit.MILLIS.between(from.dateTime, to.dateTime) / 1000.0);
  }

  @Extension
  public static @HiddenType(clazz=NtDateTime.class) NtMaybe<NtDateTime> withYear(NtDateTime current, int year) {
    try {
      return new NtMaybe<>(new NtDateTime(current.dateTime.withYear(year)));
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz=NtDateTime.class) NtMaybe<NtDateTime> withMonth(NtDateTime current, int month) {
    try {
      return new NtMaybe<>(new NtDateTime(current.dateTime.withMonth(month)));
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz=NtDateTime.class) NtMaybe<NtDateTime> withDayOfMonth(NtDateTime current, int dayOfMonth) {
    try {
      return new NtMaybe<>(new NtDateTime(current.dateTime.withDayOfMonth(dayOfMonth)));
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz=NtDateTime.class) NtMaybe<NtDateTime> withHour(NtDateTime current, int hour) {
    try {
      return new NtMaybe<>(new NtDateTime(current.dateTime.withHour(hour)));
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz=NtDateTime.class) NtMaybe<NtDateTime> withMinute(NtDateTime current, int minute) {
    try {
      return new NtMaybe<>(new NtDateTime(current.dateTime.withMinute(minute)));
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz=NtDateTime.class) NtMaybe<NtDateTime> withTime(NtDateTime current, NtTime t) {
    try {
      return new NtMaybe<>(new NtDateTime(current.dateTime.withHour(t.hour).withMinute(t.minute).withSecond(0).truncatedTo(ChronoUnit.SECONDS)));
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static NtDateTime truncateDay(NtDateTime current) {
    return new NtDateTime(current.dateTime.truncatedTo(ChronoUnit.DAYS));
  }

  @Extension
  public static NtDateTime truncateHour(NtDateTime current) {
    return new NtDateTime(current.dateTime.truncatedTo(ChronoUnit.HOURS));
  }

  @Extension
  public static NtDateTime truncateMinute(NtDateTime current) {
    return new NtDateTime(current.dateTime.truncatedTo(ChronoUnit.MINUTES));
  }

  @Extension
  public static NtDateTime truncateSeconds(NtDateTime current) {
    return new NtDateTime(current.dateTime.truncatedTo(ChronoUnit.SECONDS));
  }

  @Extension
  public static NtDateTime truncateMilliseconds(NtDateTime current) {
    return new NtDateTime(current.dateTime.truncatedTo(ChronoUnit.MILLIS));
  }

  @Extension
  public static NtDateTime min(NtDateTime a, NtDateTime b) {
    if (a.compareTo(b) < 0) {
      return a;
    } else {
      return b;
    }
  }

  @Extension
  public static NtDateTime max(NtDateTime a, NtDateTime b) {
    if (a.compareTo(b) > 0) {
      return a;
    } else {
      return b;
    }
  }


  @Extension
  public static NtDate min(NtDate a, NtDate b) {
    if (a.compareTo(b) < 0) {
      return a;
    } else {
      return b;
    }
  }

  @Extension
  public static NtDate max(NtDate a, NtDate b) {
    if (a.compareTo(b) > 0) {
      return a;
    } else {
      return b;
    }
  }

  public static boolean overlaps(NtDateTime a, NtDateTime b, NtDateTime c, NtDateTime d) {
    return max(a, c).compareTo(min(b, d)) <= 0;
  }

  public static boolean overlaps(NtDate a, NtDate b, NtDate c, NtDate d) {
    return max(a, c).compareTo(min(b, d)) <= 0;
  }

  @Skip
  public static boolean equalsD(NtMaybe<NtDate> a, NtDate b) {
    if (a.has()) {
      return a.get().equals(b);
    }
    return false;
  }

  @Skip
  public static boolean equalsD(NtMaybe<NtDate> a, NtMaybe<NtDate> b) {
    if (a.has()) {
      if (b.has()) {
        return a.get().equals(b.get());
      }
    } else {
      return !b.has();
    }
    return false;
  }
}
