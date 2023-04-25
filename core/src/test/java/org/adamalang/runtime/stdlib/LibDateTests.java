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
import org.junit.Assert;
import org.junit.Test;

public class LibDateTests {
  @Test
  public void calendarViewOf_Launch() {
    NtList<NtDate> cal = LibDate.calendarViewOf(new NtDate(2023, 4, 25));
    Assert.assertEquals("2023/3/26", cal.lookup(0).get().toString());
    Assert.assertEquals("2023/5/6", cal.lookup(cal.size() - 1).get().toString());
  }
}
