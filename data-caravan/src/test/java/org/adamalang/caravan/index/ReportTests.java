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
package org.adamalang.caravan.index;

import org.junit.Assert;
import org.junit.Test;

public class ReportTests {
  @Test
  public void flow() {
    Report report = new Report();
    report.addTotal(2000);
    report.addFree(1000);
    Assert.assertFalse(report.alarm(0.2));
    Assert.assertEquals(1000, report.getFreeBytesAvailable());
    Assert.assertEquals(2000, report.getTotalBytes());

    report.addTotal(1000000000L);
    Assert.assertTrue(report.alarm(0.2));
    report.addFree(1000000000L);
    Assert.assertFalse(report.alarm(0.2));
    report.addTotal(100000000000L);
    Assert.assertTrue(report.alarm(0.2));
  }
}
