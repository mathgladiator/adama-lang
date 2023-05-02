/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtDate;
import org.adamalang.runtime.natives.NtList;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class LibDateTests {
  @Test
  public void calendarViewOf_LaunchSample() {
    NtList<NtDate> cal = LibDate.calendarViewOf(new NtDate(2023, 4, 25));
    Assert.assertEquals("2023-03-26", cal.lookup(0).get().toString());
    Assert.assertEquals("2023-05-06", cal.lookup(cal.size() - 1).get().toString());
  }

  @Test
  public void calendarViewOf_Jan2023() {
    NtList<NtDate> cal = LibDate.calendarViewOf(new NtDate(2023, 1, 25));
    Assert.assertEquals("2023-01-01", cal.lookup(0).get().toString());
    Assert.assertEquals("2023-02-04", cal.lookup(cal.size() - 1).get().toString());
  }

  @Test
  public void calendarViewOf_Dec2023() {
    NtList<NtDate> cal = LibDate.calendarViewOf(new NtDate(2023, 12, 1));
    Assert.assertEquals("2023-11-26", cal.lookup(0).get().toString());
    Assert.assertEquals("2024-01-06", cal.lookup(cal.size() - 1).get().toString());
  }

  @Test
  public void calendarViewOf_Jan2024() {
    NtList<NtDate> cal = LibDate.calendarViewOf(new NtDate(2024, 1, 25));
    Assert.assertEquals("2023-12-31", cal.lookup(0).get().toString());
    Assert.assertEquals("2024-02-03", cal.lookup(cal.size() - 1).get().toString());
  }

  @Test
  public void calendarViewOf_Dec2024() {
    NtList<NtDate> cal = LibDate.calendarViewOf(new NtDate(2024, 12, 1));
    Assert.assertEquals("2024-12-01", cal.lookup(0).get().toString());
    Assert.assertEquals("2025-01-04", cal.lookup(cal.size() - 1).get().toString());
  }

  @Test
  public void inclusiveRange_yr() {
    NtList<NtDate> cal = LibDate.inclusiveRange(new NtDate(2024, 12, 3), new NtDate(2025, 3, 27));
    Assert.assertEquals("2024-12-03", cal.lookup(0).get().toString());
    Assert.assertEquals("2025-03-27", cal.lookup(cal.size() - 1).get().toString());
    Assert.assertEquals(115, cal.size());
  }

  @Test
  public void inclusiveRange_single() {
    NtList<NtDate> cal = LibDate.inclusiveRange(new NtDate(2024, 12, 3), new NtDate(2024, 12, 3));
    Assert.assertEquals("2024-12-03", cal.lookup(0).get().toString());
    Assert.assertEquals("2024-12-03", cal.lookup(cal.size() - 1).get().toString());
    Assert.assertEquals(1, cal.size());
  }

  @Test
  public void inclusiveRange_invert() {
    NtList<NtDate> cal = LibDate.inclusiveRange(new NtDate(2025, 3, 27), new NtDate(2024, 12, 3));
    Assert.assertEquals(0, cal.size());
  }

  @Test
  public void dayOfWeek_now() {
    Assert.assertEquals(6, LibDate.dayOfWeek(new NtDate(2023, 4, 29)));
    Assert.assertEquals(7, LibDate.dayOfWeek(new NtDate(2023, 4, 30)));
    Assert.assertEquals(1, LibDate.dayOfWeek(new NtDate(2023, 5, 1)));
  }

  @Test
  public void pattern_validate() {
    Assert.assertEquals(0, LibDate.patternOf(false, false, false, false, false, false, false));
    Assert.assertEquals(1, LibDate.patternOf(true, false, false, false, false, false, false));
    Assert.assertEquals(2, LibDate.patternOf(false, true, false, false, false, false, false));
    Assert.assertEquals(4, LibDate.patternOf(false, false, true, false, false, false, false));
    Assert.assertEquals(8, LibDate.patternOf(false, false, false, true, false, false, false));
    Assert.assertEquals(16, LibDate.patternOf(false, false, false, false, true, false, false));
    Assert.assertEquals(32, LibDate.patternOf(false, false, false, false, false, true, false));
    Assert.assertEquals(64, LibDate.patternOf(false, false, false, false, false, false, true));
    Assert.assertEquals(127, LibDate.patternOf(true, true, true, true, true, true, true));
  }

  @Test
  public void patternMatch_now() {
    Assert.assertFalse(LibDate.satisfiesWeeklyPattern(new NtDate(2023, 4, 29), 0));
    Assert.assertTrue(LibDate.satisfiesWeeklyPattern(new NtDate(2023, 4, 29), 32));
    Assert.assertFalse(LibDate.satisfiesWeeklyPattern(new NtDate(2023, 4, 30), 32));
    Assert.assertTrue(LibDate.satisfiesWeeklyPattern(new NtDate(2023, 4, 30), 64));
    Assert.assertTrue(LibDate.satisfiesWeeklyPattern(new NtDate(2023, 4, 29), 127));
  }

  @Test
  public void interiorRange_weeklyPattern() {
    NtList<NtDate> cal = LibDate.inclusiveRangeSatisfiesWeeklyPattern(new NtDate(2024, 12, 3), new NtDate(2025, 3, 27), 4);
    Assert.assertEquals("2024-12-04", cal.lookup(0).get().toString());
    Assert.assertEquals("2025-03-26", cal.lookup(cal.size() - 1).get().toString());
    Assert.assertEquals(17, cal.size());
  }

  @Test
  public void monthNameEnglish_LaunchSample() {
    Assert.assertEquals("April", LibDate.monthNameEnglish(new NtDate(2023, 4, 25)));
  }

  @Test
  public void timezone() {
    ZoneId zid = ZoneId.systemDefault();
    System.err.println(ZoneId.of("America/Chicago"));
    System.err.println(zid);
    System.err.println(ZoneId.getAvailableZoneIds());
  }
}
