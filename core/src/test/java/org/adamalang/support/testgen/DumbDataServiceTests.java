/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.support.testgen;

import org.adamalang.runtime.contracts.ActiveKeyStream;
import org.adamalang.common.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class DumbDataServiceTests {
  @Test
  public void coverage() {
    DumbDataService dds = new DumbDataService((t) -> {});
    dds.scan(new ActiveKeyStream() {
      @Override
      public void schedule(Key key, long time) {

      }

      @Override
      public void finish() {

      }

      @Override
      public void error(ErrorCodeException failure) {
      }
    });
    dds.get(new Key("0", "0"), new Callback<DataService.LocalDocumentChange>() {
      @Override
      public void success(DataService.LocalDocumentChange value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
      }

    });
    Key key = new Key("?", "1");
    dds.delete(key, new Callback<Void>() {
      @Override
      public void success(Void value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    });
    dds.compute(key, DataService.ComputeMethod.Rewind, 1, new Callback<DataService.LocalDocumentChange>() {
      @Override
      public void success(DataService.LocalDocumentChange value) {
        Assert.assertEquals("{\"x\":1000}", value.patch);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    });
    try {
      dds.compute(key, DataService.ComputeMethod.Unsend, 1, null);
      Assert.fail();
    } catch (UnsupportedOperationException uoe) {}
  }

  @Test
  public void acquire() {
    DumbDataService.DumbDurableLivingDocumentAcquire acquire = new DumbDataService.DumbDurableLivingDocumentAcquire();
    try {
      acquire.get();
      Assert.fail();
    } catch (NullPointerException npe) {
    }
    try {
      acquire.failure(new ErrorCodeException(0, new Exception()));
      Assert.fail();
    } catch (RuntimeException re) {}
  }

  @Test
  public void noopint() {
    try {
      DumbDataService.NOOPINT.failure(new ErrorCodeException(0, new Exception()));
      Assert.fail();
    } catch (RuntimeException re) {}
  }

  @Test
  public void pv() {
    try {
      DumbDataService.NOOPPrivateView.failure(new ErrorCodeException(0, new Exception()));
      Assert.fail();
    } catch (RuntimeException re) {}
  }
}
