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
package org.adamalang.support.testgen;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.DeleteTask;
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
        DeleteTask.TRIVIAL,
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
