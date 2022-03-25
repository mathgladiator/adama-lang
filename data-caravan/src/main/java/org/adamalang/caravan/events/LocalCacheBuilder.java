package org.adamalang.caravan.events;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.contracts.ByteArrayStream;
import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.adamalang.runtime.data.LocalDocumentChange;
import org.adamalang.runtime.json.JsonAlgebra;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class LocalCacheBuilder implements ByteArrayStream, EventCodec.HandlerEvent {
  public int currentAppendIndex;
  public String document;
  private class SeqString {
    private final int seq;
    private final String data;

    public SeqString(int seq, String data) {
      this.seq = seq;
      this.data = data;
    }
  }
  public ArrayList<SeqString> redos;

  public LocalCacheBuilder() {
    this.document = null;
    this.redos = new ArrayList<>();
  }

  @Override
  public void next(int appendIndex, byte[] value) {
    this.currentAppendIndex = appendIndex;
    EventCodec.route(Unpooled.wrappedBuffer(value), this);
  }


  @Override
  public void handle(Events.Snapshot payload) {
    document = payload.document;
    Iterator<SeqString> it = redos.iterator();
    while (it.hasNext()) {
      if (it.next().seq <= payload.seq) {
        it.remove();
      }
    }
  }

  @Override
  public void handle(Events.Batch payload) {
    for (Events.Change change : payload.changes) {
      handle(change);
    }
  }

  @Override
  public void handle(Events.Change change) {
    redos.add(new SeqString(change.seq_end, change.redo));
  }

  public LocalDocumentChange build() {
    AutoMorphicAccumulator<String> merger = JsonAlgebra.mergeAccumulator();
    int count = 0;
    if (document != null) {
      count++;
      merger.next(document);
    }
    for (SeqString ss : redos) {
      count++;
      merger.next(ss.data);
    }
    return new LocalDocumentChange(merger.finish(), count);
  }
}
