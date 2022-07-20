/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.support.testgen;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.ComputeMethod;
import org.adamalang.runtime.data.DocumentSnapshot;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.LocalDocumentChange;
import org.adamalang.runtime.json.PrivateView;
import org.junit.Assert;
import org.junit.Test;

public class DumbDataServiceTests {
  @Test
  public void coverage() {
    DumbDataService dds = new DumbDataService((t) -> {});
    dds.get(
        new Key("0", "0"),
        new Callback<LocalDocumentChange>() {
          @Override
          public void success(LocalDocumentChange value) {
            Assert.fail();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
    Key key = new Key("?", "1");
    dds.delete(
        key,
        new Callback<Void>() {
          @Override
          public void success(Void value) {}

          @Override
          public void failure(ErrorCodeException ex) {
            Assert.fail();
          }
        });
    dds.compute(
        key,
        ComputeMethod.Rewind,
        1,
        new Callback<LocalDocumentChange>() {
          @Override
          public void success(LocalDocumentChange value) {
            Assert.assertEquals("{\"x\":1000}", value.patch);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            Assert.fail();
          }
        });
  }

  @Test
  public void acquire() {
    DumbDataService.DumbDurableLivingDocumentAcquire acquire =
        new DumbDataService.DumbDurableLivingDocumentAcquire();
    try {
      acquire.get();
      Assert.fail();
    } catch (NullPointerException npe) {
    }
    try {
      acquire.failure(new ErrorCodeException(0, new Exception()));
      Assert.fail();
    } catch (RuntimeException re) {
    }
  }

  @Test
  public void nocompact() {
    try {
      new DumbDataService((up) -> {}).snapshot(null, new DocumentSnapshot(1, "{}",-1, 1234L), null);
      Assert.fail();
    } catch (UnsupportedOperationException re) {
    }
  }

  @Test
  public void printer_coverage() {
    StringBuilder sb = new StringBuilder();
    Callback<Integer> cb1 = DumbDataService.makePrinterInt("X", sb);
    Callback<PrivateView> cb2 = DumbDataService.makePrinterPrivateView("Y", sb);
    cb1.success(123);
    cb2.success(null);
    cb1.failure(new ErrorCodeException(123));
    cb2.failure(new ErrorCodeException(456));
    Assert.assertEquals("X|SUCCESS:123\n" + "Y: CREATED PRIVATE VIEW\n" + "X|FAILURE:123\n" + "Y: FAILED PRIVATE VIEW DUE TO:456\n", sb.toString());
  }
}
