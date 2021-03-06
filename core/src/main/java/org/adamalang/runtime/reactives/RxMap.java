/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.reactives;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.adamalang.runtime.LivingDocument;
import org.adamalang.runtime.contracts.CanGetAndSet;
import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.index.ReactiveIndex;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.lists.SelectorRxObjectList;

/** a reactive map */
public class RxMap<DomainTy, RangeTy extends RxBase> extends RxBase implements Iterable<Map.Entry<DomainTy, RangeTy>>, RxParent, RxChild {
  private final LinkedHashMap<DomainTy, RangeTy> objects;
  public final Codec<DomainTy, RangeTy> codec;
  public final LinkedHashMap<DomainTy, RangeTy> deleted;
  public final HashSet<DomainTy> created;

  public RxMap(final RxParent owner, final Codec<DomainTy, RangeTy> codec) {
    super(owner);
    this.codec = codec;
    this.objects = new LinkedHashMap<>();
    this.deleted = new LinkedHashMap<>();
    this.created = new HashSet<>();
  }

  public static interface Codec<DomainTy, RangeTy extends RxBase> {
    public RangeTy make(RxParent maker);

    public String toStr(DomainTy key);

    public DomainTy fromStr(String key);
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.beginObject();
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.beginObject();
      for (final Map.Entry<DomainTy, RangeTy> entry : deleted.entrySet()) {
        String key = codec.toStr(entry.getKey());
        final var value = entry.getValue();
        forwardDelta.writeObjectFieldIntro(key);
        forwardDelta.writeNull();
        reverseDelta.writeObjectFieldIntro(key);
        value.__dump(reverseDelta);
      }

      for (final Map.Entry<DomainTy, RangeTy> entry : objects.entrySet()) {
        String key = codec.toStr(entry.getKey());
        final var value = entry.getValue();
        if (created.contains(entry.getKey())) {
          value.__commit(key, forwardDelta, new JsonStreamWriter());
          reverseDelta.writeObjectFieldIntro(key);
          reverseDelta.writeNull();
        } else {
          value.__commit(key, forwardDelta, reverseDelta);
        }
      }
      created.clear();
      deleted.clear();
      forwardDelta.endObject();
      reverseDelta.endObject();
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.beginObject();
    for (final Map.Entry<DomainTy, RangeTy> entry : objects.entrySet()) {
      writer.writeObjectFieldIntro(codec.toStr(entry.getKey()));
      entry.getValue().__dump(writer);
    }
    writer.endObject();
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        final var key = codec.fromStr(reader.fieldName());
        if (reader.testLackOfNull()) {
          var value = getOrCreate(key); // TODO: this leaks dirty
          value.__insert(reader);
          created.remove(key);
        } else {
          remove(key); // TODO: this may cause an excess dirty
        }
      }
    }
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        final var key = codec.fromStr(reader.fieldName());
        if (reader.testLackOfNull()) {
          getOrCreate(key).__patch(reader);
        } else {
          remove(key);
        }
      }
    }
  }


  public NtMaybe<RangeTy> lookup(DomainTy key) {
    return new NtMaybe<>(objects.get(key));
  }

  public RangeTy getOrCreate(DomainTy key) {
    RangeTy value = objects.get(key);
    if (value != null) {
      return value;
    }
    value = deleted.remove(key);
    if (value != null) {
      __raiseDirty();
      objects.put(key, value);
      return value;
    }
    __raiseDirty();
    value = codec.make(this);
    objects.put(key, value);
    value.__subscribe(this);
    created.add(key);
    return value;
  }

  public void remove(DomainTy key) {
    RangeTy value = objects.remove(key);
    if (value != null) {
      if (!created.contains(key)) {
        __raiseDirty();
        deleted.put(key, value);
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
      for (final Map.Entry<DomainTy, RangeTy> entry : deleted.entrySet()) {
        objects.put(entry.getKey(), entry.getValue());
      }
      for (final DomainTy axe : created) {
        objects.remove(axe);
      }
      for (final Map.Entry<DomainTy, RangeTy> entry : objects.entrySet()) {
        entry.getValue().__revert();
      }
      created.clear();
      deleted.clear();
      __lowerDirtyRevert();
    }
  }

  @Override
  public Iterator<Map.Entry<DomainTy, RangeTy>> iterator() {
    return objects.entrySet().iterator();
  }

  public int size() {
    return objects.size();
  }

  public static abstract class IntegerCodec<R extends RxBase> implements Codec<Integer, R> {
    public String toStr(Integer key) {
      return "" + key;
    }

    public Integer fromStr(String key) {
      return Integer.parseInt(key);
    }
  }

  public static abstract class LongCodec<R extends RxBase> implements Codec<Long, R> {
    public String toStr(Long key) {
      return "" + key;
    }

    public Long fromStr(String key) {
      return Long.parseLong(key);
    }
  }

  public static abstract class StringCodec<R extends RxBase> implements Codec<String, R> {
    public String toStr(String key) {
      return key;
    }

    public String fromStr(String key) {
      return key;
    }
  }
}
