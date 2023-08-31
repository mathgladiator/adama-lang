/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.*;
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
  public void weekViewOf_Aug1_2023() {
    NtList<NtDate> week = LibDate.weekViewOf(new NtDate(2023, 8, 1));
    Assert.assertEquals(7, week.size());
    Assert.assertEquals("2023-07-30", week.lookup(0).get().toString());
    Assert.assertEquals("2023-08-05", week.lookup(6).get().toString());
  }

  @Test
  public void weekViewOf_Dec17_2025() {
    NtList<NtDate> week = LibDate.weekViewOf(new NtDate(2025, 12, 17));
    Assert.assertEquals(7, week.size());
    Assert.assertEquals("2025-12-14", week.lookup(0).get().toString());
    Assert.assertEquals("2025-12-20", week.lookup(6).get().toString());
  }

  @Test
  public void weekViewOf_Jan1_2021() {
    NtList<NtDate> week = LibDate.weekViewOf(new NtDate(2021, 1, 1));
    Assert.assertEquals(7, week.size());
    Assert.assertEquals("2020-12-27", week.lookup(0).get().toString());
    Assert.assertEquals("2021-01-02", week.lookup(6).get().toString());
  }

  @Test
  public void dayOfWeek_Aug1_2023() {
    Assert.assertEquals("Tuesday", LibDate.dayOfWeekEnglish(new NtDate(2023, 8, 1)));
  }

  @Test
  public void dayOfWeek_Dec17_2025() {
    Assert.assertEquals("Wednesday", LibDate.dayOfWeekEnglish(new NtDate(2025, 12, 17)));
  }

  @Test
  public void neighborView_Aug1_2023() {
    NtList<NtDate> n;
    n = LibDate.neighborViewOf(new NtDate(2023, 8, 1), 1);
    Assert.assertEquals(3, n.size());
    Assert.assertEquals("2023-07-31", n.lookup(0).get().toString());
    Assert.assertEquals("2023-08-02", n.lookup(2).get().toString());
    n = LibDate.neighborViewOf(new NtDate(2023, 8, 1), 3);
    Assert.assertEquals(7, n.size());
    Assert.assertEquals("2023-07-29", n.lookup(0).get().toString());
    Assert.assertEquals("2023-08-04", n.lookup(6).get().toString());
    n = LibDate.neighborViewOf(new NtDate(2023, 8, 1), 7);
    Assert.assertEquals(15, n.size());
    Assert.assertEquals("2023-07-25", n.lookup(0).get().toString());
    Assert.assertEquals("2023-08-08", n.lookup(14).get().toString());
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

  @Test
  public void future() {
    NtDateTime present = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]"));
    NtDateTime next = LibDate.future(present, new NtTimeSpan(60 * 60 * 15 * 7));
    Assert.assertEquals("2023-04-29T02:57:19.802528800-05:00[America/Chicago]", next.toString());
  }

  @Test
  public void past() {
    NtDateTime present = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]"));
    NtDateTime next = LibDate.past(present, new NtTimeSpan(60 * 60 * 15 * 7));
    Assert.assertEquals("2023-04-20T08:57:19.802528800-05:00[America/Chicago]", next.toString());
  }

  @Test
  public void offset_month_small_pos() {
    NtDate x = new NtDate(2000, 7, 31);
    Assert.assertEquals("2000-07-31", LibDate.offsetMonth(x, 0).toString());
    Assert.assertEquals("2000-08-31", LibDate.offsetMonth(x, 1).toString());
    Assert.assertEquals("2000-09-30", LibDate.offsetMonth(x, 2).toString());
  }

  @Test
  public void offset_month_large_pos() {
    NtDate x = new NtDate(2000, 7, 31);
    Assert.assertEquals("2001-06-30", LibDate.offsetMonth(x, 11).toString());
    Assert.assertEquals("2001-07-31", LibDate.offsetMonth(x, 12).toString());
    Assert.assertEquals("2002-06-30", LibDate.offsetMonth(x, 23).toString());
    Assert.assertEquals("2002-08-31", LibDate.offsetMonth(x, 25).toString());
  }

  @Test
  public void offset_month_small_neg() {
    NtDate x = new NtDate(2000, 7, 31);
    Assert.assertEquals("2000-06-30", LibDate.offsetMonth(x, -1).toString());
    Assert.assertEquals("2000-05-31", LibDate.offsetMonth(x, -2).toString());
  }

  @Test
  public void offset_month_large_neg() {
    NtDate x = new NtDate(2000, 7, 31);
    Assert.assertEquals("2001-06-30", LibDate.offsetMonth(x, 11).toString());
    Assert.assertEquals("2001-07-31", LibDate.offsetMonth(x, 12).toString());
    Assert.assertEquals("2002-06-30", LibDate.offsetMonth(x, 23).toString());
    Assert.assertEquals("2002-08-31", LibDate.offsetMonth(x, 25).toString());
  }

  @Test
  public void offset_day_small() {
    NtDate x = new NtDate(2000, 7, 31);
    Assert.assertEquals("2000-07-30", LibDate.offsetDay(x, -1).toString());
    Assert.assertEquals("2000-08-01", LibDate.offsetDay(x, 1).toString());
  }

  @Test
  public void offset_day_medium() {
    NtDate x = new NtDate(2023, 8, 1);
    Assert.assertEquals("2023-07-25", LibDate.offsetDay(x, -7).toString());
    Assert.assertEquals("2023-08-08", LibDate.offsetDay(x, 7).toString());
  }

  @Test
  public void offset_day_large() {
    NtDate x = new NtDate(2023, 8, 1);
    Assert.assertEquals("2022-06-27", LibDate.offsetDay(x, -400).toString());
    Assert.assertEquals("2024-09-04", LibDate.offsetDay(x, 400).toString());
  }

  @Test
  public void conv_to_date() {
    NtDateTime present = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]"));
    NtDate d = LibDate.date(present);
    Assert.assertEquals("2023-04-24", d.toString());
  }

  @Test
  public void conv_to_time() {
    NtDateTime present = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]"));
    NtTime t = LibDate.time(present);
    Assert.assertEquals("17:57", t.toString());
  }

  @Test
  public void shift_time_zone() {
    NtDateTime present = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]"));
    NtDateTime inUTC = LibDate.adjustTimeZone(present, "UTC").get();
    Assert.assertEquals("2023-04-24T22:57:19.802528800Z[UTC]", inUTC.toString());
    NtDateTime inLA = LibDate.adjustTimeZone(present, "America/Los_Angeles").get();
    Assert.assertEquals("2023-04-24T15:57:19.802528800-07:00[America/Los_Angeles]", inLA.toString());
    Assert.assertFalse(LibDate.adjustTimeZone(present, "NoWhere").has());
  }

  @Test
  public void formatting_usa() {
    NtDateTime present = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]"));
    Assert.assertEquals("04/24/2023", LibDate.format(present, "MM/dd/yyyy").get());
    Assert.assertFalse(LibDate.format(present, "pZ").has());
  }

  @Test
  public void formatting_jp() {
    NtDateTime present = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]"));
    Assert.assertEquals("04/24/2023", LibDate.format(present, "MM/dd/yyyy", "JP").get());
    Assert.assertFalse(LibDate.format(present, "pZ", "JP").has());
  }

  @Test
  public void formatting_en_us() {
    NtDateTime present = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]"));
    Assert.assertEquals("04/24/2023", LibDate.format(present, "MM/dd/yyyy", "en-US").get());
    Assert.assertFalse(LibDate.format(present, "pZ", "en-US").has());
  }
}
