/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.data.managed;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.data.mocks.MockArchiveDataSource;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MachineTests {
  public RemoteDocumentUpdate update(int seq, String redo, String undo) {
    return new RemoteDocumentUpdate(seq, seq, NtPrincipal.NO_ONE, null, redo, undo, false, 0, 0, UpdateType.AddUserData);
  }

  private static final Key KEY = new Key("space", "key");

  @Test
  public void happy() throws Exception {
    MockInstantDataService data = new MockInstantDataService();
    MockArchiveDataSource archive = new MockArchiveDataSource(data);

    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<String> val = new AtomicReference<>();
    Callback<LocalDocumentChange> got = new Callback<LocalDocumentChange>() {
      @Override
      public void success(LocalDocumentChange value) {
        val.set(value.patch);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
        latch.countDown();
      }
    };

    Callback<Void> debug = new Callback<Void>() {
      @Override
      public void success(Void value) {
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
      }
    };

    BaseTests.flow((base) -> {
      base.on(KEY, (machine) -> {
        machine.write(new Action(() -> archive.initialize(KEY, update(1, "{\"x\":1}", "{\"x\":0}"), debug), debug));
        machine.write(new Action(() -> archive.patch(KEY, new RemoteDocumentUpdate[] { update(2, "{\"x\":2}", "{\"x\":1}") }, debug), debug));
        machine.read(new Action(() -> archive.get(KEY, got), got));
      });
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
      Assert.assertEquals("{\"x\":2}", val.get());
    }, archive);
  }

  @Test
  public void closing() throws Exception {
    MockInstantDataService data = new MockInstantDataService();
    MockArchiveDataSource archive = new MockArchiveDataSource(data);
    BaseTests.flow((base) -> {
      CountDownLatch latchFailure = new CountDownLatch(2);
      base.on(KEY, (machine) -> {
        Callback<Void> cb = new Callback<Void>() {
          @Override
          public void success(Void value) {

          }

          @Override
          public void failure(ErrorCodeException ex) {
            latchFailure.countDown();
          }
        };
        machine.close();
        machine.read(new Action(() -> {}, cb));
        machine.close();
        machine.write(new Action(() -> {}, cb));
      });
      Assert.assertTrue(latchFailure.await(1000, TimeUnit.MILLISECONDS));
    }, archive);
  }

  @Test
  public void deleting() throws Exception {
    MockInstantDataService data = new MockInstantDataService();
    MockArchiveDataSource archive = new MockArchiveDataSource(data);
    BaseTests.flow((base) -> {
      CountDownLatch latchFailure = new CountDownLatch(2);
      base.on(KEY, (machine) -> {
        Callback<Void> cb = new Callback<Void>() {
          @Override
          public void success(Void value) {

          }

          @Override
          public void failure(ErrorCodeException ex) {
            latchFailure.countDown();
          }
        };
        machine.delete();
        machine.read(new Action(() -> {}, cb));
        machine.delete();
        machine.write(new Action(() -> {}, cb));
      });
      Assert.assertTrue(latchFailure.await(1000, TimeUnit.MILLISECONDS));
    }, archive);
  }
}
