/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.caravan.events;

import io.netty.buffer.Unpooled;
import org.adamalang.caravan.contracts.ByteArrayStream;
import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.adamalang.runtime.data.LocalDocumentChange;
import org.adamalang.runtime.json.JsonAlgebra;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class LocalCache implements ByteArrayStream, EventCodec.HandlerEvent {
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

  public LocalCache() {
    this.document = null;
    this.redos = new ArrayList<>();
    this.undos = new ArrayDeque<>();
    this.seq = 0;
    this.itemsInRemote = 0;
  }

  public int seq() {
    return seq;
  }

  @Override
  public void next(int appendIndex, byte[] value, int seq, long assetBytes) throws Exception {
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
      if (it.next().seq + payload.history <= payload.seq) {
        it.remove();
      }
    }
    while (undos.size() > payload.history) {
      undos.removeLast();
    }
  }

  public ArrayList<byte[]> filter(ArrayList<byte[]> writes) {
    ArrayList<byte[]> reduced = new ArrayList<>();
    AtomicInteger checkSeq = new AtomicInteger(seq);
    for (byte[] write : writes) {
      EventCodec.route(Unpooled.wrappedBuffer(write), new EventCodec.HandlerEvent() {
        @Override
        public void handle(Events.Snapshot payload) {
          if (checkSeq.get() < payload.seq || checkSeq.get() == 0) {
            reduced.add(write);
            checkSeq.set(payload.seq);
          }
        }

        @Override
        public void handle(Events.Batch payload) {
          if (checkSeq.get() + 1 == payload.changes[0].seq_begin || checkSeq.get() == 0) {
            reduced.add(write);
            checkSeq.set(payload.changes[payload.changes.length - 1].seq_end);
          }
        }

        @Override
        public void handle(Events.Change payload) {
          if (checkSeq.get() + 1 == payload.seq_begin || checkSeq.get() == 0) {
            reduced.add(write);
            checkSeq.set(payload.seq_end);
          }
        }
      });
    }
    return reduced;
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
    undos.addFirst(new SeqString(change.seq_begin, change.undo));
    seq = change.seq_end;
  }

  public boolean check(int newSeq) {
    return seq + 1 == newSeq;
  }

  public String computeHeadPatch(int seqGoal) {
    AutoMorphicAccumulator<String> merger = JsonAlgebra.mergeAccumulator(true);
    int last = -1;
    for (SeqString ss : redos) {
      if (ss.seq > seqGoal) {
        last = ss.seq;
        merger.next(ss.data);
      }
    }
    if (merger.empty()) {
      return null;
    }
    return merger.finish();
  }

  public String computeRewind(int seqGoal) {
    AutoMorphicAccumulator<String> merger = JsonAlgebra.mergeAccumulator(true);
    Iterator<SeqString> it = undos.iterator();
    int last = -1;
    while (it.hasNext()) {
      SeqString ss = it.next();
      if (ss.seq >= seqGoal) {
        merger.next(ss.data);
        last = ss.seq;
      }
    }
    if (merger.empty() || last > seqGoal) {
      return null;
    }
    return merger.finish();
  }

  public LocalDocumentChange build() {
    AutoMorphicAccumulator<String> merger = JsonAlgebra.mergeAccumulator();
    int count = 0;
    int seqAt = -1;
    if (document != null) {
      count++;
      seqAt = document.seq;
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
    return new LocalDocumentChange(merger.finish(), count, seq);
  }
}
