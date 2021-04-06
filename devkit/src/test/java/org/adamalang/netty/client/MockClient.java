/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.adamalang.netty.contracts.ClientCallback;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;

public class MockClient implements ClientCallback {
  private final ArrayList<String> output;
  private final HashMap<Integer, IdAccum> camp;
  private final CountDownLatch first;

  public class IdAccum {
    private final ArrayList<String> output;
    public final CountDownLatch first;
    public final CountDownLatch done;

    public IdAccum() {
      this.output = new ArrayList<>();
      this.first = new CountDownLatch(1);
      this.done = new CountDownLatch(1);
    }

    public void assertOnce(String data) {
      Assert.assertEquals(1, output.size());
      Assert.assertEquals(data, output.get(0));
    }

    public void assertLast(int size, String data) {
      Assert.assertEquals(size, output.size());
      Assert.assertEquals(data, output.get(size - 1));
    }

    private synchronized int _write(String x) {
      output.add(x);
      return output.size();
    }

    private void write(String x, boolean isDone) {
      int sz = _write(x);
      if (sz == 1) {
        first.countDown();
      }
      if (isDone) {
        done.countDown();
      }
    }
  }

  public IdAccum camp(int id) {
    IdAccum x = new IdAccum();
    camp.put(id, x);
    return x;
  }

  public MockClient() {
    output = new ArrayList<>();
    camp = new LinkedHashMap<>();
    this.first = new CountDownLatch(1);
  }

  public void awaitFirst() throws Exception {
    first.await(1000, TimeUnit.MILLISECONDS);
  }

  @Override
  public void closed() {
    write("Closed");
  }

  @Override
  public void failed(final Throwable exception) {
    exception.printStackTrace();
    write("Exception:" + exception.getMessage());
  }

  @Override
  public void failedToConnect() {
    write("FailedToConnect!");
  }

  @Override
  @SuppressWarnings("unchecked")
  public void successfulResponse(final String data) {
    JsonStreamReader reader = new JsonStreamReader(data);
    HashMap<String, Object> tree = (HashMap<String, Object>) reader.readJavaTree();
    Object id = tree.get("deliver");
    if (id == null) {
      id = tree.get("failure");
    }
    Object done = tree.get("done");
    if (id instanceof Integer) {
      route((int) id, data, done instanceof Boolean ? (boolean) done : true);
    }
  }

  private synchronized void route(final int id, String data, boolean isDone) {
    IdAccum accum = camp.get(id);
    if (accum != null) {
      accum.write(data, isDone);
    }
  }

  private synchronized void write(final String out) {
    first.countDown();
    output.add(out);
  }
}
