/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.mocks;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockResponder implements CommandResponder {
  public final ArrayList<String> data;
  private boolean done;
  public ErrorCodeException ex;
  private final CountDownLatch doneLatch;
  private final CountDownLatch firstLatch;

  public MockResponder() {
    this.data = new ArrayList<>();
    this.done = false;
    this.doneLatch = new CountDownLatch(1);
    this.firstLatch = new CountDownLatch(1);
  }

  public void awaitFirst() {
    try {
      Assert.assertTrue(firstLatch.await(2000, TimeUnit.MILLISECONDS));
    } catch (InterruptedException ie) {
      Assert.fail();
    }
  }

  public void awaitDone() {
    try {
      Assert.assertTrue(doneLatch.await(2000, TimeUnit.MILLISECONDS));
    } catch (InterruptedException ie) {
      Assert.fail();
    }
  }

  public void assertLast(String expected) {
    Assert.assertEquals(expected, data.get(data.size() - 1));
  }

  @Override
  public void stream(String response) {
    Assert.assertFalse(done);
    data.add(response);
    if (data.size() == 1) {
      firstLatch.countDown();
    }
  }

  @Override
  public void finish(String response) {
    Assert.assertFalse(done);
    done = true;
    data.add(response);
    if (data.size() == 1) {
      firstLatch.countDown();
    }
    doneLatch.countDown();
  }

  @Override
  public void error(ErrorCodeException ex) {
    ex.printStackTrace();
    Assert.assertFalse(done);
    done = true;
    this.ex = ex;
    firstLatch.countDown();
    doneLatch.countDown();
  }
}
