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
package org.adamalang.net.mocks;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.ScheduledFuture;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.TestBed;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerCodec;
import org.adamalang.net.codec.ServerMessage;
import org.adamalang.net.server.Handler;
import org.adamalang.net.server.ServerNexus;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.sys.CoreStream;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class NaughyHandler implements ByteStream, ClientCodec.HandlerServer, Streamback {
  private static final ServerMessage.CreateResponse SHARED_CREATE_RESPONSE_EMPTY = new ServerMessage.CreateResponse();
  private final ServerNexus nexus;
  private final ByteStream upstream;
  private final AtomicBoolean alive;
  private CoreStream stream;
  private ScheduledFuture<?> futureHeat;

  public static class NaughtyBits {
    public HashSet<String> inventory;
    private final TestBed bed;
    private boolean fail;
    private boolean closeStream;

    public NaughtyBits(TestBed bed) {
      this.bed = bed;
      this.fail = false;
      this.inventory = null;
      this.closeStream = false;
    }

    public NaughtyBits closeStream() {
      this.closeStream = true;
      return this;
    }

    public NaughtyBits failEverything() {
      this.fail = true;
      return this;
    }

    public NaughtyBits inventory(String... inventory) {
      HashSet<String> items = new HashSet<>();
      for (String item : inventory) {
        items.add(item);
      }
      this.inventory = items;
      return this;
    }

    public void start() throws Exception {
      bed.startManual((upstream) -> new NaughyHandler(bed.nexus, upstream, this));
    }
  }

  private final Handler real;
  private final NaughtyBits bits;

  public NaughyHandler(ServerNexus nexus, ByteStream upstream, NaughtyBits bits) {
    this.nexus = nexus;
    this.upstream = upstream;
    this.futureHeat = null;
    this.alive = new AtomicBoolean(true);
    this.bits = bits;
    this.real = new Handler(nexus, upstream);
  }

  @Override
  public void request(int bytes) {
    real.request(bytes);
  }

  @Override
  public ByteBuf create(int bestGuessForSize) {
    return real.create(bestGuessForSize);
  }

  @Override
  public void next(ByteBuf buf) {
    ClientCodec.route(buf, this);
  }

  @Override
  public void completed() {
    real.completed();
  }

  @Override
  public void error(int errorCode) {
    real.error(errorCode);
  }

  @Override
  public void onSetupComplete(CoreStream stream) {
    real.onSetupComplete(stream);
  }

  @Override
  public void status(StreamStatus status) {
    real.status(status);
  }

  @Override
  public void next(String data) {
    real.next(data);
  }

  @Override
  public void handle(ClientMessage.FindRequest payload) {
    real.failure(new ErrorCodeException(-12324));
  }

  @Override
  public void handle(ClientMessage.ProbeCommandRequest payload) {
    real.failure(new ErrorCodeException(-2342));
  }

  @Override
  public void handle(ClientMessage.ExecuteQuery payload) {
    real.failure(new ErrorCodeException(-1230));
  }

  @Override
  public void failure(ErrorCodeException exception) {
    real.failure(exception);
  }

  @Override
  public void handle(ClientMessage.WebGet payload) {
    real.failure(new ErrorCodeException(-1000));
  }

  @Override
  public void handle(ClientMessage.WebOptions payload) {
    real.failure(new ErrorCodeException(-1000));
  }

  @Override
  public void handle(ClientMessage.WebPut payload) {
    real.failure(new ErrorCodeException(-1000));
  }

  @Override
  public void handle(ClientMessage.WebDelete payload) {
    real.failure(new ErrorCodeException(-1023));
  }

  @Override
  public void handle(ClientMessage.ReplicaDisconnect payload) {
    real.failure(new ErrorCodeException(-12347));
  }

  @Override
  public void handle(ClientMessage.ReplicaConnect payload) {
    real.failure(new ErrorCodeException(-12349));
  }

  @Override
  public void handle(ClientMessage.DirectSend payload) {
    real.failure(new ErrorCodeException(-40100));
  }

  @Override
  public void handle(ClientMessage.Authorize payload) {
    real.failure(new ErrorCodeException(-12324));
  }

  @Override
  public void handle(ClientMessage.RequestInventoryHeartbeat payload) {
    if (bits.inventory != null) {
      ServerMessage.InventoryHeartbeat inventoryHeartbeat = new ServerMessage.InventoryHeartbeat();
      inventoryHeartbeat.spaces = bits.inventory.toArray(new String[bits.inventory.size()]);
      ByteBuf buf = upstream.create(24);
      ServerCodec.write(buf, inventoryHeartbeat);
      upstream.next(buf);
      upstream.completed(); // this is for coverage, not the spec
    } else {
      real.handle(payload);
    }
  }

  @Override
  public void handle(ClientMessage.RequestHeat payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }

  @Override
  public void handle(ClientMessage.StreamAttach payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }

  @Override
  public void handle(ClientMessage.StreamAskAttachmentRequest payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }

  @Override
  public void handle(ClientMessage.StreamDisconnect payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }

  @Override
  public void handle(ClientMessage.StreamUpdate payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }

  @Override
  public void handle(ClientMessage.StreamSend payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }

  @Override
  public void handle(ClientMessage.StreamPassword payload) {
    if (bits.fail) {
      upstream.error(24242);
      return;
    }
    real.handle(payload);
  }

  @Override
  public void handle(ClientMessage.StreamConnect payload) {
    if (bits.closeStream) {
      upstream.completed();
      return;
    }
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }

  @Override
  public void handle(ClientMessage.ScanDeployment payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }

  @Override
  public void handle(ClientMessage.ReflectRequest payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }

  @Override
  public void handle(ClientMessage.CreateRequest payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }

  @Override
  public void handle(ClientMessage.DeleteRequest payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }

  @Override
  public void handle(ClientMessage.PingRequest payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }
}
