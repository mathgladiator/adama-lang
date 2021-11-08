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

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class DumbDataServiceTests {
  @Test
  public void coverage() {
    DumbDataService dds = new DumbDataService((t) -> {});
    try {
      dds.create(new DataService.Key("0", "0"), new Callback<Long>() {
        @Override
        public void success(Long value) {

        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      Assert.fail();
    } catch (UnsupportedOperationException uoe) {}
    dds.get(new DataService.Key("0", "0"), new Callback<DataService.LocalDocumentChange>() {
      @Override
      public void success(DataService.LocalDocumentChange value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
      }

    });
    DataService.Key key = new DataService.Key("?", "1");
    try {
      dds.delete(key, null);
      Assert.fail();
    } catch (UnsupportedOperationException uoe) {}
    try {
      dds.fork(key, key, null, null, null);
      Assert.fail();
    } catch (UnsupportedOperationException uoe) {}
    try {
      dds.rewind(key, null, null, null);
      Assert.fail();
    } catch (UnsupportedOperationException uoe) {}
    try {
      dds.unsend(key,null, null, null);
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
