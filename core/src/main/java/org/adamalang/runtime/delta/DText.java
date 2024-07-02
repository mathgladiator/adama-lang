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
package org.adamalang.runtime.delta;

import org.adamalang.runtime.contracts.DeltaNode;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.text.RxText;
import org.adamalang.runtime.text.SeqString;

/** a document synchronized by deltas using CodeMirror's format */
public class DText implements DeltaNode {
  private boolean initialized;
  private int seq;
  private int gen;

  public DText() {
    initialized = false;
    seq = 0;
    gen = 0;
  }

  /** the string is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (initialized) {
      writer.writeNull();
      initialized = false;
      seq = 0;
      gen = 0;
    }
  }

  @Override
  public void clear() {
    initialized = false;
    seq = 0;
    gen = 0;
  }

  /** memory usage */
  @Override
  public long __memory() {
    return 64;
  }

  public void show(final RxText value, final PrivateLazyDeltaWriter writer) {
    if (value.current().gen != gen) {
      this.initialized = false;
      this.seq = 0;
      this.gen = value.current().gen;
    }
    PrivateLazyDeltaWriter obj = writer.planObject();
    if (initialized) {
      int start = seq;
      String change;
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
      initialized = true;
      SeqString val = value.current().get();
      obj.planField("$g").writeInt(gen);
      obj.planField("$i").writeString(val.value);
      obj.planField("$s").writeInt(val.seq);
      seq = val.seq;
    }
    obj.end();
  }
}
