/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.reactives;

import org.adamalang.common.SlashStringArrayEncoder;
import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxKillable;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtMap;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.NtPair;
import org.adamalang.runtime.natives.NtPrincipal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** a reactive map */
public class RxMap<DomainTy, RangeTy extends RxBase> extends RxBase implements Iterable<NtPair<DomainTy, RangeTy>>, RxParent, RxChild, RxKillable {
  public final Codec<DomainTy, RangeTy> codec;
  public final LinkedHashMap<DomainTy, RangeTy> deleted;
  public final HashSet<DomainTy> created;
  private final NtMap<DomainTy, RangeTy> objects;

  public RxMap(final RxParent owner, final Codec<DomainTy, RangeTy> codec) {
    super(owner);
    this.codec = codec;
    this.objects = new NtMap<>();
    this.deleted = new LinkedHashMap<>();
    this.created = new HashSet<>();
  }

  @Override
  public boolean __isAlive() {
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.beginObject();
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.beginObject();
      for (final Map.Entry<DomainTy, RangeTy> entry : deleted.entrySet()) {
        if (entry.getValue() instanceof RxKillable) {
          ((RxKillable) entry.getValue()).__kill();
        }
        String key = codec.toStr(entry.getKey());
        final var value = entry.getValue();
        forwardDelta.writeObjectFieldIntro(key);
        forwardDelta.writeNull();
        reverseDelta.writeObjectFieldIntro(key);
        value.__dump(reverseDelta);
      }

      for (final Map.Entry<DomainTy, RangeTy> entry : objects.entries()) {
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
    for (final Map.Entry<DomainTy, RangeTy> entry : objects.entries()) {
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

  @Override
  public void __revert() {
    if (__isDirty()) {
      for (final Map.Entry<DomainTy, RangeTy> entry : deleted.entrySet()) {
        objects.put(entry.getKey(), entry.getValue());
      }
      for (final DomainTy axe : created) {
        objects.removeDirect(axe);
      }
      for (final Map.Entry<DomainTy, RangeTy> entry : objects.entries()) {
        entry.getValue().__revert();
      }
      created.clear();
      deleted.clear();
      __lowerDirtyRevert();
    }
  }

  @Override
  public long __memory() {
    long sum = super.__memory() + 128;
    for (Map.Entry<DomainTy, RangeTy> entry : objects.entries()) {
      sum += entry.getValue().__memory() + 20;
      if (entry.getKey() instanceof String) {
        sum += ((String) entry.getKey()).length() * 2L;
      }
    }
    return sum;
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
    RangeTy value = objects.removeDirect(key);
    if (value != null) {
      if (!created.contains(key)) {
        __raiseDirty();
        deleted.put(key, value);
      }
    }
  }

  @Override
  public void __kill() {
    for (final Map.Entry<DomainTy, RangeTy> entry : objects.entries()) {
      if (entry.getValue() instanceof RxKillable) {
        ((RxKillable) entry.getValue()).__kill();
      }
    }
  }

  public NtMaybe<RangeTy> lookup(DomainTy key) {
    return new NtMaybe<>(objects.get(key));
  }

  @Override
  public boolean __raiseInvalid() {
    __raiseDirty();
    return true;
  }

  @Override
  public Iterator<NtPair<DomainTy, RangeTy>> iterator() {
    return objects.iterator();
  }

  public int size() {
    return objects.size();
  }

  public NtMaybe<NtPair<DomainTy, RangeTy>> min() {
    return objects.min();
  }

  public NtMaybe<NtPair<DomainTy, RangeTy>> max() {
    return objects.max();
  }

  public interface Codec<DomainTy, RangeTy extends RxBase> {
    RangeTy make(RxParent maker);

    String toStr(DomainTy key);

    DomainTy fromStr(String key);
  }

  public abstract static class IntegerCodec<R extends RxBase> implements Codec<Integer, R> {
    public String toStr(Integer key) {
      return "" + key;
    }

    public Integer fromStr(String key) {
      return Integer.parseInt(key);
    }
  }

  public abstract static class LongCodec<R extends RxBase> implements Codec<Long, R> {
    public String toStr(Long key) {
      return "" + key;
    }

    public Long fromStr(String key) {
      return Long.parseLong(key);
    }
  }

  public abstract static class StringCodec<R extends RxBase> implements Codec<String, R> {
    public String toStr(String key) {
      return key;
    }

    public String fromStr(String key) {
      return key;
    }
  }

  public abstract static class PrincipalCodec<R extends RxBase> implements Codec<NtPrincipal, R> {
    public String toStr(NtPrincipal key) {
      return SlashStringArrayEncoder.encode(key.agent, key.authority);
    }

    public NtPrincipal fromStr(String key) {
      String[] parts = SlashStringArrayEncoder.decode(key);
      return new NtPrincipal(parts[0], parts[1]);
    }
  }
}
