/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.model;

import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

public class BillingTests {
  @Test
  public void billing() {
    Assert.assertEquals(0, Billing.usageValueOfZeroIfNotPresentOrNull(Json.parseJsonObject("{}"), "x"));
    Assert.assertEquals(123, Billing.usageValueOfZeroIfNotPresentOrNull(Json.parseJsonObject("{\"x\":123}"), "x"));
  }
}
