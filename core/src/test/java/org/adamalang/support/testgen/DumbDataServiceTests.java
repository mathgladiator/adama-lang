package org.adamalang.support.testgen;

import org.adamalang.runtime.contracts.DataCallback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class DumbDataServiceTests {
  @Test
  public void coverage() {
    DumbDataService dds = new DumbDataService((t) -> {});
    try {
      dds.create(null);
      Assert.fail();
    } catch (UnsupportedOperationException uoe) {}
    dds.get(1, new DataCallback<DataService.LocalDocumentChange>() {
      @Override
      public void success(DataService.LocalDocumentChange value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
      }

    });
    try {
      dds.delete(1, null);
      Assert.fail();
    } catch (UnsupportedOperationException uoe) {}
    try {
      dds.fork(1, 1, 1, null);
      Assert.fail();
    } catch (UnsupportedOperationException uoe) {}
    try {
      dds.rewind(1, 1, null);
      Assert.fail();
    } catch (UnsupportedOperationException uoe) {}
    try {
      dds.unsend(1,1, 1, null);
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
