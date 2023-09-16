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
package org.adamalang.caravan.events;

import io.netty.buffer.Unpooled;
import org.adamalang.caravan.contracts.ByteArrayStream;
import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.adamalang.runtime.data.LocalDocumentChange;
import org.adamalang.runtime.json.JsonAlgebra;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class LocalCache implements ByteArrayStream, EventCodec.HandlerEvent {
  private final ArrayList<SeqString> redos;
  private final ArrayDeque<SeqString> undos;
  public int currentAppendIndex;
  public SeqString document;
  private int seq;

  public LocalCache() {
    this.document = null;
    this.redos = new ArrayList<>();
    this.undos = new ArrayDeque<>();
    this.seq = 0;
  }

  public int seq() {
    return seq;
  }

  @Override
  public void next(int appendIndex, byte[] value, int seq, long assetBytes) throws Exception {
    this.currentAppendIndex = appendIndex;
    EventCodec.route(Unpooled.wrappedBuffer(value), this);
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

  public int getMinimumHistoryToPreserve() {
    return 1 + redos.size();
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

  public ArrayList<byte[]> filter(ArrayList<byte[]> writes) {
    ArrayList<byte[]> reduced = new ArrayList<>();
    AtomicInteger checkSeq = new AtomicInteger(seq);
    for (byte[] write : writes) {
      EventCodec.route(Unpooled.wrappedBuffer(write), new EventCodec.HandlerEvent() {
        @Override
        public void handle(Events.Snapshot payload) {
          if (checkSeq.get() <= payload.seq || checkSeq.get() == 0) {
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

  public boolean check(int newSeq) {
    return seq + 1 == newSeq;
  }

  public String computeHeadPatch(int seqGoal) {
    AutoMorphicAccumulator<String> merger = JsonAlgebra.mergeAccumulator(true);
    for (SeqString ss : redos) {
      if (ss.seq > seqGoal) {
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
      count++;
      if (ss.seq > seqAt) {
        merger.next(ss.data);
        seqAt = ss.seq;
      }
    }
    if (merger.empty()) {
      return null;
    }
    return new LocalDocumentChange(merger.finish(), count, seq);
  }

  private class SeqString {
    private final int seq;
    private final String data;

    public SeqString(int seq, String data) {
      this.seq = seq;
      this.data = data;
    }
  }
}
