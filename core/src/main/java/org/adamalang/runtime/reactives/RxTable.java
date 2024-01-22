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
package org.adamalang.runtime.reactives;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.runtime.contracts.*;
import org.adamalang.runtime.graph.DifferentialEdgeTracker;
import org.adamalang.runtime.index.ReactiveIndex;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.JsonSum;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.runtime.natives.lists.EmptyNtList;
import org.adamalang.runtime.natives.lists.SelectorRxObjectList;
import org.adamalang.runtime.reactives.tables.TablePubSub;
import org.adamalang.runtime.reactives.tables.TableSubscription;
import org.adamalang.runtime.sys.LivingDocument;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/** a reactive table */
public class RxTable<Ty extends RxRecordBase<Ty>> extends RxBase implements Iterable<Ty>, RxParent, RxChild, RxKillable {
  public final LivingDocument document;
  public final Function<RxParent, Ty> maker;
  public final String className;
  private final LinkedHashMap<Integer, Ty> createdObjects;
  private final ReactiveIndex<Ty>[] indices;
  private final LinkedHashMap<Integer, Ty> itemsByKey;
  private final TreeSet<Ty> unknowns;
  public final TablePubSub pubsub;
  private final Stack<RxTableGuard> guardsInflight;
  private RxTableGuard activeGuard;
  private ArrayList<DifferentialEdgeTracker<Ty>> trackers;
  private TableSubscription trackerSub;

  public void debug(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("created");
    writer.writeInteger(createdObjects.size());
    writer.writeObjectFieldIntro("items");
    writer.writeInteger(itemsByKey.size());
    if (indices != null) {
      writer.writeObjectFieldIntro("idx");
      writer.writeInteger(indices.length);
    }
    if (unknowns != null) {
      writer.writeObjectFieldIntro("unknowns");
      writer.writeInteger(unknowns.size());
    }
    writer.endObject();
  }

  @SuppressWarnings("unchecked")
  public RxTable(final LivingDocument document, final RxParent owner, final String className, final Function<RxParent, Ty> maker, final int indicies) {
    super(owner);
    this.document = document;
    this.className = className;
    this.maker = maker;
    if (indicies == 0) {
      this.indices = null;
      this.unknowns = null;
    } else {
      this.indices = new ReactiveIndex[indicies];
      this.unknowns = new TreeSet<>();
      for (var k = 0; k < indicies; k++) {
        this.indices[k] = new ReactiveIndex<>(unknowns);
      }
    }
    // check if we have rows; make sure we link into the JSON tree
    this.itemsByKey = new LinkedHashMap<>();
    this.createdObjects = new LinkedHashMap<>();
    this.pubsub = new TablePubSub(this);
    this.activeGuard = null;
    this.trackers = null;
    this.trackerSub = null;
    this.guardsInflight = new Stack<>();
  }

  public void pump(DifferentialEdgeTracker<Ty> tracker) {
    if (this.trackers == null) {
      this.trackers = new ArrayList<>();
      trackerSub = new TableSubscription() {
        @Override
        public boolean alive() {
          return true;
        }

        @Override
        public boolean primary(int primaryKey) {
          for (DifferentialEdgeTracker<Ty> child : trackers) {
            child.change(primaryKey);
          }
          return false;
        }

        @Override
        public void index(int index, int value) {
        }
      };
      pubsub.subscribe(trackerSub);
    }
    trackers.add(tracker);
  }

  @Override
  public boolean __isAlive() {
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }

  @Override
  public void __cost(int cost) {
    if (__parent != null) {
      __parent.__cost(cost);
    }
  }

  @Override
  public void __kill() {
    for (Map.Entry<Integer, Ty> entry : itemsByKey.entrySet()) {
      entry.getValue().__kill();
    }
    if (trackers != null) {
      for (DifferentialEdgeTracker<Ty> tracker : trackers) {
        tracker.removeAll();
      }
    }
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      pubsub.gc();
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.beginObject();
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.beginObject();
      final var keysToKill = new ArrayList<Integer>();
      for (final Map.Entry<Integer, Ty> entry : itemsByKey.entrySet()) {
        final int key = entry.getKey();
        final var row = entry.getValue();
        if (row.__isDying()) {
          if (!createdObjects.containsKey(key)) {
            forwardDelta.writeObjectFieldIntro(key);
            forwardDelta.writeNull();
            reverseDelta.writeObjectFieldIntro(key);
            row.__dump(reverseDelta);
          }
          row.__kill();
          keysToKill.add(key);
        } else if (row.__isDirty()) {
          if (createdObjects.containsKey(key)) {
            forwardDelta.writeObjectFieldIntro(key);
            row.__dump(forwardDelta);
            reverseDelta.writeObjectFieldIntro(key);
            reverseDelta.writeNull();
            JsonStreamWriter redundantWrite = new JsonStreamWriter();
            row.__commit(key + "", redundantWrite, redundantWrite);
          } else {
            row.__commit(key + "", forwardDelta, reverseDelta);
          }
        }
      }
      forwardDelta.endObject();
      reverseDelta.endObject();
      createdObjects.clear();
      // murder the keys
      for (final Integer keyToKill : keysToKill) {
        itemsByKey.remove(keyToKill);
      }
      __lowerDirtyCommit();
    }
    if (unknowns != null) {
      for (final Ty item : unknowns) {
        item.__reindex();
      }
      unknowns.clear();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.beginObject();
    for (final Map.Entry<Integer, Ty> entry : itemsByKey.entrySet()) {
      writer.writeObjectFieldIntro(entry.getKey());
      entry.getValue().__dump(writer);
    }
    writer.endObject();
  }

  public void invalidatePrimaryKey(int key, Ty value) {
    if (pubsub.primary(key)) {
      value.__invalidateIndex(pubsub);
    }
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        final var f2 = reader.fieldName();
        if (reader.testLackOfNull()) {
          final var key = Integer.parseInt(f2);
          final var tyPrior = itemsByKey.get(key);
          if (trackerSub != null) {
            trackerSub.primary(key);
          }
          if (tyPrior == null) {
            final var tyObj = maker.apply(this);
            tyObj.__setId(key, true);
            tyObj.__insert(reader);
            itemsByKey.put(key, tyObj);
            if (unknowns != null) {
              tyObj.__reindex();
            }
            tyObj.__subscribe(() -> {
              invalidatePrimaryKey(key, tyObj);
              return RxTable.this.__raiseInvalid();
            });
            tyObj.__pumpIndexEvents(pubsub);
          } else {
            // it exists, so it is already subscribed
            tyPrior.__insert(reader);
            if (unknowns != null && !unknowns.contains(tyPrior)) {
              tyPrior.__deindex();
              unknowns.add(tyPrior);
            }
          }
        } else {
          final var key = Integer.parseInt(f2);
          final var tyObject = itemsByKey.remove(key);
          if (tyObject != null) {
            tyObject.__delete();
            tyObject.__deindex();
          }
        }
      }
    }
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        final var f2 = reader.fieldName();
        final var key = Integer.parseInt(f2);
        if (reader.testLackOfNull()) {
          var tyPrior = itemsByKey.get(key);
          if (tyPrior == null) {
            tyPrior = make(key);
          }
          tyPrior.__patch(reader);
          invalidatePrimaryKey(key, tyPrior);
        } else {
          var tyPrior = itemsByKey.get(key);
          if (tyPrior != null) {
            tyPrior.__delete();
            invalidatePrimaryKey(key, tyPrior);
          }
        }
      }
    }
  }

  @Override
  public void __revert() {
    if (__isDirty()) {
      // if we are commiting
      for (final Integer killKey : createdObjects.keySet()) {
        final var item = itemsByKey.remove(killKey);
        if (item != null) {
          item.__delete();
          item.__deindex();
        }
      }
      createdObjects.clear();
      for (final Map.Entry<Integer, Ty> entry : itemsByKey.entrySet()) {
        entry.getValue().__revert();
      }
      __lowerDirtyRevert();
    }
  }

  @Override
  public long __memory() {
    long sum = super.__memory() + 64 + className.length() * 2L + pubsub.__memory();
    if (indices != null) {
      for (ReactiveIndex<Ty> idx : indices) {
        sum += idx.memory();
      }
    }
    for (Ty value : itemsByKey.values()) {
      sum += value.__memory() + 20;
    }
    if (unknowns != null) {
      sum += unknowns.size() * 8L;
    }
    if (trackers != null) {
      for (DifferentialEdgeTracker<Ty> tracker : trackers) {
        sum += tracker.memory();
      }
    }
    return sum;
  }

  public Ty make(int key) {
    final var result = maker.apply(this);
    result.__setId(key, false);
    result.__subscribe(() -> {
      invalidatePrimaryKey(key, result);
      return RxTable.this.__raiseInvalid();
    });
    result.__raiseDirty();
    if (unknowns != null) {
      unknowns.add(result);
    }
    this.createdObjects.put(key, result);
    this.itemsByKey.put(key, result);
    result.__pumpIndexEvents(pubsub);
    __raiseDirty();
    invalidatePrimaryKey(key, result);
    return result;
  }

  @Override
  public boolean __raiseInvalid() {
    __invalidateSubscribers();
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }

  @Override
  public void __invalidateUp() {
    __raiseInvalid();
  }

  public Ty getById(final int id) {
    final var obj = itemsByKey.get(id);
    return obj;
  }

  public ReactiveIndex<Ty> getIndex(final short column) {
    return indices[column];
  }

  @Override
  public Iterator<Ty> iterator() {
    return itemsByKey.values().iterator();
  }

  public Ty make() {
    return make(document.__genNextAutoKey());
  }

  public Iterable<Ty> scan(final WhereClause<Ty> filter) {
    if (filter == null) {
      readAll();
      return this;
    }
    final var everything = new AtomicBoolean(false);
    final var union = new AtomicReference<TreeSet<Ty>>(null);
    filter.scopeByIndicies(new IndexQuerySet() {
      private TreeSet<Ty> current = null;
      boolean didIndexing = false;
      @Override
      public void intersect(int column, int value, LookupMode mode) {
        if (everything.get()) { // a prior branch requires everythig
          return;
        }
        if (mode == IndexQuerySet.LookupMode.Equals) {
          readIndex(column, value);
          didIndexing = true;
        }
        final var specific = indices[column].of(value, mode);
        if (specific == null) { // no index available
          current = new TreeSet<>();
          return;
        }
        if (current == null) {
          current = specific;
        } else {
          final var common = new TreeSet<Ty>();
          if (specific != null) {
            for (final Ty item : specific) {
              if (current.contains(item)) {
                common.add(item);
              }
            }
          }
          current = common;
        }
      }

      @Override
      public void primary(int value) {
        if (everything.get()) {
          // a prior branch requires everythig
          return;
        }
        Ty val = itemsByKey.get(value);
        readPrimaryKey(value);
        if (val != null) {
          if (current == null) {
            current = new TreeSet<>();
            current.add(val);
          } else {
            boolean keep = current.contains(val);
            current.clear();
            if (keep) {
              current.add(val);
            }
          }
        } else {
          current = new TreeSet<>();
        }
      }

      @Override
      public void push() {
        if (!everything.get()) {
          if (current != null) {
            if (union.get() == null) {
              union.set(current);
            } else {
              union.get().addAll(current);
            }
          } else {
            everything.set(true);
          }
        }
        current = null;
      }

      @Override
      public void finish() {
        push();
        if (!didIndexing) {
          readAll();
        }
      }
    });
    if (everything.get() || union.get() == null) {
      readAll();
      return this;
    }
    final var clone = new TreeSet<>(union.get());
    clone.addAll(unknowns);
    return clone;
  }

  public int size() {
    readAll();
    if (__isDirty()) {
      return iterate(true).size();
    } else {
      return itemsByKey.size();
    }
  }

  public void readAll() {
    if (activeGuard != null) {
      activeGuard.readAll();
    }
  }

  public NtMaybe<Ty> lookup(int pkey) {
    readPrimaryKey(pkey);
    return new NtMaybe<>(itemsByKey.get(pkey));
  }

  public NtMaybe<Ty> lookup(NtMaybe<Integer> pkey) {
    if (pkey.has()) {
      readPrimaryKey(pkey.get());
      return new NtMaybe<>(itemsByKey.get(pkey.get()));
    } else {
      return new NtMaybe<>();
    }
  }

  public NtList<NtMaybe<Ty>> lookup(NtList<Integer> pkeys) {
    ArrayList<NtMaybe<Ty>> result = new ArrayList<>();
    for (int pkey : pkeys) {
      readPrimaryKey(pkey);
      result.add(new NtMaybe<>(itemsByKey.get(pkey)));
    }
    if (result.size() == 0) {
      return new EmptyNtList<>();
    }
    return new ArrayNtList<>(result);
  }

  public void readPrimaryKey(int pkey) {
    if (activeGuard != null) {
      activeGuard.readPrimaryKey(pkey);
    }
  }

  public void readIndex(int index, int val) {
    if (activeGuard != null) {
      activeGuard.readIndexValue(index, val);
    }
  }

  public NtList<Ty> iterate(final boolean done) {
    if (done) {
      readAll();
    }
    return new SelectorRxObjectList<>(this);
  }

  public void __subscribe(final RxTableGuard g) {
    pubsub.subscribe(g);
  }

  public void pushGuard(RxTableGuard guard) {
    guardsInflight.push(guard);
    activeGuard = guard;
  }

  public void popGuard() {
    guardsInflight.pop();
    if (guardsInflight.empty()) {
      activeGuard = null;
    } else {
      activeGuard = guardsInflight.peek();
    }
  }

  @Override
  public void __settle(Set<Integer> viewers) {
    __lowerInvalid();
    for (Ty child : itemsByKey.values()) {
      child.__settle(viewers);
    }
    pubsub.settle();
  }

  @Override
  public void __reportRx(String name, JsonStreamWriter __writer) {
    int direct = __getSubscriberCount();
    int focused = pubsub.count();
    ArrayList<ObjectNode> children = new ArrayList<>();
    for (Map.Entry<Integer, Ty> entry : itemsByKey.entrySet()) {
      JsonStreamWriter childWriter = new JsonStreamWriter();
      entry.getValue().__writeRxReport(childWriter);
      children.add(Json.parseJsonObject(childWriter.toString()));
    }
    __writer.writeObjectFieldIntro(name);
    __writer.beginObject();
    __writer.writeObjectFieldIntro("subscribers");
    __writer.writeInteger(direct + focused);
    __writer.writeObjectFieldIntro("direct");
    __writer.writeInteger(direct);
    __writer.writeObjectFieldIntro("pubsub");
    __writer.writeInteger(focused);
    __writer.writeObjectFieldIntro("count");
    __writer.writeInteger(itemsByKey.size());
    if (children.size() > 0) {
      __writer.writeObjectFieldIntro("sum_rows");
      __writer.injectJson(JsonSum.sum(children).toString());
    }
    __writer.endObject();
  }
}
