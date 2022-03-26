package org.adamalang.caravan.events;

import io.netty.buffer.Unpooled;
import org.adamalang.caravan.contracts.ByteArrayStream;
import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.adamalang.runtime.data.LocalDocumentChange;
import org.adamalang.runtime.json.JsonAlgebra;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class LocalCacheBuilder implements ByteArrayStream, EventCodec.HandlerEvent {
  public int currentAppendIndex;
  private int itemsInRemote;
  private class SeqString {
    private final int seq;
    private final String data;

    public SeqString(int seq, String data) {
      this.seq = seq;
      this.data = data;
    }
  }
  private final ArrayList<SeqString> redos;
  private final ArrayDeque<SeqString> undos;
  private int seq;
  public SeqString document;

  public LocalCacheBuilder() {
    this.document = null;
    this.redos = new ArrayList<>();
    this.undos = new ArrayDeque<>();
    this.seq = 0;
    this.itemsInRemote = 0;
  }

  @Override
  public void next(int appendIndex, byte[] value) {
    this.currentAppendIndex = appendIndex;
    EventCodec.route(Unpooled.wrappedBuffer(value), this);
    this.itemsInRemote++;
  }

  public void bump() {
    this.itemsInRemote++;
  }

  public int reset() {
    int toTrim = itemsInRemote;
    itemsInRemote = 0;
    return toTrim;
  }

  @Override
  public void handle(Events.Snapshot payload) {
    document = new SeqString(payload.seq, payload.document);
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
    undos.addFirst(new SeqString(change.seq_end, change.undo));
    seq = change.seq_end;
  }

  public boolean check(int newSeq) {
    return seq + 1 == newSeq;
  }

  public String computeHeadPatch(int seq) {
    return null;
  }

  public String computeRewind(int seq) {
    return null;
  }

  public LocalDocumentChange build() {
    AutoMorphicAccumulator<String> merger = JsonAlgebra.mergeAccumulator();
    int count = 0;
    int seqAt = -1;
    if (document != null) {
      count++;
      seq = document.seq;
      merger.next(document.data);
    }
    for (SeqString ss : redos) {
      if (ss.seq > seqAt) {
        count++;
        merger.next(ss.data);
        seqAt = ss.seq;
      }
    }
    if (merger.empty()) {
      return null;
    }
    return new LocalDocumentChange(merger.finish(), count);
  }
}
