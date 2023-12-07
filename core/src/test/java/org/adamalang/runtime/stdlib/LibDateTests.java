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
import org.junit.Assert;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class LibDateTests {

  @Test
  public void make() {
    NtDate a = LibDate.make(2023, 12, 17).get();
    Assert.assertEquals(2023, a.year);
    Assert.assertEquals(12, a.month);
    Assert.assertEquals(17, a.day);
    Assert.assertFalse(LibDate.make(2023, 3, 52).has());
  }

  @Test
  public void ageFractional() {
    Assert.assertEquals(2.833333, LibDate.periodYearsFractional(new NtDate(2020, 4, 25), new NtDate(2023, 2, 25)), 0.01);
    Assert.assertEquals(5.5, LibDate.periodYearsFractional(new NtDate(2018, 4, 25), new NtDate(2023, 10, 25)), 0.01);
  }

  @Test
  public void ageMonths() {
    Assert.assertEquals(34, LibDate.periodMonths(new NtDate(2020, 4, 25), new NtDate(2023, 2, 25)));
    Assert.assertEquals(66, LibDate.periodMonths(new NtDate(2018, 4, 25), new NtDate(2023, 10, 25)));
  }

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
  public void shift_time_zone_2() {
    NtDateTime present = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]"));
    NtDateTime inNY = LibDate.adjustTimeZone(present, "America/New_York").get();
    Assert.assertEquals("2023-04-24T18:57:19.802528800-04:00[America/New_York]", inNY.toString());
  }

  @Test
  public void formatting_usa_just_date() {
    NtDate present = new NtDate(1999, 12, 17);
    Assert.assertEquals("12/17/1999", LibDate.format(present, "MM/dd/yyyy").get());
    Assert.assertFalse(LibDate.format(present, "pZ").has());
  }

  @Test
  public void formatting_jp_just_date() {
    NtDate present = new NtDate(1999, 12, 17);
    Assert.assertEquals("12/17/1999", LibDate.format(present, "MM/dd/yyyy", "JP").get());
    Assert.assertFalse(LibDate.format(present, "pZ").has());
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

  @Test
  public void between() {
    NtDateTime a = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:22:01.102528800-05:00[America/Chicago]"));
    NtDateTime b = new NtDateTime(ZonedDateTime.parse("2023-04-24T18:57:22.802528800-05:00[America/Chicago]"));
    Assert.assertEquals(5721.7, LibDate.between(a, b).seconds, 0.01);
    Assert.assertEquals(-5721.7, LibDate.between(b, a).seconds, 0.01);
  }
}
