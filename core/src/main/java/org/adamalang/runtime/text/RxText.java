/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.text;

import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.reactives.RxBase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/** a text field within a document */
public class RxText extends RxBase {
  private Text backup;
  private Text value;

  public RxText(final RxParent parent, final Text value) {
    super(parent);
    this.backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      if (backup == value && value.uncommitedChanges.size() > 0 && !value.upgraded) {
        __commitJustChanges(name, forwardDelta, reverseDelta);
      } else {
        __commitFullDiff(name, forwardDelta, reverseDelta);
      }
      backup = value;
      __lowerDirtyCommit();
    }
  }

  private void __commitJustChanges(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    forwardDelta.writeObjectFieldIntro(name);
    forwardDelta.beginObject();
    forwardDelta.writeObjectFieldIntro("changes");
    forwardDelta.beginObject();
    reverseDelta.writeObjectFieldIntro(name);
    reverseDelta.beginObject();
    reverseDelta.writeObjectFieldIntro("changes");
    reverseDelta.beginObject();
    for (Map.Entry<Integer, String> change : value.uncommitedChanges.entrySet()) {
      forwardDelta.writeObjectFieldIntro(change.getKey());
      forwardDelta.injectJson(change.getValue());
      reverseDelta.writeObjectFieldIntro(change.getKey());
      reverseDelta.writeNull();
    }
    value.commit();
    forwardDelta.endObject();
    forwardDelta.endObject();
    reverseDelta.endObject();
    reverseDelta.endObject();
  }

  private static <T> void __commitDiffMap(HashMap<T, String> prior, HashMap<T, String> next, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    HashSet<T> nuke = new HashSet<>(prior.keySet());
    for (Map.Entry<T, String> entryNew : next.entrySet()) {
      nuke.remove(entryNew.getKey());
      String old = prior.get(entryNew.getKey());
      if (!entryNew.getValue().equals(old)) {
        forwardDelta.writeObjectFieldIntro(entryNew.getKey());
        forwardDelta.writeString(entryNew.getValue());
        reverseDelta.writeObjectFieldIntro(entryNew.getKey());
        if (old == null) {
          reverseDelta.writeNull();
        } else {
          reverseDelta.writeString(old);
        }
      }
    }
    for (T nukeKey : nuke) {
      forwardDelta.writeObjectFieldIntro(nukeKey);
      forwardDelta.writeNull();
      reverseDelta.writeObjectFieldIntro(nukeKey);
      reverseDelta.writeString(prior.get(nukeKey));
    }
  }

  private void __commitFullDiff(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    forwardDelta.writeObjectFieldIntro(name);
    forwardDelta.beginObject();
    reverseDelta.writeObjectFieldIntro(name);
    reverseDelta.beginObject();

    forwardDelta.writeObjectFieldIntro("fragments");
    forwardDelta.beginObject();
    reverseDelta.writeObjectFieldIntro("fragments");
    reverseDelta.beginObject();
    __commitDiffMap(backup.fragments, value.fragments, forwardDelta, reverseDelta);
    forwardDelta.endObject();
    reverseDelta.endObject();

    forwardDelta.writeObjectFieldIntro("order");
    forwardDelta.beginObject();
    reverseDelta.writeObjectFieldIntro("order");
    reverseDelta.beginObject();
    __commitDiffMap(backup.order, value.order, forwardDelta, reverseDelta);
    forwardDelta.endObject();
    reverseDelta.endObject();

    forwardDelta.writeObjectFieldIntro("changes");
    forwardDelta.beginObject();
    reverseDelta.writeObjectFieldIntro("changes");
    reverseDelta.beginObject();
    __commitDiffMap(backup.changes, value.changes, forwardDelta, reverseDelta);
    forwardDelta.endObject();
    reverseDelta.endObject();

    if (backup.seq != value.seq) {
      forwardDelta.writeObjectFieldIntro("seq");
      forwardDelta.writeInteger(value.seq);
      reverseDelta.writeObjectFieldIntro("seq");
      forwardDelta.writeInteger(backup.seq);
    }
    forwardDelta.endObject();

    value.commit();
    backup = value;
  }

  @Override
  public void __dump(JsonStreamWriter writer) {
    value.write(writer);
  }

  @Override
  public void __insert(JsonStreamReader reader) {
    backup = new Text(reader);
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    value = new Text(reader);
  }

  @Override
  public void __revert() {
    if (__isDirty()) {
      value = backup;
      __lowerDirtyRevert();
    }
  }

  public boolean change(int seq, NtDynamic changes) {
    if (value.change(seq, changes.json)) {
      __raiseDirty();
      return true;
    }
    return false;
  }

  public void set(String str) {
    if (value == backup) {
      value = new Text(backup);
    }
    this.value.set(str);
    __raiseDirty();
  }

  public String get() {
    return value.get();
  }
}
