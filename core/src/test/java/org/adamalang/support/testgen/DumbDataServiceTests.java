package org.adamalang.support.testgen;

import org.adamalang.runtime.contracts.DataCallback;
import org.adamalang.runtime.contracts.DataService;
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
      public void progress(int stage) {
        Assert.fail();
      }

      @Override
      public void failure(int stage, Exception ex) {

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
      acquire.failure(0, new RuntimeException());
      Assert.fail();
    } catch (RuntimeException re) {}
    acquire.progress(1);
  }

  @Test
  public void noopint() {
    try {
      DumbDataService.NOOPINT.failure(0, new RuntimeException());
      Assert.fail();
    } catch (RuntimeException re) {}
    DumbDataService.NOOPINT.progress(1);
  }

  @Test
  public void pv() {
    try {
      DumbDataService.NOOPPrivateView.failure(0, new RuntimeException());
      Assert.fail();
    } catch (RuntimeException re) {}
    DumbDataService.NOOPPrivateView.progress(1);
  }
}
