/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.adamalang.runtime.LivingDocument;
import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.index.ReactiveIndex;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.lists.SelectorRxObjectList;

/** a reactive table */
public class RxTable<Ty extends RxRecordBase<Ty>> extends RxBase implements Iterable<Ty>, RxParent, RxChild {
  private int autoKey;
  private int autoKeyBackup;
  private final LinkedHashMap<Integer, Ty> createdObjects;
  public final LivingDocument document;
  private final ReactiveIndex<Ty>[] indices;
  private final LinkedHashMap<Integer, Ty> itemsByKey;
  public final Function<RxParent, Ty> maker;
  public final String name;
  private final TreeSet<Ty> unknowns;

  public RxTable(final LivingDocument document, final RxParent owner, final String name, final Function<RxParent, Ty> maker, final int indicies) {
    super(owner);
    this.document = document;
    this.name = name;
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
    this.autoKey = 0;
    this.autoKeyBackup = this.autoKey;
    this.createdObjects = new LinkedHashMap<>();
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.beginObject();
      forwardDelta.writeObjectFieldIntro("auto_key");
      forwardDelta.writeInteger(this.autoKey);
      forwardDelta.writeObjectFieldIntro("rows");
      forwardDelta.beginObject();
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.beginObject();
      reverseDelta.writeObjectFieldIntro("auto_key");
      reverseDelta.writeInteger(this.autoKeyBackup);
      reverseDelta.writeObjectFieldIntro("rows");
      reverseDelta.beginObject();
      this.autoKeyBackup = this.autoKey;

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
            // lower the change flag; TODO: make this an explicit thing to save heap churn?
            row.__commit(key + "", new JsonStreamWriter(), new JsonStreamWriter());
          } else {
            row.__commit(key + "", forwardDelta, reverseDelta);
          }
        }
      }
      forwardDelta.endObject();
      forwardDelta.endObject();
      reverseDelta.endObject();
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
    writer.writeObjectFieldIntro("auto_key");
    writer.writeInteger(autoKey);
    writer.writeObjectFieldIntro("rows");
    writer.beginObject();
    for (final Map.Entry<Integer, Ty> entry : itemsByKey.entrySet()) {
      writer.writeObjectFieldIntro(entry.getKey());
      entry.getValue().__dump(writer);
    }
    writer.endObject();
    writer.endObject();
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        final var f1 = reader.fieldName();
        switch (f1) {
          case "auto_key":
            this.autoKey = reader.readInteger();
            this.autoKeyBackup = this.autoKey;
            break;
          case "rows":
            if (reader.startObject()) {
              while (reader.notEndOfObject()) {
                final var f2 = reader.fieldName();
                if (reader.testLackOfNull()) {
                  final var key = Integer.parseInt(f2);
                  final var tyPrior = itemsByKey.get(key);
                  if (tyPrior == null) {
                    final var tyObj = maker.apply(this);
                    tyObj.__setId(key, true);
                    tyObj.__insert(reader);
                    itemsByKey.put(key, tyObj);
                    if (unknowns != null) {
                      tyObj.__reindex();
                    }
                    tyObj.__subscribe(this);
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
            break;
          default:
            reader.skipValue();
        }
      }
    }
  }

  @Override
  public boolean __raiseInvalid() {
    __raiseDirty();
    return true;
  }

  @Override
  public void __revert() {
    if (__isDirty()) {
      // if we are commiting
      this.autoKey = this.autoKeyBackup;
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

  public Ty getById(final int id) {
    final var obj = itemsByKey.get(id);
    if (obj != null) { return obj; }
    return null;
  }

  public ReactiveIndex<Ty> getIndex(final short column) {
    return indices[column];
  }

  public NtList<Ty> iterate(final boolean done) {
    return new SelectorRxObjectList<>(this);
  }

  @Override
  public Iterator<Ty> iterator() {
    return itemsByKey.values().iterator();
  }

  public Ty make() {
    final var key = autoKey;
    autoKey++;
    final var result = maker.apply(this);
    result.__setId(key, false);
    result.__subscribe(this);
    result.__raiseDirty();
    if (unknowns != null) {
      unknowns.add(result);
    }
    this.createdObjects.put(key, result);
    this.itemsByKey.put(key, result);
    __raiseDirty();
    return result;
  }

  public Iterable<Ty> scan(final WhereClause<Ty> filter) {
    if (filter == null) { return this; }
    final var prior = new AtomicReference<TreeSet<Ty>>(null);
    filter.scopeByIndicies((column, value) -> {
      final var specific = indices[column].of(value);
      if (specific == null) { // no index available
        prior.set(new TreeSet<>());
        return;
      }
      if (prior.get() == null) {
        // just use the index
        prior.set(specific);
      } else {
        final var common = new TreeSet<Ty>();
        if (specific != null) {
          for (final Ty item : specific) {
            if (prior.get().contains(item)) {
              common.add(item);
            }
          }
        }
        prior.set(common);
      }
    });
    if (prior.get() == null) { return this; }
    final var clone = new TreeSet<>(prior.get());
    clone.addAll(unknowns);
    return clone;
  }

  public int size() {
    if (__isDirty()) {
      return iterate(true).size();
    } else {
      return itemsByKey.size();
    }
  }
}
