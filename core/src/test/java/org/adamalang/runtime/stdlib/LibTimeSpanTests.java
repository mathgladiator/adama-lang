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

import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.NtTimeSpan;
import org.junit.Assert;
import org.junit.Test;

public class LibTimeSpanTests {
  @Test
  public void basics() {
    NtTimeSpan a = new NtTimeSpan(4.1);
    NtTimeSpan b = new NtTimeSpan(50.5);
    NtTimeSpan c = LibTimeSpan.add(a, b);
    Assert.assertEquals(54.6, c.seconds, 0.01);
    NtTimeSpan d = LibTimeSpan.multiply(c, 4.2);
    Assert.assertEquals(229.32, d.seconds, 0.01);
    Assert.assertEquals(229.32, LibTimeSpan.seconds(d), 0.01);
    Assert.assertEquals(3.822, LibTimeSpan.minutes(d), 0.01);
    Assert.assertEquals(0.0637, LibTimeSpan.hours(d), 0.01);
    Assert.assertEquals(229.32, LibTimeSpan.seconds(new NtMaybe<>(d)).get(), 0.01);
    Assert.assertEquals(3.822, LibTimeSpan.minutes(new NtMaybe<>(d)).get(), 0.01);
    Assert.assertEquals(0.0637, LibTimeSpan.hours(new NtMaybe<>(d)).get(), 0.01);
    Assert.assertFalse(LibTimeSpan.seconds(new NtMaybe<>()).has());
    Assert.assertFalse(LibTimeSpan.minutes(new NtMaybe<>()).has());
    Assert.assertFalse(LibTimeSpan.hours(new NtMaybe<>()).has());
  }

  @Test
  public void make() {
    Assert.assertEquals(100, LibTimeSpan.makeFromSeconds(100.0).seconds, 0.01);
    Assert.assertEquals(100, LibTimeSpan.makeFromSeconds((int) 100).seconds, 0.01);
    Assert.assertEquals(6000, LibTimeSpan.makeFromMinutes(100.0).seconds, 0.01);
    Assert.assertEquals(6000, LibTimeSpan.makeFromMinutes((int) 100).seconds, 0.01);
  }
}
