/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryDataServiceTests {

  @Test
  public void flow() {
    MockTime time = new MockTime();
    InMemoryDataService ds = new InMemoryDataService((t) -> t.run(), time);
    AtomicInteger success = new AtomicInteger(0);
    Key key = new Key("space", "key");
    ds.initialize(key, update(1, "{\"x\":1}", "{\"x\":0,\"y\":0}"), bumpSuccess(success));
    ds.patch(key, new DataService.RemoteDocumentUpdate[] {update(2, "{\"x\":2}", "{\"x\":1}"), update(3, "{\"x\":3}", "{\"x\":2}")}, bumpSuccess(success));
    ds.get(
        key,
        new Callback<>() {
          @Override
          public void success(DataService.LocalDocumentChange value) {
            success.getAndIncrement();
            Assert.assertEquals("{\"x\":3}", value.patch);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            Assert.fail();
          }
        });
    ds.patch(
        key,
        new DataService.RemoteDocumentUpdate[] { update(3, "{\"x\":3}", "{\"x\":2}") },
        new Callback<Void>() {
          @Override
          public void success(Void value) {
            Assert.fail();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            success.getAndIncrement();
            Assert.assertEquals(621580, ex.code);
          }
        });
    ds.compute(
        key,
        DataService.ComputeMethod.Rewind,
        1,
        new Callback<DataService.LocalDocumentChange>() {
          @Override
          public void success(DataService.LocalDocumentChange value) {
            success.getAndIncrement();
            Assert.assertEquals("{\"x\":0,\"y\":0}", value.patch);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            Assert.fail();
          }
        });
    ds.compute(
        key,
        DataService.ComputeMethod.HeadPatch,
        1,
        new Callback<DataService.LocalDocumentChange>() {
          @Override
          public void success(DataService.LocalDocumentChange value) {
            success.getAndIncrement();
            Assert.assertEquals("{\"x\":3}", value.patch);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            Assert.fail();
          }
        });
    ds.compute(
        key,
        DataService.ComputeMethod.HeadPatch,
        10,
        new Callback<DataService.LocalDocumentChange>() {
          @Override
          public void success(DataService.LocalDocumentChange value) {
            Assert.fail();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            success.getAndIncrement();
            Assert.assertEquals(120944, ex.code);
          }
        });
    ds.patch(key, new DataService.RemoteDocumentUpdate[] { updateActive(4, "{\"x\":4}", "{\"x\":3}", 42) }, bumpSuccess(success));
    ds.delete(key, bumpSuccess(success));
    Assert.assertEquals(9, success.get());
  }

  public DataService.RemoteDocumentUpdate update(int seq, String redo, String undo) {
    return new DataService.RemoteDocumentUpdate(seq, NtClient.NO_ONE, null, redo, undo, false, 0);
  }

  private static Callback<Void> bumpSuccess(AtomicInteger success) {
    return new Callback<Void>() {
      @Override
      public void success(Void value) {
        success.getAndIncrement();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    };
  }

  public DataService.RemoteDocumentUpdate updateActive(
      int seq, String redo, String undo, int time) {
    return new DataService.RemoteDocumentUpdate(seq, NtClient.NO_ONE, null, redo, undo, true, time);
  }

  @Test
  public void notFound() {
    MockTime time = new MockTime();
    InMemoryDataService ds = new InMemoryDataService((t) -> t.run(), time);
    Key key = new Key("space", "key");
    AtomicInteger failure = new AtomicInteger(0);
    ds.get(key, bumpFailureDoc(failure, 198705));
    ds.patch(key, new DataService.RemoteDocumentUpdate[] { update(1, null, null) }, bumpFailure(failure, 144944));
    ds.compute(key, null, 1, bumpFailureDoc(failure, 106546));
    ds.delete(key, bumpFailure(failure, 117816));
  }

  private static Callback<DataService.LocalDocumentChange> bumpFailureDoc(
      AtomicInteger failure, int expectedCode) {
    return new Callback<>() {
      @Override
      public void success(DataService.LocalDocumentChange value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        failure.getAndIncrement();
        Assert.assertEquals(expectedCode, ex.code);
      }
    };
  }

  private static Callback<Void> bumpFailure(AtomicInteger failure, int expectedCode) {
    return new Callback<Void>() {
      @Override
      public void success(Void value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        failure.getAndIncrement();
        Assert.assertEquals(expectedCode, ex.code);
      }
    };
  }

  @Test
  public void computeFailures() {
    MockTime time = new MockTime();
    InMemoryDataService ds = new InMemoryDataService((t) -> t.run(), time);
    Key key = new Key("space", "key");
    AtomicInteger failure = new AtomicInteger(0);
    AtomicInteger success = new AtomicInteger(0);
    ds.initialize(key, update(1, "{\"x\":1}", "{\"x\":0,\"y\":0}"), bumpSuccess(success));
    ds.initialize(
        key,
        update(1, "{\"x\":1}", "{\"x\":0,\"y\":0}"),
        bumpFailure(failure, ErrorCodes.INMEMORY_DATA_INITIALIZED_UNABLE_ALREADY_EXISTS));
    ds.patch(key, new DataService.RemoteDocumentUpdate[] { update(2, "{\"x\":2}", "{\"x\":1}") }, bumpSuccess(success));
    ds.patch(key, new DataService.RemoteDocumentUpdate[] { update(3, "{\"x\":3}", "{\"x\":2}") }, bumpSuccess(success));
    ds.compute(
        key, null, 1, bumpFailureDoc(failure, ErrorCodes.INMEMORY_DATA_COMPUTE_INVALID_METHOD));
    ds.compute(
        key,
        DataService.ComputeMethod.Rewind,
        100,
        bumpFailureDoc(failure, ErrorCodes.INMEMORY_DATA_COMPUTE_REWIND_NOTHING_TODO));
    Assert.assertEquals(3, success.get());
    Assert.assertEquals(3, failure.get());
  }
}
