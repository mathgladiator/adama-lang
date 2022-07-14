/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.contracts.DeltaNode;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.text.RxText;
import org.adamalang.runtime.text.SeqString;

/** a document synchronized by deltas using CodeMirror's format */
public class DText implements DeltaNode {
  private boolean init;
  private int seq;
  private int gen;

  public DText() {
    init = false;
    seq = 0;
    gen = 0;
  }

  /** the string is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (init) {
      writer.writeNull();
      init = false;
      seq = 0;
      gen = 0;
    }
  }

  @Override
  public void clear() {
    init = false;
    seq = 0;
    gen = 0;
  }

  /** memory usage */
  @Override
  public long __memory() {
    return 1024;
  }

  public void show(final RxText value, final PrivateLazyDeltaWriter writer) {
    if (value.current().gen != gen) {
      this.init = false;
      this.seq = 0;
      this.gen = value.current().gen;
    }
    PrivateLazyDeltaWriter obj = writer.planObject();
    if (init) {
      int start = seq;
      String change = null;
      while ((change = value.current().changes.get(seq)) != null) {
        obj.planField("" + seq).writeString(change);
        seq++;
      }
      while ((change = value.current().uncommitedChanges.get(seq)) != null) {
        obj.planField("" + seq).writeString(change);
        seq++;
      }
      if (start != seq) {
        obj.planField("$s").writeInt(seq);
      }
    } else {
      init = true;
      SeqString val = value.current().get();
      obj.planField("$i").writeString(val.value);
      obj.planField("$s").writeInt(val.seq);
      seq = val.seq;
    }
    obj.end();
  }
}
