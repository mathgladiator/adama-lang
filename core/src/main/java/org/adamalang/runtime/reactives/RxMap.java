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

import java.util.*;

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
  public void __cost(int cost) {
    if (__parent != null) {
      __parent.__cost(cost);
    }
  }

  @Override
  public void __settle(Set<Integer> viewers) {
    __lowerInvalid();
    for (RangeTy item : objects.storage.values()) {
      if (item instanceof RxParent) {
        ((RxParent) item).__settle(viewers);
      } else {
        return;
      }
    }
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
          var value = getOrCreateAndMaybeSignalDirty(key, false);
          value.__insert(reader);
          created.remove(key);
        } else {
          removeAndMaybeSignalDirty(key, false);
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
    return getOrCreateAndMaybeSignalDirty(key, true);
  }

  public RangeTy getOrCreateAndMaybeSignalDirty(DomainTy key, boolean signalDirty) {
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
    if (signalDirty) {
      __raiseDirty();
    }
    value = codec.make(this);
    objects.put(key, value);
    value.__subscribe(this);
    created.add(key);
    return value;
  }

  public boolean has(DomainTy key) {
    return objects.storage.containsKey(key);
  }

  public void remove(DomainTy key) {
    removeAndMaybeSignalDirty(key, true);
  }

  public void removeAndMaybeSignalDirty(DomainTy key, boolean signalDirty) {
    RangeTy value = objects.removeDirect(key);
    if (value != null) {
      if (!created.contains(key)) {
        if (signalDirty) {
          __raiseDirty();
        }
        deleted.put(key, value);
      }
    }
  }

  public void clear() {
    for (Map.Entry<DomainTy, RangeTy> entry : objects.storage.entrySet()) {
      deleted.put(entry.getKey(), entry.getValue());
    }
    __raiseDirty();
    objects.storage.clear();
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
