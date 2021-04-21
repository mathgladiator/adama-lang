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

import java.util.function.Supplier;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;

public class DMaybe<dTy> {
  private dTy cache;

  public dTy get(final Supplier<dTy> maker) {
    if (cache == null) {
      cache = maker.get();
    }
    return cache;
  }

  public void hide(final PrivateLazyDeltaWriter writer) {
    if (cache != null) {
      writer.writeNull();
      cache = null;
    }
  }
}
