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
package org.adamalang.runtime.data.managed;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.data.mocks.MockArchiveDataSource;
import org.adamalang.runtime.data.mocks.MockFinderService;
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
  public void shed() throws Exception {
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

    Runnable gotIt = archive.latchLogAt(1);
    BaseTests.flow((base) -> {
      base.on(KEY, (machine) -> {
        machine.write(new Action(() -> archive.initialize(KEY, update(1, "{\"x\":1}", "{\"x\":0}"), debug), debug));
        machine.write(new Action(() -> archive.patch(KEY, new RemoteDocumentUpdate[] { update(2, "{\"x\":2}", "{\"x\":1}") }, debug), debug));
        machine.read(new Action(() -> archive.get(KEY, got), got));
      });
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
      Assert.assertEquals("{\"x\":2}", val.get());
      CountDownLatch sentShed = new CountDownLatch(1);
      base.on(KEY, (machine) -> {
        machine.write(new Action(() -> archive.patch(KEY, new RemoteDocumentUpdate[] { update(3, "{\"x\":3}", "{\"x\":2}") }, debug), debug));
        machine.shed();
        sentShed.countDown();
      });
      Assert.assertTrue(sentShed.await(1000, TimeUnit.MILLISECONDS));
      gotIt.run();
      archive.assertLogAt(0, "BACKUP:space/key");
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

  @Test
  public void badstate() throws Exception {
    MockInstantDataService data = new MockInstantDataService();
    MockArchiveDataSource archive = new MockArchiveDataSource(data);
    BaseTests.flow((base) -> {
      MockFinderService finder = (MockFinderService) base.finder;
      finder.bindArchive(KEY, "");
      CountDownLatch latchFailure = new CountDownLatch(1);
      base.on(KEY, (machine) -> {
        Callback<String> callback = new Callback<String>() {
          @Override
          public void success(String value) {

          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();
            latchFailure.countDown();
          }
        };
        machine.read(new Action(() -> {
          callback.success("hi");
        }, callback));
      });
      Assert.assertTrue(latchFailure.await(1000, TimeUnit.MILLISECONDS));
    }, archive);
  }
}
