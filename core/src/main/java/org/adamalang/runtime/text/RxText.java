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
      value.changes.put(change.getKey(), change.getValue());
    }
    value.uncommitedChanges.clear();
    forwardDelta.endObject();
    forwardDelta.endObject();
    reverseDelta.endObject();
    reverseDelta.endObject();
  }

  private void __commitFullDiff(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    forwardDelta.writeObjectFieldIntro(name);
    forwardDelta.beginObject();

    forwardDelta.writeObjectFieldIntro("fragments");
    forwardDelta.beginObject();
    // for each NEW, add it; for each change, show it; for each removal, null it out
    forwardDelta.endObject();

    forwardDelta.writeObjectFieldIntro("order");
    forwardDelta.beginObject();
    forwardDelta.endObject();

    forwardDelta.writeObjectFieldIntro("changes");
    forwardDelta.beginObject();
    forwardDelta.endObject();

    forwardDelta.endObject();
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

  public void change(int seq, NtDynamic changes) {
    value.change(seq, changes);
    __raiseDirty();
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
