/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.remote;

import org.adamalang.runtime.contracts.RxKillable;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.reactives.RxBase;
import org.adamalang.runtime.sys.LivingDocument;

import java.util.Map;
import java.util.TreeMap;

/** a cache of service calls */
public class RxCache extends RxBase implements RxKillable {
  private final LivingDocument root;
  private final TreeMap<RemoteInvocation, RemoteSite> cache;
  private final TreeMap<Integer, RemoteSite> additions;
  private final TreeMap<Integer, RemoteSite> sites;
  private final TreeMap<Integer, RemoteSite> removals;

  public RxCache(LivingDocument __root, RxParent __parent) {
    super(__parent);
    this.root = __root;
    this.sites = new TreeMap<>();
    this.cache = new TreeMap<>();
    this.removals = new TreeMap<>();
    this.additions = new TreeMap<>();
  }

  public int bind(RemoteInvocation invocation) {
    int id = root.__bindRoute(this);
    additions.put(id, new RemoteSite(invocation));
    __raiseDirty();
    return id;
  }

  public boolean deliver(int id, RemoteResult result) {
    RemoteSite site = sites.get(id);
    if (site != null) {
      site.deliver(result);
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
      for (Map.Entry<Integer, RemoteSite> entry : removals.entrySet()) {
        forwardDelta.writeObjectFieldIntro(entry.getKey());
        forwardDelta.writeNull();
        reverseDelta.writeObjectFieldIntro(entry.getKey());
        entry.getValue().dump(forwardDelta);
        sites.remove(entry.getKey());
      }
      forwardDelta.endObject();
      reverseDelta.endObject();
      __lowerDirtyCommit();
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
        sites.put(key, new RemoteSite(reader));
      }
    }
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        int key = Integer.parseInt(reader.fieldName());
        RemoteSite prior = sites.get(key);
        if (prior != null) {
          if (reader.testLackOfNull()) {
            prior.patch(reader);
          } else {
            sites.remove(key);
            removals.put(key, prior);
          }
        } else {
          if (reader.testLackOfNull()) {
            additions.put(key, new RemoteSite(reader));
          } // otherwise, nothing
        }
      }
    }
  }

  @Override
  public void __kill() {
  }

  public void clear() {
    removals.putAll(sites);
    sites.clear();
  }
}
