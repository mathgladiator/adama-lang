/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
