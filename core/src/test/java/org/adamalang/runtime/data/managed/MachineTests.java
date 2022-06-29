/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data.managed;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.mocks.LatchCallback;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MachineTests {
  public RemoteDocumentUpdate update(int seq, String redo, String undo) {
    return new RemoteDocumentUpdate(seq, seq, NtClient.NO_ONE, null, redo, undo, false, 0, 0, UpdateType.AddUserData);
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
}
