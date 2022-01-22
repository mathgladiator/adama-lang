/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client;

import org.adamalang.grpc.client.contracts.AskAttachmentCallback;
import org.adamalang.grpc.client.contracts.Events;
import org.adamalang.grpc.client.contracts.Remote;
import org.adamalang.grpc.client.contracts.SeqCallback;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

public class CallbackTableTests {
  @Test
  public void cleanup() {
    AtomicLong counts = new AtomicLong(0);
    AtomicLong sum = new AtomicLong(0);
    CallbackTable table = new CallbackTable();
    table.associate(1, new Events() {
      @Override
      public void connected(Remote remote) {

      }

      @Override
      public void delta(String data) {

      }

      @Override
      public void error(int code) {
      }

      @Override
      public void disconnected() {
        counts.incrementAndGet();
      }
    });
    table.associate(2, new AskAttachmentCallback() {
          @Override
          public void allow() {

          }

          @Override
          public void reject() {

          }

          @Override
          public void error(int code) {
            counts.incrementAndGet();
            sum.addAndGet(code);
          }
        });
    table.associate(3, new SeqCallback() {
      @Override
      public void success(int seq) {

      }

      @Override
      public void error(int code) {
        counts.incrementAndGet();
        sum.addAndGet(code);
      }
    });
    Assert.assertEquals(0, counts.get());
    table.kill();;
    Assert.assertEquals(3, counts.get());
    Assert.assertEquals(786441, sum.get() / 2);
  }
}
