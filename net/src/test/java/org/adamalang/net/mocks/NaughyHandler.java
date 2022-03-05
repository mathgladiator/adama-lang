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
import org.junit.Test;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

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

    public NaughtyBits(TestBed bed) {
      this.bed = bed;
      this.fail = false;
      this.inventory = null;
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
  public void failure(ErrorCodeException exception) {
    real.failure(exception);
  }

  @Override
  public void handle(ClientMessage.RequestInventoryHeartbeat payload) {
    if (bits.inventory != null) {
      ServerMessage.InventoryHeartbeat inventoryHeartbeat = new ServerMessage.InventoryHeartbeat();
      inventoryHeartbeat.spaces = bits.inventory.toArray(new String[bits.inventory.size()]);
      ByteBuf buf = upstream.create(24);
      ServerCodec.write(buf, inventoryHeartbeat);
      upstream.next(buf);
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
  public void handle(ClientMessage.StreamConnect payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }

  @Override
  public void handle(ClientMessage.MeteringDeleteBatch payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }

    ByteBuf toWrite = upstream.create(4);
    ServerCodec.write(toWrite, new ServerMessage.MeteringBatchRemoved());
    upstream.next(toWrite);
    // real.handle(payload);
  }

  boolean hasFakeBatch = true;

  @Override
  public void handle(ClientMessage.MeteringBegin payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }

    if (hasFakeBatch) {
      hasFakeBatch = false;
      ServerMessage.MeteringBatchFound found = new ServerMessage.MeteringBatchFound();
      found.id = "fake-id";
      found.batch = "fake-batch";
      ByteBuf toWrite = upstream.create(found.id.length() + found.batch.length() + 8);
      ServerCodec.write(toWrite, found);
      upstream.next(toWrite);
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
  public void handle(ClientMessage.PingRequest payload) {
    if (bits.fail) {
      upstream.error(123456789);
      return;
    }
    real.handle(payload);
  }
}
