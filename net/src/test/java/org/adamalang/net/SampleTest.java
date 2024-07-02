/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
