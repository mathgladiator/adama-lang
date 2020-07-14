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
import org.adamalang.runtime.LivingDocument;
import org.adamalang.runtime.bridges.NativeBridge;
import org.adamalang.runtime.bridges.RecordBridge;
import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.index.ReactiveIndex;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.lists.SelectorRxObjectList;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** a reactive table */
public class RxTable<Ty extends RxRecordBase<Ty>> extends RxBase implements Iterable<Ty>, RxParent, RxChild {
  private int autoKey;
  private int autoKeyBackup;
  public final RecordBridge<Ty> bridge;
  private final LinkedHashMap<Integer, Ty> createdObjects;
  private final TreeSet<Ty> dirtySet;
  public final LivingDocument document;
  private final ReactiveIndex<Ty>[] indices;
  private final LinkedHashMap<Integer, Ty> itemsByKey;
  public final String name;
  private final RxParent owner;
  private final TreeSet<Ty> unknowns;

  public RxTable(final LivingDocument document, final String name, final ObjectNode node, final RxParent owner, final RecordBridge<Ty> bridge) {
    super(owner);
    this.document = document;
    this.name = name;
    this.owner = owner;
    this.bridge = bridge;
    if (bridge.getNumberColumns() == 0) {
      this.indices = null;
      this.unknowns = null;
    } else {
      this.indices = new ReactiveIndex[bridge.getNumberColumns()];
      this.unknowns = new TreeSet<>();
      for (var k = 0; k < bridge.getNumberColumns(); k++) {
        this.indices[k] = new ReactiveIndex<>(unknowns);
      }
    }
    // check if we have rows; make sure we link into the JSON tree
    this.itemsByKey = new LinkedHashMap<>();
    this.autoKey = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(node, "auto_key");
    this.autoKeyBackup = this.autoKey;
    this.createdObjects = new LinkedHashMap<>();
    this.dirtySet = new TreeSet<>();
    if (node.has("rows")) {
      // ingest all prior data into memory
      final var it = node.get("rows").fields();
      while (it.hasNext()) {
        final var entry = it.next();
        final var itemNode = (ObjectNode) entry.getValue();
        itemNode.put("id", entry.getKey());
        final var tyObj = bridge.construct(itemNode, this);
        itemsByKey.put(Integer.parseInt(entry.getKey()), tyObj);
        if (unknowns != null) {
          tyObj.__reindex();
        }
        tyObj.__subscribe(this);
      }
    }
  }

  @Override
  public void __commit(final String name, final ObjectNode deltaParent) {
    if (__isDirty()) {
      final var delta = deltaParent.putObject(name);
      delta.put("auto_key", this.autoKey);
      final var rowsDelta = delta.putObject("rows");
      this.autoKeyBackup = this.autoKey;
      final var keysToKill = new ArrayList<Integer>();
      for (final Map.Entry<Integer, Ty> entry : itemsByKey.entrySet()) {
        final int key = entry.getKey();
        final var row = entry.getValue();
        if (row.__isDying()) {
          if (!createdObjects.containsKey(key)) {
            rowsDelta.putNull(key + "");
          }
          row.__kill();
          keysToKill.add(key);
        } else if (row.__isDirty()) {
          row.__commit(key + "", rowsDelta);
        }
      }
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
    return new SelectorRxObjectList<>(this, bridge);
  }

  @Override
  public Iterator<Ty> iterator() {
    return itemsByKey.values().iterator();
  }

  public Ty make() {
    final var key = autoKey;
    autoKey++;
    final var row = Utility.createObjectNode();
    row.put("id", key);
    final var result = bridge.construct(row, this);
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
