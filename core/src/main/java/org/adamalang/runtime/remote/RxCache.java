/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.remote;

import org.adamalang.runtime.contracts.RxKillable;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtResult;
import org.adamalang.runtime.natives.NtToDynamic;
import org.adamalang.runtime.reactives.RxBase;
import org.adamalang.runtime.sys.LivingDocument;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/** a cache of service calls */
public class RxCache extends RxBase implements RxKillable {
  private final LivingDocument root;
  private final TreeMap<RemoteInvocation, RemoteSite> mapping;
  private final TreeMap<Integer, RemoteSite> additions;
  private final TreeMap<Integer, RemoteSite> sites;
  private final TreeMap<Integer, RemoteSite> removals;
  private final HashSet<Integer> keep;

  public RxCache(LivingDocument __root, RxParent __parent) {
    super(__parent);
    this.root = __root;
    this.sites = new TreeMap<>();
    this.mapping = new TreeMap<>();
    this.removals = new TreeMap<>();
    this.additions = new TreeMap<>();
    this.keep = new HashSet<>();
  }

  /** wrap a supplier to monitor the cache and keep the cache clean */
  public <Tx> Supplier<Tx> wrap(Supplier<Tx> supplier) {
    return () -> {
      keep.clear();
      Tx result = supplier.get();
      {
        ArrayList<Integer> axe = new ArrayList<>();
        for (Map.Entry<Integer, RemoteSite> entry : sites.entrySet()) {
          int id = entry.getValue().id;
          if (!keep.contains(id)) {
            axe.add(id);
          }
        }
        for (int id : axe) {
          removals.put(id, sites.remove(id));
        }
      }
      return result;
    };
  }

  /** try to answer a service request against the cache, and emit an execution if we need to do some work */
  public <Tx> NtResult<Tx> answer(String service, String method, NtPrincipal who, NtToDynamic request, Function<String, Tx> parser, BiFunction<Integer, String, RemoteResult> execute) {
    // create the invocation
    String parameters = request.to_dynamic().json;
    RemoteInvocation invocation = new RemoteInvocation(service, method, who, parameters);

    // see if we have the invocation available
    RemoteSite site = mapping.get(invocation);

    // we don't, so let's create it
    if (site == null) {
      // ask the document for a document unique id
      int id = root.__createRouteId();
      site = new RemoteSite(id, invocation);
      additions.put(id, site);
      mapping.put(site.invocation(), site);
      __raiseDirty();
    }
    keep.add(site.id);

    // create the result
    NtResult<Tx> result = site.of(parser);

    // if not done
    if (!result.finished()) {
      // and we don't have a route established, then execute the request
      if (!root.__isRouteInflight(site.id)) {
        root.__bindRoute(site.id, this);
        RemoteResult instant = execute.apply(site.id, parameters);
        if (instant != null) {
          root.__removeRoute(site.id);
          site.deliver(instant);
          __raiseDirty();
          result = site.of(parser);
        }
      }
    }
    return result;
  }

  /** deliver a result to the cache */
  public boolean deliver(int id, RemoteResult result) {
    RemoteSite site = sites.get(id);
    if (site == null) {
      site = additions.get(id);
    }
    if (site != null) {
      root.__removeRoute(id);
      site.deliver(result);
      __raiseDirty();
      return true;
    }
    return false;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.beginObject();
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.beginObject();

      for (Map.Entry<Integer, RemoteSite> entry : additions.entrySet()) {
        forwardDelta.writeObjectFieldIntro(entry.getKey());
        entry.getValue().dump(forwardDelta);
        reverseDelta.writeObjectFieldIntro(entry.getKey());
        reverseDelta.writeNull();
      }
      for (Map.Entry<Integer, RemoteSite> entry : sites.entrySet()) {
        if (entry.getValue().shouldCommit()) {
          forwardDelta.writeObjectFieldIntro(entry.getKey());
          entry.getValue().writeValue(forwardDelta);
          reverseDelta.writeObjectFieldIntro(entry.getKey());
          entry.getValue().writeBackup(reverseDelta);
          entry.getValue().commit();
        }
      }
      sites.putAll(additions);
      additions.clear();
      for (Map.Entry<Integer, RemoteSite> entry : removals.entrySet()) {
        forwardDelta.writeObjectFieldIntro(entry.getKey());
        forwardDelta.writeNull();
        reverseDelta.writeObjectFieldIntro(entry.getKey());
        entry.getValue().dump(reverseDelta);
        sites.remove(entry.getKey());
      }
      removals.clear();
      forwardDelta.endObject();
      reverseDelta.endObject();
      __lowerDirtyCommit();
      index();
    }
  }

  private void index() {
    mapping.clear();
    for (Map.Entry<Integer, RemoteSite> entry : sites.entrySet()) {
      mapping.put(entry.getValue().invocation(), entry.getValue());
    }
    for (Map.Entry<Integer, RemoteSite> entry : additions.entrySet()) {
      mapping.put(entry.getValue().invocation(), entry.getValue());
    }
  }

  @Override
  public void __dump(JsonStreamWriter writer) {
    writer.beginObject();
    for (Map.Entry<Integer, RemoteSite> entry : sites.entrySet()) {
      writer.writeObjectFieldIntro(entry.getKey());
      entry.getValue().dump(writer);
    }
    writer.endObject();
  }

  @Override
  public void __insert(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        int key = Integer.parseInt(reader.fieldName());
        if (reader.testLackOfNull()) {
          RemoteSite prior = sites.get(key);
          if (prior != null) {
            prior.patch(reader);
          } else {
            sites.put(key, new RemoteSite(key, reader));
          }
        } else {
          sites.remove(key);
        }
      }
      index();
    }
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    boolean dirty = false;
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        int key = Integer.parseInt(reader.fieldName());
        RemoteSite prior = sites.get(key);
        if (prior != null) {
          if (reader.testLackOfNull()) {
            prior.patch(reader);
            dirty = true;
          } else {
            sites.remove(key);
            removals.put(key, prior);
            dirty = true;
          }
        } else {
          if (reader.testLackOfNull()) {
            additions.put(key, new RemoteSite(key, reader));
            dirty = true;
          } // otherwise, nothing
        }
      }
      if (dirty) {
        __raiseDirty();
      }
      index();
    }
  }

  @Override
  public void __revert() {
    if (__isDirty()) {
      additions.clear();
      sites.putAll(removals);
      removals.clear();
      for (Map.Entry<Integer, RemoteSite> entry : sites.entrySet()) {
        entry.getValue().revert();
      }
      __lowerDirtyRevert();
    }
  }

  @Override
  public void __kill() {
  }

  public void clear() {
    additions.clear();
    removals.putAll(sites);
    sites.clear();
    if (removals.size() > 0) {
      __raiseDirty();
    }
  }
}
