/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.DeleteTask;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PrefixSplitDataServiceTests {

  @Test
  public void coverage() throws Exception {
    MockInstantDataService dataServiceA = new MockInstantDataService();
    MockInstantDataService dataServiceB = new MockInstantDataService();
    PrefixSplitDataService ds = new PrefixSplitDataService(dataServiceA, "x", dataServiceB);
    Key key = new Key("space", "key");
    Key key2 = new Key("space", "xyz");
    RemoteDocumentUpdate update =
        new RemoteDocumentUpdate(1, 1, NtPrincipal.NO_ONE, "", "", "", false, 1, 123, UpdateType.AddUserData);
    CountDownLatch latch = new CountDownLatch(7);
    ds.get(
        key,
        new Callback<LocalDocumentChange>() {
          @Override
          public void success(LocalDocumentChange value) {}

          @Override
          public void failure(ErrorCodeException ex) {
            latch.countDown();
          }
        });
    ds.initialize(
        key,
        update,
        new Callback<Void>() {
          @Override
          public void success(Void value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
    ds.initialize(
        key2,
        update,
        new Callback<Void>() {
          @Override
          public void success(Void value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
    ds.patch(
        key,
        new RemoteDocumentUpdate[] { update },
        new Callback<Void>() {
          @Override
          public void success(Void value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
    ds.patch(
        key2,
        new RemoteDocumentUpdate[] { update },
        new Callback<Void>() {
          @Override
          public void success(Void value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
    ds.compute(
        key,
        ComputeMethod.Rewind,
        1,
        new Callback<LocalDocumentChange>() {
          @Override
          public void success(LocalDocumentChange value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
    ds.delete(
        key,
        DeleteTask.TRIVIAL,
        new Callback<Void>() {
          @Override
          public void success(Void value) {
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
    CountDownLatch latchCompacted = new CountDownLatch(1);
    ds.snapshot(key, new DocumentSnapshot(1, "{}",100, 1234L), new Callback<Integer>() {
      @Override
      public void success(Integer value) {
        latchCompacted.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    CountDownLatch latchClosed = new CountDownLatch(1);
    ds.shed(new Key("space", "nope"));
    ds.close(key, new Callback<Void>() {
      @Override
      public void success(Void value) {
        latchClosed.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(latchCompacted.await(1000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(latchClosed.await(1000, TimeUnit.MILLISECONDS));
  }
}
