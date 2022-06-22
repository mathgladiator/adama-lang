/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.net.ByteStream;
import org.adamalang.common.net.ChannelClient;
import org.adamalang.common.net.Lifecycle;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerCodec;
import org.adamalang.net.codec.ServerMessage;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SampleTest {
  @Test
  public void ping() throws Exception {
    try (TestBed bed = new TestBed( 20000, "@connected { return true; } public int x; @construct { x = 123; transition #p in 0.5; } #p { x++; } ")) {
      bed.startServer();
      CountDownLatch latch = new CountDownLatch(1);
      bed.base.connect("127.0.0.1:20000", new Lifecycle() {
        @Override
        public void connected(ChannelClient channel) {
          channel.open(new ByteStream() {
            @Override
            public void request(int bytes) {

            }

            @Override
            public ByteBuf create(int bestGuessForSize) {
              return null;
            }

            @Override
            public void next(ByteBuf buf) {
              ServerMessage.PingResponse response = ServerCodec.read_PingResponse(buf);
              System.err.println(response);
              System.err.println("got response");
              latch.countDown();
            }

            @Override
            public void completed() {

            }

            @Override
            public void error(int errorCode) {

            }
          }, new Callback<ByteStream>() {
            @Override
            public void success(ByteStream upstream) {
              ByteBuf buf = upstream.create(8);
              ClientCodec.write(buf, new ClientMessage.PingRequest());
              upstream.next(buf);
              System.err.println("wrote ping");
            }

            @Override
            public void failure(ErrorCodeException ex) {

            }
          });
        }

        @Override
        public void failed(ErrorCodeException ex) {

        }

        @Override
        public void disconnected() {

        }
      });

      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }
  }
}
