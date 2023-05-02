/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.ops.StdOutDocumentMonitor;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.remote.RemoteResult;
import org.adamalang.runtime.sys.*;
import org.adamalang.support.testgen.DumbDataService;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RealDocumentSetup implements Deliverer {
  public final LivingDocumentFactory factory;
  public final MockTime time;
  public final DurableLivingDocument document;
  private DurableLivingDocument mirror;
  public final String code;
  private Deliverer deliver = Deliverer.FAILURE;
  public final DumbDataService dumbDataService;

  public RealDocumentSetup(final String code) throws Exception {
    this(code, null);
  }

  public RealDocumentSetup(final String code, final String json) throws Exception {
    this(code, json, true);
  }

  public RealDocumentSetup(final String code, final String json, final boolean stdout)
      throws Exception {
    this(code, json, stdout, new MockTime());
  }

  @Override
  public void deliver(NtPrincipal agent, Key key, int id, RemoteResult result, boolean firstParty, Callback<Integer> callback) {
    this.deliver.deliver(agent, key, id, result, firstParty, callback);
  }

  public RealDocumentSetup(
      final String code, final String json, final boolean stdout, MockTime time) throws Exception {
    this.time = time;
    DumbDataService dds =
        new DumbDataService(
            (update) -> {
              if (stdout) {
                System.out.println(" REQ :" + update.request);
                System.out.println("FORWARD:" + update.redo);
                System.out.println("REVERSE:" + update.undo);
              }
              if (mirror != null) {
                mirror.document().__insert(new JsonStreamReader(update.redo));
              }
            });
    this.dumbDataService = dds;
    DocumentThreadBase base = new DocumentThreadBase(new ServiceShield(), dds, new CoreMetrics(new NoOpMetricsFactory()), SimpleExecutor.NOW, time);
    dds.setData(json);
    factory = LivingDocumentTests.compile(code, deliver);
    this.code = code;
    DumbDataService.DumbDurableLivingDocumentAcquire acquireReal =
        new DumbDataService.DumbDurableLivingDocumentAcquire();
    DumbDataService.DumbDurableLivingDocumentAcquire acquireMirror =
        new DumbDataService.DumbDurableLivingDocumentAcquire();
    DocumentMonitor monitor = stdout ? new StdOutDocumentMonitor() : null;
    Key key = new Key("space", "0");
    if (json == null) {
      DurableLivingDocument.fresh(
          key, factory, new CoreRequestContext(NtPrincipal.NO_ONE, "origin", "ip", key.key), "{}", "123", monitor, base, acquireReal);
      DurableLivingDocument.fresh(
          key, factory, new CoreRequestContext(NtPrincipal.NO_ONE, "origin", "ip", key.key), "{}", "123", monitor, base, acquireMirror);
    } else {
      DurableLivingDocument.load(key, factory, monitor, base, acquireReal);
      DurableLivingDocument.load(key, factory, monitor, base, acquireMirror);
    }
    document = acquireReal.get();
    mirror = acquireMirror.get();
  }

  public void assertCompare() {
    Assert.assertEquals(mirror.json(), document.json());
  }

  public static class GotView implements Callback<PrivateView> {

    public PrivateView view = null;

    @Override
    public void success(PrivateView value) {
      view = value;
    }

    @Override
    public void failure(ErrorCodeException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static class AssertInt implements Callback<Integer> {
    public final int expected;

    public AssertInt(int value) {
      this.expected = value;
    }

    @Override
    public void success(Integer actual) {
      System.err.println("exp:" + expected + "/" + actual);
      Assert.assertEquals(expected, (int) actual);
    }

    @Override
    public void failure(ErrorCodeException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static class AssertSuccess implements Callback<Void> {

    CountDownLatch latch;
    public AssertSuccess() {
      latch = new CountDownLatch(1);
    }

    @Override
    public void success(Void v) {
      latch.countDown();
    }

    public void test() throws Exception{
      Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    }

    @Override
    public void failure(ErrorCodeException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static class AssertJustFailure implements Callback<Void> {
    int codeToExpect;
    CountDownLatch latch;
    public AssertJustFailure(int codeToExpect) {
      this.codeToExpect = codeToExpect;
      latch = new CountDownLatch(1);
    }

    @Override
    public void success(Void v) {
      Assert.fail();
    }

    public void test() throws Exception{
      Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    }

    @Override
    public void failure(ErrorCodeException ex) {
      Assert.assertEquals(codeToExpect, ex.code);
      latch.countDown();
    }
  }

  public static class AssertFailure implements Callback<Integer> {
    private int codeToExpect;

    public AssertFailure(int codeToExpect) {
      this.codeToExpect = codeToExpect;
    }

    @Override
    public void success(Integer actual) {
      throw new RuntimeException("should have failed");
    }

    @Override
    public void failure(ErrorCodeException ex) {
      Assert.assertEquals(codeToExpect, ex.code);
    }
  }

  public static class AssertNoResponse implements Callback<Integer> {
    public AssertNoResponse() {
    }

    @Override
    public void success(Integer actual) {
      Assert.fail();
    }

    @Override
    public void failure(ErrorCodeException ex) {
      Assert.fail();
    }
  }

  public static class ArrayPerspective implements Perspective {
    public final ArrayList<String> datum;

    public ArrayPerspective() {
      this.datum = new ArrayList<>();
    }

    @Override
    public void data(String data) {
      this.datum.add(data);
    }

    @Override
    public void disconnect() {}
  }
}
