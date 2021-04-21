/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.delta;

import java.util.ArrayList;
import java.util.function.Supplier;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;

public class DList<dTy> {
  public final ArrayList<dTy> cachedDeltas;
  private int emittedSize;

  public DList() {
    this.cachedDeltas = new ArrayList<>();
    this.emittedSize = 0;
  }

  public dTy getPrior(final int k, final Supplier<dTy> maker) {
    while (cachedDeltas.size() <= k) {
      cachedDeltas.add(maker.get());
    }
    return cachedDeltas.get(k);
  }

  public void hide(final PrivateLazyDeltaWriter writer) {
    if (emittedSize > 0) {
      emittedSize = 0;
      cachedDeltas.clear();
      writer.writeNull();
    }
  }

  public void rectify(final int size, final PrivateLazyDeltaWriter writer) {
    for (var k = size; k < cachedDeltas.size(); k++) {
      writer.planField("" + k).writeNull();
    }
    if (emittedSize != cachedDeltas.size()) {
      writer.planField("@s").writeInt(size);
      emittedSize = cachedDeltas.size();
    }
  }
}
